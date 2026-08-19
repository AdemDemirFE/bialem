package com.bialem.backend.domain;

import static com.bialem.backend.domain.CommunityTestSamples.*;
import static com.bialem.backend.domain.HonorBadgeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HonorBadgeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HonorBadge.class);
        HonorBadge honorBadge1 = getHonorBadgeSample1();
        HonorBadge honorBadge2 = new HonorBadge();
        assertThat(honorBadge1).isNotEqualTo(honorBadge2);

        honorBadge2.setId(honorBadge1.getId());
        assertThat(honorBadge1).isEqualTo(honorBadge2);

        honorBadge2 = getHonorBadgeSample2();
        assertThat(honorBadge1).isNotEqualTo(honorBadge2);
    }

    @Test
    void communityTest() {
        HonorBadge honorBadge = getHonorBadgeRandomSampleGenerator();
        Community communityBack = getCommunityRandomSampleGenerator();

        honorBadge.setCommunity(communityBack);
        assertThat(honorBadge.getCommunity()).isEqualTo(communityBack);

        honorBadge.community(null);
        assertThat(honorBadge.getCommunity()).isNull();
    }
}
