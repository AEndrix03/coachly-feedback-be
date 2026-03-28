package com.coachly.feedback.poll.application;

import com.coachly.feedback.common.exception.BadRequestException;
import com.coachly.feedback.common.exception.ConflictException;
import com.coachly.feedback.common.exception.NotFoundException;
import com.coachly.feedback.common.model.DomainEventPublisher;
import com.coachly.feedback.common.model.PollStatus;
import com.coachly.feedback.common.security.AuthorizationService;
import com.coachly.feedback.common.security.RequestUserContext;
import com.coachly.feedback.common.security.RequestUserContextResolver;
import com.coachly.feedback.common.time.TimeProvider;
import com.coachly.feedback.poll.api.CreatePollRequest;
import com.coachly.feedback.poll.api.PollResponseDto;
import com.coachly.feedback.poll.api.PollResponseRequest;
import com.coachly.feedback.poll.api.PollResultResponse;
import com.coachly.feedback.poll.domain.Poll;
import com.coachly.feedback.poll.domain.PollAnsweredEvent;
import com.coachly.feedback.poll.domain.PollOption;
import com.coachly.feedback.poll.domain.PollResponse;
import com.coachly.feedback.poll.domain.PollResponseOption;
import com.coachly.feedback.poll.infrastructure.PollOptionRepository;
import com.coachly.feedback.poll.infrastructure.PollRepository;
import com.coachly.feedback.poll.infrastructure.PollResponseOptionRepository;
import com.coachly.feedback.poll.infrastructure.PollResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollResponseRepository pollResponseRepository;
    private final PollResponseOptionRepository pollResponseOptionRepository;
    private final RequestUserContextResolver contextResolver;
    private final AuthorizationService authorizationService;
    private final TimeProvider timeProvider;
    private final DomainEventPublisher eventPublisher;

    public PollService(PollRepository pollRepository,
                       PollOptionRepository pollOptionRepository,
                       PollResponseRepository pollResponseRepository,
                       PollResponseOptionRepository pollResponseOptionRepository,
                       RequestUserContextResolver contextResolver,
                       AuthorizationService authorizationService,
                       TimeProvider timeProvider,
                       DomainEventPublisher eventPublisher) {
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.pollResponseRepository = pollResponseRepository;
        this.pollResponseOptionRepository = pollResponseOptionRepository;
        this.contextResolver = contextResolver;
        this.authorizationService = authorizationService;
        this.timeProvider = timeProvider;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PollResponseDto create(CreatePollRequest request) {
        RequestUserContext context = contextResolver.getRequiredContext();
        authorizationService.requireAdmin(context);
        if (request.closesAt().isBefore(request.opensAt())) {
            throw new BadRequestException("POLL_WINDOW_INVALID", "closesAt must be greater than opensAt");
        }

        Poll poll = new Poll();
        poll.setTitle(request.title());
        poll.setDescription(request.description());
        poll.setType(request.type());
        poll.setMultipleChoice(request.multipleChoice());
        poll.setAnonymousResults(request.anonymousResults());
        poll.setOpensAt(request.opensAt());
        poll.setClosesAt(request.closesAt());
        poll.setCreatedByUserId(context.userId());

        Poll savedPoll = pollRepository.save(poll);
        for (int i = 0; i < request.options().size(); i++) {
            PollOption option = new PollOption();
            option.setPollId(savedPoll.getId());
            option.setLabel(request.options().get(i));
            option.setPosition(i);
            pollOptionRepository.save(option);
        }
        return toDto(savedPoll);
    }

    @Transactional(readOnly = true)
    public List<PollResponseDto> listVisible() {
        return pollRepository.findByStatusIn(List.of(PollStatus.PUBLISHED, PollStatus.CLOSED))
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PollResponseDto get(UUID pollId) {
        return toDto(findPoll(pollId));
    }

    @Transactional
    public void answer(UUID pollId, PollResponseRequest request) {
        RequestUserContext context = contextResolver.getRequiredContext();
        Poll poll = findPoll(pollId);
        validatePollOpen(poll);
        pollResponseRepository.findByPollIdAndUserId(pollId, context.userId())
                .ifPresent(existing -> { throw new ConflictException("POLL_ALREADY_ANSWERED", "User has already answered this poll"); });

        List<UUID> optionIds = request.optionIds();
        if (!poll.isMultipleChoice() && optionIds.size() != 1) {
            throw new BadRequestException("POLL_SINGLE_CHOICE_REQUIRED", "Only one option is allowed");
        }

        for (UUID optionId : optionIds) {
            if (!pollOptionRepository.existsByIdAndPollId(optionId, pollId)) {
                throw new BadRequestException("POLL_OPTION_INVALID", "One or more option ids are invalid");
            }
        }

        PollResponse response = new PollResponse();
        response.setPollId(pollId);
        response.setUserId(context.userId());
        response.setSubmittedAt(timeProvider.now());
        PollResponse savedResponse = pollResponseRepository.save(response);

        for (UUID optionId : optionIds) {
            PollResponseOption responseOption = new PollResponseOption();
            responseOption.setPollResponseId(savedResponse.getId());
            responseOption.setPollOptionId(optionId);
            pollResponseOptionRepository.save(responseOption);
        }
        eventPublisher.publish(new PollAnsweredEvent(pollId, context.userId()));
    }

    @Transactional(readOnly = true)
    public PollResultResponse results(UUID pollId) {
        Poll poll = findPoll(pollId);
        List<PollOption> options = pollOptionRepository.findByPollIdOrderByPositionAsc(pollId);
        Map<UUID, PollOption> optionById = options.stream().collect(Collectors.toMap(PollOption::getId, Function.identity()));
        long participants = pollResponseRepository.countByPollId(pollId);
        List<Object[]> counts = pollResponseOptionRepository.countByOptionForPoll(pollId);
        Map<UUID, Long> voteCountByOption = counts.stream().collect(Collectors.toMap(row -> (UUID) row[0], row -> (Long) row[1]));

        List<PollResultResponse.OptionResult> result = options.stream()
                .map(option -> {
                    long votes = voteCountByOption.getOrDefault(option.getId(), 0L);
                    double percentage = participants == 0 ? 0 : (votes * 100.0 / participants);
                    return new PollResultResponse.OptionResult(option.getId(), option.getLabel(), votes, percentage);
                })
                .sorted(Comparator.comparingLong(PollResultResponse.OptionResult::votes).reversed())
                .toList();

        return new PollResultResponse(poll.getId(), participants, result);
    }

    @Transactional
    public PollResponseDto publish(UUID pollId) {
        RequestUserContext context = contextResolver.getRequiredContext();
        authorizationService.requireAdmin(context);
        Poll poll = findPoll(pollId);
        poll.setStatus(PollStatus.PUBLISHED);
        return toDto(pollRepository.save(poll));
    }

    @Transactional
    public PollResponseDto close(UUID pollId) {
        RequestUserContext context = contextResolver.getRequiredContext();
        authorizationService.requireAdmin(context);
        Poll poll = findPoll(pollId);
        poll.setStatus(PollStatus.CLOSED);
        return toDto(pollRepository.save(poll));
    }

    private void validatePollOpen(Poll poll) {
        var now = timeProvider.now();
        if (poll.getStatus() != PollStatus.PUBLISHED || now.isBefore(poll.getOpensAt()) || now.isAfter(poll.getClosesAt())) {
            throw new BadRequestException("POLL_NOT_OPEN", "Poll is not open for responses");
        }
    }

    private Poll findPoll(UUID pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new NotFoundException("POLL_NOT_FOUND", "Poll not found"));
    }

    private PollResponseDto toDto(Poll poll) {
        List<PollResponseDto.PollOptionDto> options = pollOptionRepository.findByPollIdOrderByPositionAsc(poll.getId()).stream()
                .map(option -> new PollResponseDto.PollOptionDto(option.getId(), option.getLabel(), option.getPosition()))
                .toList();
        return new PollResponseDto(
                poll.getId(),
                poll.getTitle(),
                poll.getDescription(),
                poll.getType(),
                poll.getStatus(),
                poll.isMultipleChoice(),
                poll.isAnonymousResults(),
                poll.getOpensAt(),
                poll.getClosesAt(),
                options
        );
    }
}