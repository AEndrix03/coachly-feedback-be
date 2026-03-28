package com.coachly.feedback.featurerequest.domain;

import com.coachly.feedback.common.exception.BadRequestException;
import com.coachly.feedback.common.model.FeatureRequestStatus;
import com.coachly.feedback.common.model.VoteType;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

@Component
public class FeatureRequestPolicy {

    private static final Set<FeatureRequestStatus> VOTABLE_STATUSES = EnumSet.of(
            FeatureRequestStatus.NEW,
            FeatureRequestStatus.UNDER_REVIEW,
            FeatureRequestStatus.PLANNED
    );

    public void validateVoteAllowed(FeatureRequestStatus status, VoteType voteType) {
        if (!VOTABLE_STATUSES.contains(status)) {
            throw new BadRequestException("FEATURE_NOT_VOTABLE", "Votes are not allowed for this feature request status");
        }
        if (voteType != VoteType.UP) {
            throw new BadRequestException("VOTE_TYPE_NOT_SUPPORTED", "Feature requests currently support only UP votes");
        }
    }
}