package com.bialem.backend.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PartnerVenueCriteriaTest {

    @Test
    void newPartnerVenueCriteriaHasAllFiltersNullTest() {
        var partnerVenueCriteria = new PartnerVenueCriteria();
        assertThat(partnerVenueCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void partnerVenueCriteriaFluentMethodsCreatesFiltersTest() {
        var partnerVenueCriteria = new PartnerVenueCriteria();

        setAllFilters(partnerVenueCriteria);

        assertThat(partnerVenueCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void partnerVenueCriteriaCopyCreatesNullFilterTest() {
        var partnerVenueCriteria = new PartnerVenueCriteria();
        var copy = partnerVenueCriteria.copy();

        assertThat(partnerVenueCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(partnerVenueCriteria)
        );
    }

    @Test
    void partnerVenueCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var partnerVenueCriteria = new PartnerVenueCriteria();
        setAllFilters(partnerVenueCriteria);

        var copy = partnerVenueCriteria.copy();

        assertThat(partnerVenueCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(partnerVenueCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var partnerVenueCriteria = new PartnerVenueCriteria();

        assertThat(partnerVenueCriteria).hasToString("PartnerVenueCriteria{}");
    }

    private static void setAllFilters(PartnerVenueCriteria partnerVenueCriteria) {
        partnerVenueCriteria.id();
        partnerVenueCriteria.name();
        partnerVenueCriteria.slug();
        partnerVenueCriteria.category();
        partnerVenueCriteria.logoUrl();
        partnerVenueCriteria.coverImageUrl();
        partnerVenueCriteria.address();
        partnerVenueCriteria.city();
        partnerVenueCriteria.latitude();
        partnerVenueCriteria.longitude();
        partnerVenueCriteria.phone();
        partnerVenueCriteria.websiteUrl();
        partnerVenueCriteria.instagramUrl();
        partnerVenueCriteria.isFeatured();
        partnerVenueCriteria.isActive();
        partnerVenueCriteria.createdAt();
        partnerVenueCriteria.updatedAt();
        partnerVenueCriteria.offersId();
        partnerVenueCriteria.staffId();
        partnerVenueCriteria.distinct();
    }

    private static Condition<PartnerVenueCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getSlug()) &&
                condition.apply(criteria.getCategory()) &&
                condition.apply(criteria.getLogoUrl()) &&
                condition.apply(criteria.getCoverImageUrl()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getCity()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getWebsiteUrl()) &&
                condition.apply(criteria.getInstagramUrl()) &&
                condition.apply(criteria.getIsFeatured()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getOffersId()) &&
                condition.apply(criteria.getStaffId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PartnerVenueCriteria> copyFiltersAre(
        PartnerVenueCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getSlug(), copy.getSlug()) &&
                condition.apply(criteria.getCategory(), copy.getCategory()) &&
                condition.apply(criteria.getLogoUrl(), copy.getLogoUrl()) &&
                condition.apply(criteria.getCoverImageUrl(), copy.getCoverImageUrl()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getCity(), copy.getCity()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getWebsiteUrl(), copy.getWebsiteUrl()) &&
                condition.apply(criteria.getInstagramUrl(), copy.getInstagramUrl()) &&
                condition.apply(criteria.getIsFeatured(), copy.getIsFeatured()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getOffersId(), copy.getOffersId()) &&
                condition.apply(criteria.getStaffId(), copy.getStaffId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
