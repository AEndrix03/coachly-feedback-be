package it.aredegalli.coachly.feedback.model;

public enum FeatureRequestSort {
    NEWEST,
    OLDEST,
    TOP,
    DISCUSSED,
    TRENDING;

    public static FeatureRequestSort from(String value) {
        if (value == null || value.isBlank()) {
            return NEWEST;
        }
        return switch (value.trim().toLowerCase()) {
            case "newest" -> NEWEST;
            case "oldest" -> OLDEST;
            case "top" -> TOP;
            case "discussed" -> DISCUSSED;
            case "trending" -> TRENDING;
            default -> NEWEST;
        };
    }
}


