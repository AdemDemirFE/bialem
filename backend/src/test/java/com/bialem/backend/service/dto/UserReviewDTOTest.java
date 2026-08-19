package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserReviewDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserReviewDTO.class);
        UserReviewDTO userReviewDTO1 = new UserReviewDTO();
        userReviewDTO1.setId(1L);
        UserReviewDTO userReviewDTO2 = new UserReviewDTO();
        assertThat(userReviewDTO1).isNotEqualTo(userReviewDTO2);
        userReviewDTO2.setId(userReviewDTO1.getId());
        assertThat(userReviewDTO1).isEqualTo(userReviewDTO2);
        userReviewDTO2.setId(2L);
        assertThat(userReviewDTO1).isNotEqualTo(userReviewDTO2);
        userReviewDTO1.setId(null);
        assertThat(userReviewDTO1).isNotEqualTo(userReviewDTO2);
    }
}
