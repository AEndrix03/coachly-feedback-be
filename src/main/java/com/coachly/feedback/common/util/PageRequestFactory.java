package com.coachly.feedback.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageRequestFactory {

    private PageRequestFactory() {
    }

    public static Pageable of(int page, int size, String sortBy, Sort.Direction direction) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(direction, sortBy));
    }
}