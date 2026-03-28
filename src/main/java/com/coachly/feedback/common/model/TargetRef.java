package com.coachly.feedback.common.model;

import java.util.UUID;

public record TargetRef(TargetType targetType, UUID targetId) {
}