package com.bialem.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PartnerVenueStaffTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PartnerVenueStaff getPartnerVenueStaffSample1() {
        return new PartnerVenueStaff().id(1L);
    }

    public static PartnerVenueStaff getPartnerVenueStaffSample2() {
        return new PartnerVenueStaff().id(2L);
    }

    public static PartnerVenueStaff getPartnerVenueStaffRandomSampleGenerator() {
        return new PartnerVenueStaff().id(longCount.incrementAndGet());
    }
}
