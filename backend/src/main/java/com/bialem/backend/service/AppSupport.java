package com.bialem.backend.service;

import com.bialem.backend.domain.*;
import com.bialem.backend.domain.enumeration.*;
import com.bialem.backend.security.SecurityUtils;
import com.bialem.backend.web.rest.vm.AppQueryRequest.AppQueryResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AppSupport {

    public static final Map<String, Class<?>> TABLES = new HashMap<>();

    static {
        TABLES.put("profiles", Profile.class);
        TABLES.put("account_preferences", AccountPreferences.class);
        TABLES.put("communities", Community.class);
        TABLES.put("community_members", CommunityMember.class);
        TABLES.put("community_moderator_assistants", CommunityModeratorAssistant.class);
        TABLES.put("events", Event.class);
        TABLES.put("event_participants", EventParticipant.class);
        TABLES.put("event_ratings", EventRating.class);
        TABLES.put("event_messages", EventMessage.class);
        TABLES.put("comments", Comment.class);
        TABLES.put("posts", Post.class);
        TABLES.put("post_media", PostMedia.class);
        TABLES.put("stories", Story.class);
        TABLES.put("story_views", StoryView.class);
        TABLES.put("story_community_targets", StoryCommunityTarget.class);
        TABLES.put("follows", Follow.class);
        TABLES.put("follow_requests", FollowRequest.class);
        TABLES.put("blocks", Block.class);
        TABLES.put("user_reviews", UserReview.class);
        TABLES.put("reports", Report.class);
        TABLES.put("notifications", Notification.class);
        TABLES.put("push_tokens", PushToken.class);
        TABLES.put("city_events", CityEvent.class);
        TABLES.put("city_event_interests", CityEventInterest.class);
        TABLES.put("city_event_ticket_offers", CityEventTicketOffer.class);
        TABLES.put("partner_venues", PartnerVenue.class);
        TABLES.put("partner_venue_staff", PartnerVenueStaff.class);
        TABLES.put("partner_offers", PartnerOffer.class);
        TABLES.put("partner_offer_redemptions", PartnerOfferRedemption.class);
        TABLES.put("honor_badges", HonorBadge.class);
        TABLES.put("user_honor_badges", UserHonorBadge.class);
        TABLES.put("platform_team_members", PlatformTeamMember.class);
        TABLES.put("ai_usage_logs", AiUsageLog.class);
        TABLES.put("roles", Role.class);
        TABLES.put("user_roles", UserRole.class);
    }

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public Profile currentProfile() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        List<Profile> matches = em
            .createQuery("select p from Profile p left join fetch p.user where lower(p.user.login) = :login", Profile.class)
            .setParameter("login", login.toLowerCase(Locale.ROOT))
            .setMaxResults(1)
            .getResultList();
        if (matches.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil bulunamadı");
        }
        return matches.get(0);
    }

    public static AppQueryResponse failure(Exception ex) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String message = ex instanceof ResponseStatusException status
            ? (status.getReason() == null ? status.getMessage() : status.getReason())
            : (root.getMessage() == null ? ex.getMessage() : root.getMessage());
        return new AppQueryResponse(null, message, null);
    }

    public Profile requireProfile(Object id) {
        Long parsed = parseLong(id);
        if (parsed == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz profil");
        }
        Profile profile = em.find(Profile.class, parsed);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil bulunamadı");
        }
        return profile;
    }

    public Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty() || "null".equals(text)) {
            return null;
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public String stringify(Object id) {
        return id == null ? null : String.valueOf(id);
    }

    public Map<String, Object> toMap(Object entity) {
        if (entity == null) {
            return null;
        }
        Map<String, Object> out = new LinkedHashMap<>();
        Class<?> type = entity.getClass();
        while (type != null && type != Object.class) {
            for (Field field : type.getDeclaredFields()) {
                if (Collection.class.isAssignableFrom(field.getType()) || field.getName().contains("$")) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    String key = camelToSnake(field.getName());
                    if (value == null) {
                        out.put(key, null);
                    } else if (value instanceof Enum<?> enumerated) {
                        out.put(key, enumerated.name().toLowerCase(Locale.ROOT));
                    } else if (value instanceof Instant instant) {
                        out.put(key, instant.toString());
                    } else if (value instanceof BigDecimal decimal) {
                        out.put(key, decimal);
                    } else if (value instanceof Boolean || value instanceof Number || value instanceof String) {
                        out.put(key, "id".equals(field.getName()) ? stringify(value) : value);
                    } else if (value.getClass().getName().startsWith("com.bialem.backend.domain.")) {
                        Object relatedId = readId(value);
                        out.put(key, stringify(relatedId));
                        out.put(key + "_id", stringify(relatedId));
                    }
                } catch (IllegalAccessException ignored) {
                    // skip unreadable fields
                }
            }
            type = type.getSuperclass();
        }
        if (entity instanceof Profile profile && profile.getUser() != null) {
            out.put("email", profile.getUser().getEmail());
            out.put("user_id", stringify(profile.getId()));
        }
        if (entity instanceof Event event) {
            out.put("communities", communityEmbed(event.getCommunity()));
            out.put("profiles", profileEmbed(event.getCreatedBy()));
        }
        if (entity instanceof Post post) {
            out.put("profiles", profileEmbed(post.getAuthor()));
            out.put("communities", communityEmbed(post.getCommunity()));
            out.put("post_media", post.getMedia() == null ? List.of() : post.getMedia().stream().map(this::toMap).toList());
        }
        if (entity instanceof Comment comment) {
            out.put("profiles", profileEmbed(comment.getAuthor()));
        }
        if (entity instanceof Story story) {
            out.put("profiles", profileEmbed(story.getAuthor()));
        }
        if (entity instanceof EventMessage message) {
            out.put("profiles", profileEmbed(message.getAuthor()));
        }
        if (entity instanceof Community community) {
            out.put("profiles", profileEmbed(community.getCreatedBy()));
        }
        if (entity instanceof CommunityMember member) {
            out.put("profiles", profileEmbed(member.getUser()));
            out.put("communities", communityEmbed(member.getCommunity()));
        }
        if (entity instanceof EventParticipant participant) {
            out.put("profiles", profileEmbed(participant.getUser()));
        }
        if (entity instanceof Follow follow) {
            out.put("profiles", profileEmbed(follow.getFollowed()));
            out.put("follower", profileEmbed(follow.getFollower()));
            out.put("followed", profileEmbed(follow.getFollowed()));
        }
        if (entity instanceof Notification notification) {
            out.put("profiles", profileEmbed(notification.getUser()));
        }
        if (entity instanceof CommunityModeratorAssistant assistant) {
            out.put("profiles", profileEmbed(assistant.getUser()));
            out.put("can_manage_members", assistant.getCanManageParticipants());
        }
        if (entity instanceof PartnerOffer offer) {
            PartnerVenue venue = offer.getVenue();
            if (venue != null) {
                out.put("partner_venues", Map.of("id", stringify(venue.getId()), "name", venue.getName() == null ? "" : venue.getName()));
            }
        }
        if (entity instanceof PlatformTeamMember teamMember) {
            out.put("profiles", profileEmbed(teamMember.getUser()));
        }
        return out;
    }

    public Map<String, Object> profileEmbed(Profile profile) {
        if (profile == null) {
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", stringify(profile.getId()));
        map.put("display_name", profile.getDisplayName());
        map.put("username", profile.getUsername());
        map.put("avatar_url", profile.getAvatarUrl());
        map.put("bio", profile.getBio());
        map.put("city", profile.getCity());
        map.put("status", profile.getStatus() == null ? null : profile.getStatus().name().toLowerCase(Locale.ROOT));
        map.put("is_verified", profile.getIsVerified());
        map.put("email", profile.getUser() != null ? profile.getUser().getEmail() : null);
        map.put("user_id", stringify(profile.getId()));
        return map;
    }

    public Map<String, Object> communityEmbed(Community community) {
        if (community == null) {
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", stringify(community.getId()));
        map.put("name", community.getName());
        map.put("slug", community.getSlug());
        return map;
    }

    public void applyPayload(Object entity, Map<String, Object> payload) {
        if (payload == null) {
            return;
        }
        payload.forEach((column, raw) -> {
            if ("id".equals(column)) {
                return;
            }
            String relation = relationField(entity.getClass(), column);
            if (relation != null) {
                setRelation(entity, relation, parseLong(raw));
                return;
            }
            setSimple(entity, snakeToCamel(column), raw);
        });
        fillDefaults(entity);
        touchTimestamps(entity);
    }

    private void fillDefaults(Object entity) {
        Instant now = Instant.now();
        if (entity instanceof Community community) {
            if (community.getVisibility() == null) community.setVisibility(CommunityVisibility.PUBLIC);
            if (community.getCommunityType() == null) community.setCommunityType(CommunityType.GROUP);
            if (community.getPartnerTrustLevel() == null) community.setPartnerTrustLevel(PartnerTrustLevel.NEW);
            if (community.getIsVerifiedPartner() == null) community.setIsVerifiedPartner(false);
            if (community.getIsDiscoverable() == null) community.setIsDiscoverable(true);
        }
        if (entity instanceof Event event) {
            if (event.getStatus() == null) event.setStatus(EventStatus.DRAFT);
            if (event.getPublishedToDiscovery() == null) event.setPublishedToDiscovery(false);
            if (event.getGroupModerationStatus() == null) event.setGroupModerationStatus(GroupModerationStatus.PENDING);
            if (event.getPlatformModerationStatus() == null) event.setPlatformModerationStatus(PlatformModerationStatus.NOT_REQUIRED);
        }
        if (entity instanceof Comment comment) {
            if (comment.getModerationStatus() == null) comment.setModerationStatus(ModerationStatus.VISIBLE);
        }
        if (entity instanceof Post post) {
            if (post.getVisibility() == null) post.setVisibility(PostVisibility.PUBLIC);
            if (post.getModerationStatus() == null) post.setModerationStatus(ModerationStatus.VISIBLE);
        }
        if (entity instanceof Report report) {
            if (report.getStatus() == null) report.setStatus(ReportStatus.OPEN);
        }
        if (entity instanceof EventMessage message) {
            if (message.getModerationStatus() == null) message.setModerationStatus(ModerationStatus.VISIBLE);
        }
        if (entity instanceof EventRating rating) {
            if (rating.getCreatedAt() == null) rating.setCreatedAt(now);
        }
        if (entity instanceof Notification notification) {
            if (notification.getIsRead() == null) notification.setIsRead(false);
        }
        if (entity instanceof PartnerVenue venue) {
            if (venue.getIsActive() == null) venue.setIsActive(true);
            if (venue.getIsFeatured() == null) venue.setIsFeatured(false);
            if (venue.getCategory() == null) venue.setCategory(PartnerVenueCategory.OTHER);
        }
        if (entity instanceof PartnerOffer offer) {
            if (offer.getIsActive() == null) offer.setIsActive(true);
            if (offer.getValidFrom() == null) offer.setValidFrom(now);
        }
        if (entity instanceof PartnerVenueStaff staff) {
            if (staff.getIsActive() == null) staff.setIsActive(true);
        }
    }

    public String relationField(Class<?> type, String column) {
        return switch (column) {
            case "user_id", "user" -> fieldExists(type, "user") ? "user" : null;
            case "community_id", "community" -> fieldExists(type, "community") ? "community" : null;
            case "event_id", "event" -> fieldExists(type, "event") ? "event" : null;
            case "author_id", "author" -> "author";
            case "created_by" -> "createdBy";
            case "cancelled_by" -> "cancelledBy";
            case "lead_moderator_id" -> "leadModerator";
            case "parent_id" -> "parent";
            case "category_id" -> fieldExists(type, "categoryHub") ? "categoryHub" : "category";
            case "follower_id" -> "follower";
            case "followed_id" -> "followed";
            case "requester_id" -> "requester";
            case "target_user_id" -> fieldExists(type, "targetUser") ? "targetUser" : "blockedUser";
            case "blocker_id" -> "blocker";
            case "blocked_user_id" -> "blockedUser";
            case "reviewer_id" -> "reviewer";
            case "reviewed_user_id" -> "reviewedUser";
            case "reporter_id" -> "reporter";
            case "resolved_by" -> "resolvedBy";
            case "viewer_id" -> "viewer";
            case "badge_id" -> "badge";
            case "awarded_by" -> "awardedBy";
            case "assigned_by" -> "assignedBy";
            case "venue_id" -> "venue";
            case "offer_id" -> "offer";
            case "redeemed_by" -> "redeemedBy";
            case "city_event_id" -> "cityEvent";
            case "profile_id" -> "profile";
            case "post_id" -> "post";
            case "story_id" -> "story";
            default -> null;
        };
    }

    public PathRef path(Class<?> type, String column) {
        String relation = relationField(type, column);
        if (relation != null) {
            return new PathRef(relation, "id");
        }
        if ("email".equals(column) && type == Profile.class) {
            return new PathRef("user", "email");
        }
        return new PathRef(snakeToCamel(column), null);
    }

    public record PathRef(String field, String nested) {}

    private void setRelation(Object entity, String fieldName, Long id) {
        try {
            Field field = findField(entity.getClass(), fieldName);
            if (field == null) {
                return;
            }
            field.setAccessible(true);
            if (id == null) {
                field.set(entity, null);
                return;
            }
            Object related = em.getReference(field.getType(), id);
            field.set(entity, related);
        } catch (Exception ignored) {
            // ignore unknown relation
        }
    }

    private void setSimple(Object entity, String fieldName, Object raw) {
        try {
            Field field = findField(entity.getClass(), fieldName);
            if (field == null) {
                return;
            }
            field.setAccessible(true);
            field.set(entity, coerce(field.getType(), raw));
        } catch (Exception ignored) {
            // ignore unknown field
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object coerce(Class<?> type, Object raw) {
        if (raw == null) {
            return null;
        }
        if (type.isAssignableFrom(raw.getClass())) {
            return raw;
        }
        if (type == Instant.class) {
            return Instant.parse(String.valueOf(raw));
        }
        if (type == Integer.class || type == int.class) {
            return Integer.valueOf(String.valueOf(raw));
        }
        if (type == Long.class || type == long.class) {
            return parseLong(raw);
        }
        if (type == Boolean.class || type == boolean.class) {
            return Boolean.valueOf(String.valueOf(raw));
        }
        if (type == BigDecimal.class) {
            return new BigDecimal(String.valueOf(raw));
        }
        if (type.isEnum()) {
            return Enum.valueOf((Class<Enum>) type, String.valueOf(raw).trim().toUpperCase(Locale.ROOT).replace('-', '_'));
        }
        if (type == String.class) {
            return String.valueOf(raw);
        }
        return raw;
    }

    private void touchTimestamps(Object entity) {
        Instant now = Instant.now();
        setIfNull(entity, "createdAt", now);
        setSimple(entity, "updatedAt", now);
    }

    private void setIfNull(Object entity, String fieldName, Object value) {
        try {
            Field field = findField(entity.getClass(), fieldName);
            if (field == null) {
                return;
            }
            field.setAccessible(true);
            if (field.get(entity) == null) {
                field.set(entity, value);
            }
        } catch (Exception ignored) {
            // ignore
        }
    }

    private boolean fieldExists(Class<?> type, String name) {
        return findField(type, name) != null;
    }

    private Field findField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ex) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private Object readId(Object entity) {
        try {
            Field field = findField(entity.getClass(), "id");
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            return field.get(entity);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String camelToSnake(String value) {
        return value.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
    }

    public static String snakeToCamel(String value) {
        StringBuilder builder = new StringBuilder();
        boolean upper = false;
        for (char character : value.toCharArray()) {
            if (character == '_') {
                upper = true;
            } else if (upper) {
                builder.append(Character.toUpperCase(character));
                upper = false;
            } else {
                builder.append(character);
            }
        }
        return builder.toString();
    }
}
