package com.bialem.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PlatformTeamMemberTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PlatformTeamMember getPlatformTeamMemberSample1() {
        return new PlatformTeamMember().id(1L);
    }

    public static PlatformTeamMember getPlatformTeamMemberSample2() {
        return new PlatformTeamMember().id(2L);
    }

    public static PlatformTeamMember getPlatformTeamMemberRandomSampleGenerator() {
        return new PlatformTeamMember().id(longCount.incrementAndGet());
    }
}
