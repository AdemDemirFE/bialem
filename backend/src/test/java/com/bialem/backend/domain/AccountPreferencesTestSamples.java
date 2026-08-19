package com.bialem.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AccountPreferencesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AccountPreferences getAccountPreferencesSample1() {
        return new AccountPreferences().id(1L);
    }

    public static AccountPreferences getAccountPreferencesSample2() {
        return new AccountPreferences().id(2L);
    }

    public static AccountPreferences getAccountPreferencesRandomSampleGenerator() {
        return new AccountPreferences().id(longCount.incrementAndGet());
    }
}
