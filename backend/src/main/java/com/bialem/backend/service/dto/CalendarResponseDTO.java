package com.bialem.backend.service.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record CalendarResponseDTO(Range range, Counts counts, List<Item> items, List<Item> upcoming) {
    public record Range(LocalDate startDate, LocalDate endDate) {}
    public record Counts(long bialemEvents, long cityEvents, long birthdays, long total) {}
    public record Item(String type, String referenceId, String title, String description, LocalDate date, Instant startsAt,
        Instant endsAt, String imageUrl, String location, String category, String priceLabel, String route,
        Long participantCount, String username, Integer calculatedAge) {}
}
