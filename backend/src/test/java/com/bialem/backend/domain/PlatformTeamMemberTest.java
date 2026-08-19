package com.bialem.backend.domain;

import static com.bialem.backend.domain.PlatformTeamMemberTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlatformTeamMemberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlatformTeamMember.class);
        PlatformTeamMember platformTeamMember1 = getPlatformTeamMemberSample1();
        PlatformTeamMember platformTeamMember2 = new PlatformTeamMember();
        assertThat(platformTeamMember1).isNotEqualTo(platformTeamMember2);

        platformTeamMember2.setId(platformTeamMember1.getId());
        assertThat(platformTeamMember1).isEqualTo(platformTeamMember2);

        platformTeamMember2 = getPlatformTeamMemberSample2();
        assertThat(platformTeamMember1).isNotEqualTo(platformTeamMember2);
    }

    @Test
    void userTest() {
        PlatformTeamMember platformTeamMember = getPlatformTeamMemberRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        platformTeamMember.setUser(profileBack);
        assertThat(platformTeamMember.getUser()).isEqualTo(profileBack);

        platformTeamMember.user(null);
        assertThat(platformTeamMember.getUser()).isNull();
    }

    @Test
    void assignedByTest() {
        PlatformTeamMember platformTeamMember = getPlatformTeamMemberRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        platformTeamMember.setAssignedBy(profileBack);
        assertThat(platformTeamMember.getAssignedBy()).isEqualTo(profileBack);

        platformTeamMember.assignedBy(null);
        assertThat(platformTeamMember.getAssignedBy()).isNull();
    }
}
