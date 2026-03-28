package it.aredegalli.coachly.feedback.time;

import java.time.Instant;

public interface TimeProvider {
    Instant now();
}


