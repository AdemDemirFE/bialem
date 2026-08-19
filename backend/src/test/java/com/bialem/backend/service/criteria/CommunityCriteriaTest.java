package com.bialem.backend.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CommunityCriteriaTest {

    @Test
    void newCommunityCriteriaHasAllFiltersNullTest() {
        var communityCriteria = new CommunityCriteria();
        assertThat(communityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void communityCriteriaFluentMethodsCreatesFiltersTest() {
        var communityCriteria = new CommunityCriteria();

        setAllFilters(communityCriteria);

        assertThat(communityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void communityCriteriaCopyCreatesNullFilterTest() {
        var communityCriteria = new CommunityCriteria();
        var copy = communityCriteria.copy();

        assertThat(communityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(communityCriteria)
        );
    }

    @Test
    void communityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var communityCriteria = new CommunityCriteria();
        setAllFilters(communityCriteria);

        var copy = communityCriteria.copy();

        assertThat(communityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(communityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var communityCriteria = new CommunityCriteria();

        assertThat(communityCriteria).hasToString("CommunityCriteria{}");
    }

    private static void setAllFilters(CommunityCriteria communityCriteria) {
        communityCriteria.id();
        communityCriteria.name();
        communityCriteria.slug();
        communityCriteria.visibility();
        communityCriteria.coverImageUrl();
        communityCriteria.communityType();
        communityCriteria.partnerTrustLevel();
        communityCriteria.isVerifiedPartner();
        communityCriteria.isDiscoverable();
        communityCriteria.createdAt();
        communityCriteria.updatedAt();
        communityCriteria.parentId();
        communityCriteria.categoryHubId();
        communityCriteria.createdById();
        communityCriteria.leadModeratorId();
        communityCriteria.childrenId();
        communityCriteria.categorizedGroupsId();
        communityCriteria.membersId();
        communityCriteria.assistantsId();
        communityCriteria.eventsId();
        communityCriteria.postsId();
        communityCriteria.storyTargetsId();
        communityCriteria.distinct();
    }

    private static Condition<CommunityCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getSlug()) &&
                condition.apply(criteria.getVisibility()) &&
                condition.apply(criteria.getCoverImageUrl()) &&
                condition.apply(criteria.getCommunityType()) &&
                condition.apply(criteria.getPartnerTrustLevel()) &&
                condition.apply(criteria.getIsVerifiedPartner()) &&
                condition.apply(criteria.getIsDiscoverable()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getParentId()) &&
                condition.apply(criteria.getCategoryHubId()) &&
                condition.apply(criteria.getCreatedById()) &&
                condition.apply(criteria.getLeadModeratorId()) &&
                condition.apply(criteria.getChildrenId()) &&
                condition.apply(criteria.getCategorizedGroupsId()) &&
                condition.apply(criteria.getMembersId()) &&
                condition.apply(criteria.getAssistantsId()) &&
                condition.apply(criteria.getEventsId()) &&
                condition.apply(criteria.getPostsId()) &&
                condition.apply(criteria.getStoryTargetsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CommunityCriteria> copyFiltersAre(CommunityCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getSlug(), copy.getSlug()) &&
                condition.apply(criteria.getVisibility(), copy.getVisibility()) &&
                condition.apply(criteria.getCoverImageUrl(), copy.getCoverImageUrl()) &&
                condition.apply(criteria.getCommunityType(), copy.getCommunityType()) &&
                condition.apply(criteria.getPartnerTrustLevel(), copy.getPartnerTrustLevel()) &&
                condition.apply(criteria.getIsVerifiedPartner(), copy.getIsVerifiedPartner()) &&
                condition.apply(criteria.getIsDiscoverable(), copy.getIsDiscoverable()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getParentId(), copy.getParentId()) &&
                condition.apply(criteria.getCategoryHubId(), copy.getCategoryHubId()) &&
                condition.apply(criteria.getCreatedById(), copy.getCreatedById()) &&
                condition.apply(criteria.getLeadModeratorId(), copy.getLeadModeratorId()) &&
                condition.apply(criteria.getChildrenId(), copy.getChildrenId()) &&
                condition.apply(criteria.getCategorizedGroupsId(), copy.getCategorizedGroupsId()) &&
                condition.apply(criteria.getMembersId(), copy.getMembersId()) &&
                condition.apply(criteria.getAssistantsId(), copy.getAssistantsId()) &&
                condition.apply(criteria.getEventsId(), copy.getEventsId()) &&
                condition.apply(criteria.getPostsId(), copy.getPostsId()) &&
                condition.apply(criteria.getStoryTargetsId(), copy.getStoryTargetsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
