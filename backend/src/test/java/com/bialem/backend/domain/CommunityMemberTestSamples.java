package com.bialem.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CommunityMemberTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CommunityMember getCommunityMemberSample1() {
        return new CommunityMember().id(1L);
    }

    public static CommunityMember getCommunityMemberSample2() {
        return new CommunityMember().id(2L);
    }

    public static CommunityMember getCommunityMemberRandomSampleGenerator() {
        return new CommunityMember().id(longCount.incrementAndGet());
    }
}
