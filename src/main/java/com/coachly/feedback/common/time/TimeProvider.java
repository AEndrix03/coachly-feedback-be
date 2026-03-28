package com.coachly.feedback.common.time;

import java.time.Instant;

public interface TimeProvider {
    Instant now();
}