package com.bialem.backend.domain;

import static com.bialem.backend.domain.CityEventInterestTestSamples.*;
import static com.bialem.backend.domain.CityEventTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CityEventInterestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CityEventInterest.class);
        CityEventInterest cityEventInterest1 = getCityEventInterestSample1();
        CityEventInterest cityEventInterest2 = new CityEventInterest();
        assertThat(cityEventInterest1).isNotEqualTo(cityEventInterest2);

        cityEventInterest2.setId(cityEventInterest1.getId());
        assertThat(cityEventInterest1).isEqualTo(cityEventInterest2);

        cityEventInterest2 = getCityEventInterestSample2();
        assertThat(cityEventInterest1).isNotEqualTo(cityEventInterest2);
    }

    @Test
    void cityEventTest() {
        CityEventInterest cityEventInterest = getCityEventInterestRandomSampleGenerator();
        CityEvent cityEventBack = getCityEventRandomSampleGenerator();

        cityEventInterest.setCityEvent(cityEventBack);
        assertThat(cityEventInterest.getCityEvent()).isEqualTo(cityEventBack);

        cityEventInterest.cityEvent(null);
        assertThat(cityEventInterest.getCityEvent()).isNull();
    }

    @Test
    void userTest() {
        CityEventInterest cityEventInterest = getCityEventInterestRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        cityEventInterest.setUser(profileBack);
        assertThat(cityEventInterest.getUser()).isEqualTo(profileBack);

        cityEventInterest.user(null);
        assertThat(cityEventInterest.getUser()).isNull();
    }
}
