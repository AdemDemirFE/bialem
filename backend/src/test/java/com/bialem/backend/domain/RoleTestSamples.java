package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RoleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Role getRoleSample1() {
        return new Role().id(1L).code("code1").name("name1");
    }

    public static Role getRoleSample2() {
        return new Role().id(2L).code("code2").name("name2");
    }

    public static Role getRoleRandomSampleGenerator() {
        return new Role().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).name(UUID.randomUUID().toString());
    }
}
