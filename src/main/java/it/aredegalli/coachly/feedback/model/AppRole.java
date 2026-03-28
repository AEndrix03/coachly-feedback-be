package it.aredegalli.coachly.feedback.model;

public enum AppRole {
    USER,
    MODERATOR,
    ADMIN;

    public boolean canModerate() {
        return this == MODERATOR || this == ADMIN;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}


