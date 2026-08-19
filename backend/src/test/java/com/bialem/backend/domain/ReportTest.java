package com.bialem.backend.domain;

import static com.bialem.backend.domain.ProfileTestSamples.*;
import static com.bialem.backend.domain.ReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Report.class);
        Report report1 = getReportSample1();
        Report report2 = new Report();
        assertThat(report1).isNotEqualTo(report2);

        report2.setId(report1.getId());
        assertThat(report1).isEqualTo(report2);

        report2 = getReportSample2();
        assertThat(report1).isNotEqualTo(report2);
    }

    @Test
    void reporterTest() {
        Report report = getReportRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        report.setReporter(profileBack);
        assertThat(report.getReporter()).isEqualTo(profileBack);

        report.reporter(null);
        assertThat(report.getReporter()).isNull();
    }

    @Test
    void resolvedByTest() {
        Report report = getReportRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        report.setResolvedBy(profileBack);
        assertThat(report.getResolvedBy()).isEqualTo(profileBack);

        report.resolvedBy(null);
        assertThat(report.getResolvedBy()).isNull();
    }
}
