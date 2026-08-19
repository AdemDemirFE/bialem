package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EventParticipantTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static EventParticipant getEventParticipantSample1() {
        return new EventParticipant().id(1L).note("note1");
    }

    public static EventParticipant getEventParticipantSample2() {
        return new EventParticipant().id(2L).note("note2");
    }

    public static EventParticipant getEventParticipantRandomSampleGenerator() {
        return new EventParticipant().id(longCount.incrementAndGet()).note(UUID.randomUUID().toString());
    }
}
