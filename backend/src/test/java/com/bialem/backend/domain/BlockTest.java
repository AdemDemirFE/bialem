package com.bialem.backend.domain;

import static com.bialem.backend.domain.BlockTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BlockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Block.class);
        Block block1 = getBlockSample1();
        Block block2 = new Block();
        assertThat(block1).isNotEqualTo(block2);

        block2.setId(block1.getId());
        assertThat(block1).isEqualTo(block2);

        block2 = getBlockSample2();
        assertThat(block1).isNotEqualTo(block2);
    }

    @Test
    void blockerTest() {
        Block block = getBlockRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        block.setBlocker(profileBack);
        assertThat(block.getBlocker()).isEqualTo(profileBack);

        block.blocker(null);
        assertThat(block.getBlocker()).isNull();
    }

    @Test
    void blockedUserTest() {
        Block block = getBlockRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        block.setBlockedUser(profileBack);
        assertThat(block.getBlockedUser()).isEqualTo(profileBack);

        block.blockedUser(null);
        assertThat(block.getBlockedUser()).isNull();
    }
}
