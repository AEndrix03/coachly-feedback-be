package it.aredegalli.coachly.feedback.service;

import it.aredegalli.coachly.feedback.dto.FeatureRequestResponse;
import it.aredegalli.coachly.feedback.dto.PagedResponse;
import it.aredegalli.coachly.feedback.controller.request.ChangeFeatureStatusRequest;
import it.aredegalli.coachly.feedback.controller.request.CreateFeatureRequestRequest;
import it.aredegalli.coachly.feedback.controller.request.UpdateFeatureRequestRequest;
import it.aredegalli.coachly.feedback.model.StatusHistory;
import it.aredegalli.coachly.feedback.model.VoteType;

import java.util.List;
import java.util.UUID;

public interface FeatureRequestService {
    FeatureRequestResponse create(CreateFeatureRequestRequest request);
    FeatureRequestResponse update(UUID id, UpdateFeatureRequestRequest request);
    FeatureRequestResponse getById(UUID id);
    PagedResponse<FeatureRequestResponse> list(String status, String category, String search, String sort, String moduleKey, String platformTarget, int page, int size);
    FeatureRequestResponse vote(UUID id, VoteType voteType);
    FeatureRequestResponse removeVote(UUID id);
    FeatureRequestResponse changeStatus(UUID id, ChangeFeatureStatusRequest request);
    FeatureRequestResponse markDuplicate(UUID id, UUID masterId);
    List<StatusHistory> history(UUID id);
}

