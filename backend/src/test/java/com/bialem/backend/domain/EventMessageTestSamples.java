package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EventMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static EventMessage getEventMessageSample1() {
        return new EventMessage().id(1L).body("body1");
    }

    public static EventMessage getEventMessageSample2() {
        return new EventMessage().id(2L).body("body2");
    }

    public static EventMessage getEventMessageRandomSampleGenerator() {
        return new EventMessage().id(longCount.incrementAndGet()).body(UUID.randomUUID().toString());
    }
}
