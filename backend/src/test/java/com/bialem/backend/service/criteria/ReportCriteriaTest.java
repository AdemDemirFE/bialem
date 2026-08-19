package com.bialem.backend.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReportCriteriaTest {

    @Test
    void newReportCriteriaHasAllFiltersNullTest() {
        var reportCriteria = new ReportCriteria();
        assertThat(reportCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void reportCriteriaFluentMethodsCreatesFiltersTest() {
        var reportCriteria = new ReportCriteria();

        setAllFilters(reportCriteria);

        assertThat(reportCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void reportCriteriaCopyCreatesNullFilterTest() {
        var reportCriteria = new ReportCriteria();
        var copy = reportCriteria.copy();

        assertThat(reportCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(reportCriteria)
        );
    }

    @Test
    void reportCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var reportCriteria = new ReportCriteria();
        setAllFilters(reportCriteria);

        var copy = reportCriteria.copy();

        assertThat(reportCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(reportCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var reportCriteria = new ReportCriteria();

        assertThat(reportCriteria).hasToString("ReportCriteria{}");
    }

    private static void setAllFilters(ReportCriteria reportCriteria) {
        reportCriteria.id();
        reportCriteria.targetType();
        reportCriteria.targetId();
        reportCriteria.reason();
        reportCriteria.status();
        reportCriteria.resolvedAt();
        reportCriteria.createdAt();
        reportCriteria.updatedAt();
        reportCriteria.reporterId();
        reportCriteria.resolvedById();
        reportCriteria.distinct();
    }

    private static Condition<ReportCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTargetType()) &&
                condition.apply(criteria.getTargetId()) &&
                condition.apply(criteria.getReason()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getResolvedAt()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getReporterId()) &&
                condition.apply(criteria.getResolvedById()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReportCriteria> copyFiltersAre(ReportCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTargetType(), copy.getTargetType()) &&
                condition.apply(criteria.getTargetId(), copy.getTargetId()) &&
                condition.apply(criteria.getReason(), copy.getReason()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getResolvedAt(), copy.getResolvedAt()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getReporterId(), copy.getReporterId()) &&
                condition.apply(criteria.getResolvedById(), copy.getResolvedById()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
