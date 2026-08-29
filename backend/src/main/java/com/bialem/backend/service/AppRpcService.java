package com.bialem.backend.service;

import com.bialem.backend.domain.*;
import com.bialem.backend.domain.enumeration.*;
import com.bialem.backend.domain.enumeration.StoryReactionType;
import com.bialem.backend.payment.*;
import com.bialem.backend.repository.*;
import com.bialem.backend.security.AuthoritiesConstants;
import com.bialem.backend.security.SecurityUtils;
import com.bialem.backend.notification.NotificationEvent;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.web.rest.vm.AppQueryRequest.AppQueryResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppRpcService {

    private static final Logger LOG = LoggerFactory.getLogger(AppRpcService.class);

    @PersistenceContext
    private EntityManager em;

    private final AppSupport support;
    private final TransactionTemplate transactions;
    private final NotificationEventPublisher notificationEvents;
    private final CommunityAuthorizationService communityAuthorization;
    private final PaymentService paymentService;
    private final EventTicketRepository eventTicketRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final HashtagRepository hashtagRepository;
    private final StoryHashtagRepository storyHashtagRepository;
    private final StoryGroupRepository storyGroupRepository;
    private final StoryElementRepository storyElementRepository;

    public AppRpcService(AppSupport support, PlatformTransactionManager transactionManager, NotificationEventPublisher notificationEvents,
        CommunityAuthorizationService communityAuthorization, PaymentService paymentService,
        EventTicketRepository eventTicketRepository, OrderRepository orderRepository,
        OrderItemRepository orderItemRepository, TicketRepository ticketRepository,
        PaymentRepository paymentRepository, HashtagRepository hashtagRepository,
        StoryHashtagRepository storyHashtagRepository, StoryGroupRepository storyGroupRepository,
        StoryElementRepository storyElementRepository) {
        this.support = support;
        this.transactions = new TransactionTemplate(transactionManager);
        this.notificationEvents = notificationEvents;
        this.communityAuthorization = communityAuthorization;
        this.paymentService = paymentService;
        this.eventTicketRepository = eventTicketRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.hashtagRepository = hashtagRepository;
        this.storyHashtagRepository = storyHashtagRepository;
        this.storyGroupRepository = storyGroupRepository;
        this.storyElementRepository = storyElementRepository;
    }

    public AppQueryResponse invoke(String name, Map<String, Object> args) {
        return transactions.execute(status -> {
            try {
                Object data = dispatch(name, args == null ? Map.of() : args);
                return new AppQueryResponse(data, null, null);
            } catch (Exception ex) {
                status.setRollbackOnly();
                LOG.error("App RPC failed: {}", name, ex);
                return AppSupport.failure(ex);
            }
        });
    }

    private Object dispatch(String name, Map<String, Object> args) {
        if ("is_admin".equals(name)) {
            return SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPER_ADMIN);
        }
        Profile me = support.currentProfile();
        return switch (name) {
            case "search_public_profiles" -> searchProfiles(me, str(args.get("target_query")), num(args.get("result_limit"), 40));
            case "get_public_profile_card" -> support.profileEmbed(support.requireProfile(args.get("target_user_id")));
            case "get_public_follow_summary" -> followSummary(id(args.get("target_user_id")));
            case "get_user_reliability" -> Map.of("score", 80, "completed_events", 0, "no_show_count", 0);
            case "get_user_honor_badges" -> honorBadges(id(args.get("target_user_id")));
            case "get_reviewable_events" -> reviewableEvents(me, id(args.get("target_user_id")));
            case "set_profile_follow_state" -> setFollow(me, id(args.get("target_user_id")), bool(args.get("target_should_follow")));
            case "get_my_follow_relation" -> followRelation(me, id(args.get("target_user_id")));
            case "get_my_follow_requests" -> myFollowRequests(me);
            case "review_follow_request" -> reviewFollowRequest(me, id(args.get("target_request_id")), str(args.get("target_decision")));
            case "get_public_follow_connections" -> followConnections(me, id(args.get("target_user_id")), str(args.get("target_kind")));
            case "set_profile_block" -> setBlock(me, id(args.get("target_user_id")), bool(args.get("should_block"), true));
            case "get_my_blocked_profiles" -> blockedProfiles(me);
            case "get_my_community_memberships" -> myCommunityMemberships(me);
            case "get_communities_with_my_membership" -> communitiesWithMyMembership(me);
            case "join_community" -> joinCommunity(me, id(args.get("target_community_id")));
            case "leave_community" -> leaveCommunity(me, id(args.get("target_community_id")));
            case "cancel_community_membership_request" -> cancelMembership(me, id(args.get("target_community_id")));
            case "review_community_membership" -> reviewMembership(me, id(args.get("target_membership_id")), str(args.get("target_status")));
            case "remove_community_member" -> {
                Long membershipId = id(args.get("target_membership_id"));
                CommunityMember member = em.find(CommunityMember.class, membershipId);
                if (member == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                communityAuthorization.requireManageMembers(member.getCommunity().getId());
                requireMutableTarget(member.getUser());
                remove(CommunityMember.class, membershipId);
                yield true;
            }
            case "get_community_member_directory" -> memberDirectory(id(args.get("target_community_id")));
            case "get_community_member_directory_count" -> (long) memberDirectory(id(args.get("target_community_id"))).size();
            case "get_managed_community_members" -> managedMemberDirectory(id(args.get("target_community_id")));
            case "get_pending_managed_community_memberships" -> managedPendingMemberships(id(args.get("target_root_community_id")));
            case "get_my_community_assistant_permissions" -> assistantPermissions(me, id(args.get("target_community_id")));
            case "get_community_moderator_assistants" -> managedAssistants(id(args.get("target_community_id")));
            case "set_community_moderator_assistant" -> setAuthorizedAssistant(id(args.get("target_community_id")), id(args.get("target_user_id")), args);
            case "remove_community_moderator_assistant" -> {
                Long communityId = id(args.get("target_community_id"));
                communityAuthorization.requireOwner(communityId);
                removeAssistant(communityId, id(args.get("target_user_id")));
                yield true;
            }
            case "set_community_lead_moderator" -> setAuthorizedLeadModerator(id(args.get("target_community_id")), id(args.get("target_user_id")));
            case "get_my_event_creation_groups" -> creationGroups(me);
            case "get_my_events" -> myEvents(me);
            case "create_group_event" -> createGroupEvent(me, args);
            case "moderate_group_event" -> moderateAuthorizedEvent(id(args.get("target_event_id")), str(args.get("target_status")), str(args.get("target_rejection_reason")));
            case "cancel_event" -> cancelEvent(me, id(args.get("target_event_id")), str(args.get("target_reason")));
            case "request_event_participation" -> requestParticipation(me, id(args.get("target_event_id")));
            case "cancel_event_participation" -> cancelParticipation(me, id(args.get("target_event_id")));
            case "get_event_participation_summary" -> participationSummary(id(args.get("target_event_id")));
            case "get_event_participant_roster" -> authorizedRoster(id(args.get("target_event_id")));
            case "review_event_participant" -> reviewParticipant(me, id(args.get("target_participant_id")), str(args.get("target_status")));
            case "mark_event_participant_no_show" -> setParticipantStatus(id(args.get("target_event_id")), id(args.get("target_user_id")), EventParticipantStatus.NO_SHOW);
            case "check_in_event_participant" -> setParticipantStatus(id(args.get("target_event_id")), id(args.get("target_user_id")), EventParticipantStatus.CHECKED_IN);
            case "get_event_chat_messages" -> chatMessages(id(args.get("target_event_id")));
            case "get_public_event_share" -> eventShare(id(args.get("target_event_id")));
            case "get_my_profile_plans" -> myPlans(me, instant(args.get("range_start")), instant(args.get("range_end")));
            case "get_story_feed" -> storyFeed(me);
            case "get_story_groups" -> storyGroups(me);
            case "get_story_detail" -> storyDetail(id(args.get("target_story_id")));
            case "mark_story_viewed" -> markStoryViewed(me, id(args.get("target_story_id")));
            case "get_story_viewers" -> storyViewers(me, id(args.get("target_story_id")));
            case "set_story_reaction" -> setStoryReaction(me, id(args.get("target_story_id")), str(args.get("target_reaction_type")));
            case "get_story_reactions" -> storyReactions(me, id(args.get("target_story_id")));
            case "remove_story_reaction" -> removeStoryReaction(me, id(args.get("target_story_id")));
            case "search_hashtags" -> searchHashtags(str(args.get("target_query")), num(args.get("result_limit"), 20));
            case "create_story_with_audience" -> createStory(me, args);
            case "get_user_reviews" -> userReviews(id(args.get("target_user_id")));
            case "get_city_radar" -> cityRadar(me, str(args.get("target_city")));
            case "set_city_event_interest" -> setCityInterest(me, id(args.get("target_event_id")), bool(args.get("target_looking_for_company")));
            case "clear_city_event_interest" -> clearCityInterest(me, id(args.get("target_event_id")));
            case "get_city_event_ticket_offers" -> ticketOffers(id(args.get("target_event_id")));
            case "get_event_tickets" -> eventTickets(id(args.get("target_event_id")));
            case "create_ticket_order" -> createTicketOrder(me, args);
            case "initiate_payment" -> initiatePayment(me, args);
            case "handle_payment_callback" -> handlePaymentCallback(args);
            case "get_my_tickets" -> myTickets(me);
            case "get_order" -> orderDetail(me, id(args.get("target_order_id")));
            case "cancel_order" -> cancelTicketOrder(me, id(args.get("target_order_id")));
            case "issue_partner_offer_redemption" -> issueRedemption(me, id(args.get("target_offer_id")));
            case "redeem_partner_offer" -> redeemOffer(str(args.get("redemption_code")), str(args.get("target_code")));
            case "register_current_device_push_token" -> registerPush(me, args);
            case "deactivate_current_device_push_token" -> deactivatePush(str(args.get("target_expo_push_token")));
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bilinmeyen işlem: " + name);
        };
    }

    private List<Map<String, Object>> searchProfiles(Profile me, String query, int limit) {
        String like = "%" + (query == null ? "" : query.toLowerCase()) + "%";
        List<Profile> profiles = em
            .createQuery(
                "select p from Profile p left join fetch p.user where p.status = :status and (lower(p.displayName) like :q or lower(p.username) like :q)",
                Profile.class
            )
            .setParameter("status", ProfileStatus.ACTIVE)
            .setParameter("q", like)
            .setMaxResults(Math.max(limit, 1))
            .getResultList();
        return profiles.stream().map(profile -> {
            Map<String, Object> card = support.profileEmbed(profile);
            card.put("follower_count", count("select count(f) from Follow f where f.followed = :p", profile));
            card.put("following_count", count("select count(f) from Follow f where f.follower = :p", profile));
            card.put("is_following", followRelation(me, profile.getId()).equals("following"));
            return card;
        }).toList();
    }

    private Map<String, Object> followSummary(Long profileId) {
        Profile profile = support.requireProfile(profileId);
        return Map.of(
            "followers",
            count("select count(f) from Follow f where f.followed = :p", profile),
            "following",
            count("select count(f) from Follow f where f.follower = :p", profile)
        );
    }

    private List<Map<String, Object>> honorBadges(Long profileId) {
        return em
            .createQuery("select b from UserHonorBadge b left join fetch b.badge where b.user.id = :id", UserHonorBadge.class)
            .setParameter("id", profileId)
            .getResultList()
            .stream()
            .map(support::toMap)
            .toList();
    }

    private List<Map<String, Object>> reviewableEvents(Profile me, Long targetId) {
        return em
            .createQuery(
                "select e from Event e where e.createdBy.id = :target and e.status = :status",
                Event.class
            )
            .setParameter("target", targetId)
            .setParameter("status", EventStatus.COMPLETED)
            .getResultList()
            .stream()
            .map(support::toMap)
            .toList();
    }

    private String setFollow(Profile me, Long targetId, boolean shouldFollow) {
        if (me.getId().equals(targetId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Users cannot follow themselves");
        }
        Profile target = support.requireProfile(targetId);
        Follow existing = one(
            "select f from Follow f where f.follower = :me and f.followed = :target",
            Follow.class,
            Map.of("me", me, "target", target)
        );
        FollowRequest request = one(
            "select r from FollowRequest r where r.requester = :me and r.targetUser = :target",
            FollowRequest.class,
            Map.of("me", me, "target", target)
        );
        if (!shouldFollow) {
            if (existing != null) em.remove(existing);
            if (request != null) em.remove(request);
            return "none";
        }
        if (existing != null) {
            return "following";
        }
        AccountPreferences preferences = one(
            "select p from AccountPreferences p where p.profile = :target",
            AccountPreferences.class,
            Map.of("target", target)
        );
        if (preferences != null && Boolean.TRUE.equals(preferences.getRequireFollowApproval())) {
            if (request == null) {
                FollowRequest created = new FollowRequest();
                created.setRequester(me);
                created.setTargetUser(target);
                created.setCreatedAt(Instant.now());
                em.persist(created);
                publish(NotificationEventType.FOLLOW_REQUEST, "follow-request:" + created.getId(), me, target.getUser().getId(),
                    "PROFILE", me.getId(), "/user/" + me.getId(), Map.of());
            }
            return "requested";
        }
        Follow follow = new Follow();
        follow.setFollower(me);
        follow.setFollowed(target);
        follow.setCreatedAt(Instant.now());
        em.persist(follow);
        publish(NotificationEventType.NEW_FOLLOWER, "follow:" + me.getId() + ":" + target.getId(), me,
            target.getUser().getId(), "PROFILE", me.getId(), "/user/" + me.getId(), Map.of());
        return "following";
    }

    private String followRelation(Profile me, Long targetId) {
        long follows = count("select count(f) from Follow f where f.follower = :p and f.followed.id = :id", me, targetId);
        if (follows > 0) return "following";
        long requests = em
            .createQuery("select count(r) from FollowRequest r where r.requester = :p and r.targetUser.id = :id", Long.class)
            .setParameter("p", me)
            .setParameter("id", targetId)
            .getSingleResult();
        return requests > 0 ? "requested" : "none";
    }

    private List<Map<String, Object>> myFollowRequests(Profile me) {
        return em
            .createQuery("select r from FollowRequest r left join fetch r.requester where r.targetUser = :me", FollowRequest.class)
            .setParameter("me", me)
            .getResultList()
            .stream()
            .map(request -> {
                Map<String, Object> row = support.profileEmbed(request.getRequester());
                row.put("request_id", support.stringify(request.getId()));
                row.put("created_at", request.getCreatedAt().toString());
                row.put("follower_count", count("select count(f) from Follow f where f.followed = :p", request.getRequester()));
                row.put("following_count", count("select count(f) from Follow f where f.follower = :p", request.getRequester()));
                return row;
            })
            .toList();
    }

    private boolean reviewFollowRequest(Profile me, Long requestId, String decision) {
        FollowRequest request = em.find(FollowRequest.class, requestId);
        if (request == null || !request.getTargetUser().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "İstek bulunamadı");
        }
        boolean approved = "approved".equalsIgnoreCase(decision) || "approve".equalsIgnoreCase(decision);
        Profile requester = request.getRequester();
        if (approved) {
            Follow follow = new Follow();
            follow.setFollower(request.getRequester());
            follow.setFollowed(me);
            follow.setCreatedAt(Instant.now());
            em.persist(follow);
        }
        publish(approved ? NotificationEventType.FOLLOW_ACCEPTED : NotificationEventType.FOLLOW_REJECTED,
            "follow-request-review:" + requestId + ":" + approved, me, requester.getUser().getId(),
            "PROFILE", me.getId(), "/user/" + me.getId(), Map.of());
        em.remove(request);
        return true;
    }

    private List<Map<String, Object>> followConnections(Profile me, Long profileId, String kind) {
        Profile profile = support.requireProfile(profileId);
        boolean wantFollowing = "following".equalsIgnoreCase(kind);
        List<Profile> followers = wantFollowing ? List.of() : em.createQuery("select f.follower from Follow f where f.followed = :p", Profile.class).setParameter("p", profile).getResultList();
        List<Profile> following = wantFollowing ? em.createQuery("select f.followed from Follow f where f.follower = :p", Profile.class).setParameter("p", profile).getResultList() : List.of();

        List<Map<String, Object>> rows = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        for (Profile person : followers) {
            if (!seen.add(person.getId())) continue;
            rows.add(enrichConnection(person, me));
        }
        for (Profile person : following) {
            if (!seen.add(person.getId())) continue;
            rows.add(enrichConnection(person, me));
        }
        return rows;
    }

    private Map<String, Object> enrichConnection(Profile person, Profile me) {
        Map<String, Object> row = support.profileEmbed(person);
        row.put("follower_count", count("select count(f) from Follow f where f.followed = :p", person));
        row.put("following_count", count("select count(f) from Follow f where f.follower = :p", person));
        row.put("is_following", followRelation(me, person.getId()).equals("following"));
        return row;
    }

    private boolean setBlock(Profile me, Long targetId, boolean shouldBlock) {
        Profile target = support.requireProfile(targetId);
        Block existing = one("select b from Block b where b.blocker = :me and b.blockedUser = :target", Block.class, Map.of("me", me, "target", target));
        if (shouldBlock && existing == null) {
            Block block = new Block();
            block.setBlocker(me);
            block.setBlockedUser(target);
            block.setCreatedAt(Instant.now());
            em.persist(block);
        } else if (!shouldBlock && existing != null) {
            em.remove(existing);
        }
        return true;
    }

    private List<Map<String, Object>> blockedProfiles(Profile me) {
        return em
            .createQuery("select b.blockedUser from Block b where b.blocker = :me", Profile.class)
            .setParameter("me", me)
            .getResultList()
            .stream()
            .map(support::profileEmbed)
            .toList();
    }

    private String joinCommunity(Profile me, Long communityId) {
        Community community = em.find(Community.class, communityId);
        CommunityMember existing = membership(me, communityId);
        if (existing != null) {
            return existing.getStatus().name().toLowerCase();
        }
        CommunityMember member = new CommunityMember();
        member.setCommunity(community);
        member.setUser(me);
        member.setRole(CommunityMemberRole.MEMBER);
        boolean open = community.getVisibility() == CommunityVisibility.PUBLIC;
        member.setStatus(open ? CommunityMemberStatus.APPROVED : CommunityMemberStatus.PENDING);
        member.setCreatedAt(Instant.now());
        em.persist(member);
        if (!open) {
            publish(NotificationEventType.COMMUNITY_JOIN_REQUEST, "community-join-request:" + member.getId(), me, null,
                "COMMUNITY", community.getId(), "/communities/" + community.getId(), Map.of("communityId", community.getId()));
        }
        return member.getStatus().name().toLowerCase();
    }

    private List<Map<String, Object>> myCommunityMemberships(Profile me) {
        return em
            .createQuery("select m from CommunityMember m where m.user = :me", CommunityMember.class)
            .setParameter("me", me)
            .getResultList()
            .stream()
            .map(support::toMap)
            .toList();
    }

    private List<Map<String, Object>> communitiesWithMyMembership(Profile me) {
        List<CommunityMember> membershipRows = em
            .createQuery("select m from CommunityMember m where m.user = :me", CommunityMember.class)
            .setParameter("me", me)
            .getResultList();
        Map<Long, CommunityMember> memberships = new HashMap<>();
        for (CommunityMember membership : membershipRows) {
            Community target = membership.getCommunity();
            Long rootId = target.getParent() != null
                ? target.getParent().getId()
                : target.getCategoryHub() != null ? target.getCategoryHub().getId() : target.getId();
            memberships.merge(rootId, membership, (current, candidate) ->
                membershipPriority(candidate) > membershipPriority(current) ? candidate : current);
        }
        return em
            .createQuery(
                "select c from Community c where c.parent is null and c.name is not null and trim(c.name) <> '' " +
                "and c.slug is not null and trim(c.slug) <> '' order by c.createdAt desc",
                Community.class
            )
            .getResultList()
            .stream()
            .map(community -> {
                Map<String, Object> row = new java.util.LinkedHashMap<>(support.toMap(community));
                CommunityMember membership = memberships.get(community.getId());
                row.put("id", String.valueOf(community.getId()));
                row.put("community_id", String.valueOf(community.getId()));
                row.put("membership_status", membership == null ? null : membership.getStatus().name().toLowerCase(Locale.ROOT));
                row.put("membership_role", membership == null ? null : membership.getRole().name().toLowerCase(Locale.ROOT));
                row.put("is_member", membership != null && membership.getStatus() == CommunityMemberStatus.APPROVED);
                return row;
            })
            .toList();
    }

    private int membershipPriority(CommunityMember membership) {
        return switch (membership.getStatus()) {
            case APPROVED -> 4;
            case PENDING -> 3;
            case REJECTED -> 2;
            case BLOCKED -> 1;
        };
    }

    private boolean leaveCommunity(Profile me, Long communityId) {
        CommunityMember member = membership(me, communityId);
        if (member != null) em.remove(member);
        return true;
    }

    private boolean cancelMembership(Profile me, Long communityId) {
        return leaveCommunity(me, communityId);
    }

    private boolean reviewMembership(Profile actor, Long membershipId, String status) {
        CommunityMember member = em.find(CommunityMember.class, membershipId);
        if (member == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        communityAuthorization.requireManageMembers(member.getCommunity().getId());
        requireMutableTarget(member.getUser());
        CommunityMemberStatus newStatus = CommunityMemberStatus.valueOf(status.toUpperCase());
        member.setStatus(newStatus);
        NotificationEventType type = newStatus == CommunityMemberStatus.APPROVED
            ? NotificationEventType.COMMUNITY_JOIN_APPROVED : NotificationEventType.COMMUNITY_JOIN_REJECTED;
        publish(type, "community-join-review:" + membershipId + ":" + newStatus, actor, member.getUser().getUser().getId(),
            "COMMUNITY", member.getCommunity().getId(), "/communities/" + member.getCommunity().getId(), Map.of());
        return true;
    }

    private List<Map<String, Object>> memberDirectory(Long communityId) {
        return em
            .createQuery("select m from CommunityMember m left join fetch m.user where m.community.id = :id", CommunityMember.class)
            .setParameter("id", communityId)
            .getResultList()
            .stream()
            .map(member -> {
                Map<String, Object> row = support.profileEmbed(member.getUser());
                row.put("membership_id", support.stringify(member.getId()));
                row.put("role", member.getRole().name().toLowerCase());
                row.put("status", member.getStatus().name().toLowerCase());
                return row;
            })
            .toList();
    }

    private List<Map<String, Object>> pendingMemberships(Long rootId) {
        return em
            .createQuery(
                "select m from CommunityMember m left join fetch m.user left join fetch m.community c where m.status = :status and (c.id = :id or c.parent.id = :id)",
                CommunityMember.class
            )
            .setParameter("status", CommunityMemberStatus.PENDING)
            .setParameter("id", rootId)
            .getResultList()
            .stream()
            .map(support::toMap)
            .toList();
    }

    private Map<String, Object> assistantPermissions(Profile me, Long communityId) {
        CommunityModeratorAssistant assistant = one(
            "select a from CommunityModeratorAssistant a where a.community.id = :id and a.user = :me",
            CommunityModeratorAssistant.class,
            Map.of("id", communityId, "me", me)
        );
        if (assistant == null) {
            return Map.of("is_assistant", false);
        }
        Map<String, Object> map = support.toMap(assistant);
        map.put("is_assistant", true);
        return map;
    }

    private List<Map<String, Object>> assistants(Long communityId) {
        return em
            .createQuery("select a from CommunityModeratorAssistant a left join fetch a.user where a.community.id = :id", CommunityModeratorAssistant.class)
            .setParameter("id", communityId)
            .getResultList()
            .stream()
            .map(support::toMap)
            .toList();
    }

    private boolean setAssistant(Long communityId, Long userId, Map<String, Object> args) {
        CommunityModeratorAssistant assistant = one(
            "select a from CommunityModeratorAssistant a where a.community.id = :id and a.user.id = :user",
            CommunityModeratorAssistant.class,
            Map.of("id", communityId, "user", userId)
        );
        if (assistant == null) {
            assistant = new CommunityModeratorAssistant();
            assistant.setCommunity(em.getReference(Community.class, communityId));
            assistant.setUser(em.getReference(Profile.class, userId));
            assistant.setCreatedAt(Instant.now());
        }
        assistant.setCanManageGroups(bool(args.get("can_manage_groups"), true));
        assistant.setCanReviewEvents(bool(args.get("can_review_events"), true));
        assistant.setCanManageParticipants(bool(args.get("can_manage_members"), true));
        assistant.setUpdatedAt(Instant.now());
        em.merge(assistant);
        return true;
    }

    private boolean setAuthorizedAssistant(Long communityId, Long userId, Map<String, Object> args) {
        communityAuthorization.requireOwner(communityId);
        requireMutableTarget(em.find(Profile.class, userId));
        return setAssistant(communityId, userId, args);
    }

    private List<Map<String, Object>> managedAssistants(Long communityId) {
        communityAuthorization.requireManageCommunity(communityId);
        return assistants(communityId);
    }

    private List<Map<String, Object>> managedMemberDirectory(Long communityId) {
        communityAuthorization.requireManageMembers(communityId);
        return memberDirectory(communityId);
    }

    private List<Map<String, Object>> managedPendingMemberships(Long communityId) {
        communityAuthorization.requireManageMembers(communityId);
        return pendingMemberships(communityId);
    }

    private void removeAssistant(Long communityId, Long userId) {
        CommunityModeratorAssistant assistant = one(
            "select a from CommunityModeratorAssistant a where a.community.id = :id and a.user.id = :user",
            CommunityModeratorAssistant.class,
            Map.of("id", communityId, "user", userId)
        );
        if (assistant != null) em.remove(assistant);
    }

    private boolean setLeadModerator(Long communityId, Long userId) {
        Community community = em.find(Community.class, communityId);
        community.setLeadModerator(em.getReference(Profile.class, userId));
        community.setUpdatedAt(Instant.now());
        return true;
    }

    private boolean setAuthorizedLeadModerator(Long communityId, Long userId) {
        communityAuthorization.requireOwner(communityId);
        requireMutableTarget(em.find(Profile.class, userId));
        return setLeadModerator(communityId, userId);
    }

    private List<Map<String, Object>> creationGroups(Profile me) {
        return em
            .createQuery(
                "select c from Community c where c.communityType = :type and (c.createdBy = :me or exists (select m from CommunityMember m where m.community = c and m.user = :me and m.status = :status and m.role <> :member))",
                Community.class
            )
            .setParameter("type", CommunityType.GROUP)
            .setParameter("me", me)
            .setParameter("status", CommunityMemberStatus.APPROVED)
            .setParameter("member", CommunityMemberRole.MEMBER)
            .getResultList()
            .stream()
            .map(group -> {
                Map<String, Object> map = support.toMap(group);
                map.put("creation_mode", "direct");
                return map;
            })
            .toList();
    }

    private List<Map<String, Object>> myEvents(Profile me) {
        List<Event> events = em
            .createQuery(
                "select distinct e from Event e left join EventParticipant p on p.event = e " +
                "where e.status in :statuses and (e.createdBy = :me or p.user = :me) order by e.startsAt desc",
                Event.class
            )
            .setParameter("statuses", List.of(EventStatus.PUBLISHED, EventStatus.COMPLETED))
            .setParameter("me", me)
            .setMaxResults(50)
            .getResultList();
        return events.stream().map(event -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("event_id", support.stringify(event.getId()));
            row.put("title", event.getTitle());
            row.put("starts_at", event.getStartsAt() == null ? null : event.getStartsAt().toString());
            return row;
        }).toList();
    }

    private List<Map<String, Object>> createGroupEvent(Profile me, Map<String, Object> args) {
        Community community = em.find(Community.class, id(args.get("target_community_id")));
        boolean moderator = membership(me, community.getId()) != null && membership(me, community.getId()).getRole() != CommunityMemberRole.MEMBER;
        Event event = new Event();
        event.setTitle(str(args.get("target_title")));
        event.setDescription(str(args.get("target_description")));
        event.setStartsAt(Instant.parse(str(args.get("target_starts_at"))));
        if (args.get("target_ends_at") != null) event.setEndsAt(Instant.parse(str(args.get("target_ends_at"))));
        event.setLocationName(str(args.get("target_location_name")));
        event.setAddressText(str(args.get("target_address_text")));
        if (args.get("target_latitude") != null) event.setLatitude(new java.math.BigDecimal(String.valueOf(args.get("target_latitude"))));
        if (args.get("target_longitude") != null) event.setLongitude(new java.math.BigDecimal(String.valueOf(args.get("target_longitude"))));
        if (args.get("target_capacity") != null) event.setCapacity(Integer.valueOf(String.valueOf(args.get("target_capacity"))));
        event.setCoverImageUrl(str(args.get("target_cover_image_url")));
        event.setCommunity(community);
        event.setCreatedBy(me);
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());
        event.setPublishedToDiscovery(moderator);
        event.setGroupModerationStatus(moderator ? GroupModerationStatus.APPROVED : GroupModerationStatus.PENDING);
        event.setPlatformModerationStatus(PlatformModerationStatus.NOT_REQUIRED);
        event.setStatus(moderator ? EventStatus.PUBLISHED : EventStatus.PENDING_APPROVAL);
        if (moderator) event.setPublishedAt(Instant.now());
        em.persist(event);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("event_id", support.stringify(event.getId()));
        result.put("event_status", event.getStatus().name().toLowerCase());
        result.put("creation_mode", moderator ? "direct" : "proposal");
        return List.of(result);
    }

    private boolean moderateEvent(Long eventId, String status, String reason) {
        Event event = em.find(Event.class, eventId);
        if ("published".equalsIgnoreCase(status) || "approved".equalsIgnoreCase(status)) {
            event.setGroupModerationStatus(GroupModerationStatus.APPROVED);
            event.setStatus(EventStatus.PUBLISHED);
            event.setPublishedAt(Instant.now());
            event.setPublishedToDiscovery(true);
        } else {
            event.setGroupModerationStatus(GroupModerationStatus.REJECTED);
            event.setStatus(EventStatus.REJECTED);
            event.setRejectionReason(reason);
        }
        event.setUpdatedAt(Instant.now());
        return true;
    }

    private boolean moderateAuthorizedEvent(Long eventId, String status, String reason) {
        Event event = em.find(Event.class, eventId);
        if (event == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        communityAuthorization.requireReviewEvents(event.getCommunity().getId());
        return moderateEvent(eventId, status, reason);
    }

    private void requireMutableTarget(Profile profile) {
        if (profile == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        boolean targetIsSuperAdmin = profile.getUser().getAuthorities().stream()
            .anyMatch(authority -> AuthoritiesConstants.SUPER_ADMIN.equals(authority.getName()));
        if (targetIsSuperAdmin && !communityAuthorization.isSuperAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "SUPER ADMIN üzerinde işlem yapılamaz.");
        }
    }

    private boolean cancelEvent(Profile actor, Long eventId, String reason) {
        Event event = em.find(Event.class, eventId);
        if (event == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        boolean creator = event.getCreatedBy() != null && event.getCreatedBy().getId().equals(actor.getId());
        if (!creator) communityAuthorization.requireReviewEvents(event.getCommunity().getId());
        event.setStatus(EventStatus.CANCELLED);
        event.setCancelledAt(Instant.now());
        event.setCancellationReason(reason);
        event.setUpdatedAt(Instant.now());
        List<Long> recipients = em.createQuery(
            "select distinct p.user.user.id from EventParticipant p where p.event.id = :event and p.status in :statuses", Long.class)
            .setParameter("event", eventId)
            .setParameter("statuses", List.of(EventParticipantStatus.APPROVED, EventParticipantStatus.CHECKED_IN, EventParticipantStatus.PENDING))
            .getResultList();
        publishMany(NotificationEventType.EVENT_CANCELLED, "event-cancelled:" + eventId + ":" + event.getCancelledAt(), actor,
            recipients, "EVENT", eventId, "/events/" + eventId, Map.of("reason", reason == null ? "" : reason));
        return true;
    }

    private String requestParticipation(Profile me, Long eventId) {
        Event event = em.find(Event.class, eventId);
        EventParticipant existing = one(
            "select p from EventParticipant p where p.event = :event and p.user = :me",
            EventParticipant.class,
            Map.of("event", event, "me", me)
        );
        if (existing != null) return existing.getStatus().name().toLowerCase();
        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(me);
        participant.setStatus(EventParticipantStatus.PENDING);
        participant.setCreatedAt(Instant.now());
        participant.setUpdatedAt(Instant.now());
        em.persist(participant);
        Profile owner = event.getCreatedBy();
        if (owner != null) {
            publish(NotificationEventType.EVENT_JOIN_REQUEST, "event-join-request:" + participant.getId(), me,
                owner.getUser().getId(), "EVENT", eventId, "/events/" + eventId, Map.of());
        }
        return "pending";
    }

    private boolean cancelParticipation(Profile me, Long eventId) {
        EventParticipant participant = one(
            "select p from EventParticipant p where p.event.id = :event and p.user = :me",
            EventParticipant.class,
            Map.of("event", eventId, "me", me)
        );
        if (participant != null) {
            participant.setStatus(EventParticipantStatus.CANCELLED);
            participant.setUpdatedAt(Instant.now());
        }
        return true;
    }

    private Map<String, Object> participationSummary(Long eventId) {
        Map<String, Object> summary = new LinkedHashMap<>();
        for (EventParticipantStatus status : EventParticipantStatus.values()) {
            Long total = em
                .createQuery("select count(p) from EventParticipant p where p.event.id = :id and p.status = :status", Long.class)
                .setParameter("id", eventId)
                .setParameter("status", status)
                .getSingleResult();
            summary.put(status.name().toLowerCase(), total);
        }
        return summary;
    }

    private List<Map<String, Object>> roster(Long eventId) {
        return em
            .createQuery("select p from EventParticipant p left join fetch p.user where p.event.id = :id", EventParticipant.class)
            .setParameter("id", eventId)
            .getResultList()
            .stream()
            .map(participant -> {
                Map<String, Object> row = support.profileEmbed(participant.getUser());
                row.put("participant_id", support.stringify(participant.getId()));
                row.put("status", participant.getStatus().name().toLowerCase());
                return row;
            })
            .toList();
    }

    private List<Map<String, Object>> authorizedRoster(Long eventId) {
        Event event = em.find(Event.class, eventId);
        if (event == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        communityAuthorization.requireManageMembers(event.getCommunity().getId());
        return roster(eventId);
    }

    private boolean reviewParticipant(Profile actor, Long participantId, String status) {
        EventParticipant participant = em.find(EventParticipant.class, participantId);
        if (participant == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        communityAuthorization.requireManageMembers(participant.getEvent().getCommunity().getId());
        EventParticipantStatus newStatus = EventParticipantStatus.valueOf(status.toUpperCase());
        participant.setStatus(newStatus);
        participant.setUpdatedAt(Instant.now());
        NotificationEventType type = newStatus == EventParticipantStatus.APPROVED
            ? NotificationEventType.EVENT_JOIN_APPROVED : NotificationEventType.EVENT_JOIN_REJECTED;
        publish(type, "event-join-review:" + participantId + ":" + newStatus, actor, participant.getUser().getUser().getId(),
            "EVENT", participant.getEvent().getId(), "/events/" + participant.getEvent().getId(), Map.of());
        return true;
    }

    private void publish(NotificationEventType type, String key, Profile actor, Long recipientUserId,
                         String referenceType, Object referenceId, String route, Map<String, Object> extra) {
        Map<String, Object> variables = notificationVariables(actor, referenceType, referenceId, route, extra);
        if (recipientUserId != null) variables.put("recipientUserId", recipientUserId);
        notificationEvents.publish(new NotificationEvent(type, key, variables));
    }

    private void publishMany(NotificationEventType type, String key, Profile actor, List<Long> recipients,
                             String referenceType, Object referenceId, String route, Map<String, Object> extra) {
        Map<String, Object> variables = notificationVariables(actor, referenceType, referenceId, route, extra);
        variables.put("recipientUserIds", recipients);
        notificationEvents.publish(new NotificationEvent(type, key, variables));
    }

    private Map<String, Object> notificationVariables(Profile actor, String referenceType, Object referenceId,
                                                       String route, Map<String, Object> extra) {
        Map<String, Object> variables = new LinkedHashMap<>(extra);
        if (actor != null && actor.getUser() != null) {
            variables.put("actorUserId", actor.getUser().getId());
            variables.put("actorProfileId", actor.getId());
            variables.put("actorName", actor.getDisplayName() != null ? actor.getDisplayName() : actor.getUsername());
        }
        variables.put("referenceType", referenceType);
        variables.put("referenceId", referenceId);
        variables.put("route", route);
        return variables;
    }

    private boolean setParticipantStatus(Long eventId, Long userId, EventParticipantStatus status) {
        Event event = em.find(Event.class, eventId);
        if (event == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        communityAuthorization.requireManageMembers(event.getCommunity().getId());
        EventParticipant participant = one(
            "select p from EventParticipant p where p.event.id = :event and p.user.id = :user",
            EventParticipant.class,
            Map.of("event", eventId, "user", userId)
        );
        if (participant != null) {
            participant.setStatus(status);
            participant.setUpdatedAt(Instant.now());
        }
        return true;
    }

    private List<Map<String, Object>> chatMessages(Long eventId) {
        return em
            .createQuery("select m from EventMessage m left join fetch m.author where m.event.id = :id order by m.createdAt", EventMessage.class)
            .setParameter("id", eventId)
            .getResultList()
            .stream()
            .map(message -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("message_id", support.stringify(message.getId()));
                row.put("author_id", support.stringify(message.getAuthor().getId()));
                row.put("display_name", message.getAuthor().getDisplayName());
                row.put("avatar_url", message.getAuthor().getAvatarUrl());
                row.put("body", message.getBody());
                row.put("created_at", message.getCreatedAt().toString());
                return row;
            })
            .toList();
    }

    private Map<String, Object> eventShare(Long eventId) {
        Event event = em.find(Event.class, eventId);
        return support.toMap(event);
    }

    private List<Map<String, Object>> myPlans(Profile me, Instant rangeStart, Instant rangeEnd) {
        Instant from = rangeStart != null ? rangeStart : Instant.now().minus(6, ChronoUnit.MONTHS);
        Instant to = rangeEnd != null ? rangeEnd : Instant.now().plus(12, ChronoUnit.MONTHS);
        List<Event> events = em
            .createQuery(
                "select distinct e from Event e left join fetch e.community c where e.startsAt < :to " +
                "and ((e.endsAt is null and e.startsAt >= :from) or e.endsAt >= :from) and (" +
                "exists (select p.id from EventParticipant p where p.event = e and p.user = :me) or " +
                "exists (select m.id from CommunityMember m where m.community = c and m.user = :me and m.status = :memberStatus))",
                Event.class
            )
            .setParameter("me", me)
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("memberStatus", CommunityMemberStatus.APPROVED)
            .getResultList()
            ;
        return events.stream()
            .map(event -> {
                EventParticipant participant = one(
                    "select p from EventParticipant p where p.event = :event and p.user = :me",
                    EventParticipant.class,
                    Map.of("event", event, "me", me)
                );
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("event_id", support.stringify(event.getId()));
                row.put("title", event.getTitle());
                row.put("starts_at", event.getStartsAt().toString());
                row.put("ends_at", event.getEndsAt() == null ? null : event.getEndsAt().toString());
                row.put("location_name", event.getLocationName());
                row.put("cover_image_url", event.getCoverImageUrl());
                row.put("event_status", event.getStatus().name().toLowerCase());
                row.put("participation_status", participant == null ? "community" : participant.getStatus().name().toLowerCase());
                row.put("community_name", event.getCommunity() == null ? "" : event.getCommunity().getName());
                row.put("source", participant == null ? "community" : "participating");
                row.put(
                    "event_type",
                    event.getCommunity() == null || event.getCommunity().getCommunityType() == null
                        ? "general"
                        : event.getCommunity().getCommunityType().name().toLowerCase(Locale.ROOT)
                );
                return row;
            })
            .toList();
    }

    private List<Map<String, Object>> storyFeed(Profile me) {
        return storyGroups(me);
    }

    private List<Map<String, Object>> storyGroups(Profile me) {
        Instant now = Instant.now();
        List<StoryGroup> groups = em
            .createQuery(
                "select distinct g from StoryGroup g " +
                "left join fetch g.author " +
                "left join fetch g.community " +
                "left join fetch g.event " +
                "left join g.stories s " +
                "where s.expiresAt > :now " +
                "order by g.createdAt desc",
                StoryGroup.class
            )
            .setParameter("now", now)
            .setMaxResults(50)
            .getResultList();
        Set<Long> viewed = new HashSet<>(
            em
                .createQuery("select v.story.id from StoryView v where v.viewer = :me", Long.class)
                .setParameter("me", me)
                .getResultList()
        );
        Map<Long, List<Story>> storiesByGroup = new HashMap<>();
        for (StoryGroup group : groups) {
            List<Story> groupStories = em
                .createQuery(
                    "select s from Story s left join fetch s.author where s.storyGroup.id = :id and s.expiresAt > :now order by s.createdAt",
                    Story.class
                )
                .setParameter("id", group.getId())
                .setParameter("now", now)
                .getResultList();
            storiesByGroup.put(group.getId(), groupStories);
        }
        return groups.stream().map(group -> groupRow(group, viewed, storiesByGroup.getOrDefault(group.getId(), List.of()))).toList();
    }

    private Object storyDetail(Long storyId) {
        Story story = em.find(Story.class, storyId);
        if (story == null) {
            return List.of();
        }
        Profile me = support.currentProfile();
        boolean viewed = one(
            "select v from StoryView v where v.story.id = :id and v.viewer = :me",
            StoryView.class,
            Map.of("id", storyId, "me", me)
        ) != null;
        return storyRow(story, viewed, true);
    }

    private Map<String, Object> groupRow(StoryGroup group, Set<Long> viewed, List<Story> stories) {
        Map<String, Object> row = new LinkedHashMap<>();
        Long groupId = group.getId();
        row.put("group_id", support.stringify(groupId));
        row.put("title", groupTitle(group));
        row.put("subtitle", groupSubtitle(group));
        row.put("context_type", groupContextType(group));
        Profile author = group.getAuthor();
        row.put("author", author == null ? Map.of() : support.profileEmbed(author));
        List<Map<String, Object>> storyRows = stories
            .stream()
            .filter(s -> s.getExpiresAt() != null && s.getExpiresAt().isAfter(Instant.now()))
            .sorted(Comparator.comparing(Story::getCreatedAt))
            .map(s -> storyRow(s, viewed.contains(s.getId()), false))
            .toList();
        row.put("stories", storyRows);
        row.put("story_count", storyRows.size());
        row.put("created_at", group.getCreatedAt() == null ? null : group.getCreatedAt().toString());
        row.put("expires_at", group.getExpiresAt() == null ? null : group.getExpiresAt().toString());
        return row;
    }

    private String groupTitle(StoryGroup group) {
        if (group.getEvent() != null && group.getEvent().getTitle() != null) {
            return group.getEvent().getTitle();
        }
        if (group.getCommunity() != null && group.getCommunity().getName() != null) {
            return group.getCommunity().getName();
        }
        if (group.getLocationName() != null) {
            return group.getLocationName();
        }
        Profile author = group.getAuthor();
        return author == null || author.getDisplayName() == null ? "Story" : author.getDisplayName();
    }

    private String groupSubtitle(StoryGroup group) {
        if (group.getEvent() != null && group.getCommunity() != null && group.getCommunity().getName() != null) {
            return group.getCommunity().getName();
        }
        return null;
    }

    private String groupContextType(StoryGroup group) {
        if (group.getEvent() != null) return "event";
        if (group.getCommunity() != null) return "community";
        if (group.getLocationName() != null) return "location";
        return "user";
    }

    private Map<String, Object> storyRow(Story story, boolean viewed, boolean withDetails) {
        Profile author = story.getAuthor();
        Map<String, Object> row = new LinkedHashMap<>();
        Long storyId = story.getId();
        row.put("story_id", support.stringify(storyId));
        row.put("author_id", author == null ? null : support.stringify(author.getId()));
        row.put("display_name", author == null || author.getDisplayName() == null ? "" : author.getDisplayName());
        row.put("avatar_url", author == null ? null : author.getAvatarUrl());
        row.put("group_id", story.getStoryGroup() == null ? null : support.stringify(story.getStoryGroup().getId()));
        List<String> targetNames = story
            .getCommunityTargets()
            .stream()
            .map(StoryCommunityTarget::getCommunity)
            .filter(community -> community != null && community.getName() != null)
            .map(Community::getName)
            .sorted()
            .toList();
        String names = String.join(", ", targetNames);
        String communityName = null;
        if (Boolean.TRUE.equals(story.getIsPublic())) {
            communityName = "Herkesle paylaştı";
        } else if (Boolean.TRUE.equals(story.getShareWithFollowers()) && !names.isBlank()) {
            communityName = "Takipçiler + " + names;
        } else if (!names.isBlank()) {
            communityName = names;
        }
        row.put("community_name", communityName);
        row.put("content_type", story.getContentType() == null ? "text" : story.getContentType().name().toLowerCase(Locale.ROOT));
        row.put("body", story.getBody());
        row.put("media_url", story.getMediaUrl());
        row.put("created_at", story.getCreatedAt() == null ? null : story.getCreatedAt().toString());
        row.put("is_viewed", viewed);
        row.put("viewer_count", storyId == null ? 0 : storyViewerCount(storyId));
        row.put("reactions", storyId == null ? Map.of() : storyReactionsSummary(storyId));
        if (story.getEvent() != null) {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("event_id", support.stringify(story.getEvent().getId()));
            event.put("title", story.getEvent().getTitle());
            row.put("event", event);
        }
        if (story.getLocationName() != null) {
            Map<String, Object> location = new LinkedHashMap<>();
            location.put("name", story.getLocationName());
            location.put("latitude", story.getLatitude());
            location.put("longitude", story.getLongitude());
            row.put("location", location);
        }
        if (withDetails) {
            row.put("hashtags", story.getStoryHashtags().stream()
                .map(StoryHashtag::getHashtag)
                .filter(Objects::nonNull)
                .map(h -> Map.of("name", h.getName(), "normalized_name", h.getNormalizedName()))
                .toList());
            row.put("elements", story.getStoryElements().stream()
                .sorted(Comparator.comparing(StoryElement::getSortOrder))
                .map(this::elementRow)
                .toList());
        } else {
            row.put("hashtags", story.getStoryHashtags().stream()
                .map(StoryHashtag::getHashtag)
                .filter(Objects::nonNull)
                .map(Hashtag::getName)
                .toList());
        }
        return row;
    }

    private Map<String, Object> elementRow(StoryElement element) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("element_id", support.stringify(element.getId()));
        row.put("type", element.getElementType() == null ? null : element.getElementType().name().toLowerCase(Locale.ROOT));
        row.put("content", element.getContent());
        row.put("position_x", element.getPositionX());
        row.put("position_y", element.getPositionY());
        row.put("scale", element.getScale());
        row.put("rotation", element.getRotation());
        row.put("color", element.getColor());
        row.put("background_color", element.getBackgroundColor());
        row.put("font_size", element.getFontSize());
        row.put("width", element.getWidth());
        row.put("height", element.getHeight());
        row.put("metadata", element.getMetadataJson());
        row.put("sort_order", element.getSortOrder());
        return row;
    }

    private long storyViewerCount(Long storyId) {
        return em.createQuery("select count(v) from StoryView v where v.story.id = :id", Long.class)
            .setParameter("id", storyId)
            .getSingleResult();
    }

    private Map<String, Long> storyReactionsSummary(Long storyId) {
        List<Object[]> rows = em.createQuery(
            "select r.reactionType, count(r) from StoryReaction r where r.story.id = :id group by r.reactionType",
            Object[].class
        ).setParameter("id", storyId).getResultList();
        Map<String, Long> summary = new LinkedHashMap<>();
        for (StoryReactionType type : StoryReactionType.values()) {
            summary.put(type.name().toLowerCase(Locale.ROOT), 0L);
        }
        for (Object[] row : rows) {
            summary.put(((StoryReactionType) row[0]).name().toLowerCase(Locale.ROOT), (Long) row[1]);
        }
        return summary;
    }

    private List<Map<String, Object>> storyViewers(Profile me, Long storyId) {
        Story story = em.find(Story.class, storyId);
        if (story == null || !story.getAuthor().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu storynin görüntüleyenlerini göremezsiniz.");
        }
        return em.createQuery(
            "select v.viewer from StoryView v where v.story.id = :id order by v.viewedAt desc",
            Profile.class
        )
            .setParameter("id", storyId)
            .getResultList()
            .stream()
            .map(viewer -> {
                Map<String, Object> row = support.profileEmbed(viewer);
                row.put("viewed_at", null);
                return row;
            })
            .toList();
    }

    private boolean setStoryReaction(Profile me, Long storyId, String reactionTypeName) {
        Story story = em.find(Story.class, storyId);
        if (story == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Story bulunamadı");
        }
        StoryReactionType reactionType;
        try {
            reactionType = StoryReactionType.valueOf(reactionTypeName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz reaction tipi");
        }
        StoryReaction existing = one(
            "select r from StoryReaction r where r.story.id = :story and r.user = :me",
            StoryReaction.class,
            Map.of("story", storyId, "me", me)
        );
        if (existing == null) {
            existing = new StoryReaction();
            existing.setStory(story);
            existing.setUser(me);
            existing.setCreatedAt(Instant.now());
        }
        existing.setReactionType(reactionType);
        em.merge(existing);
        return true;
    }

    private boolean removeStoryReaction(Profile me, Long storyId) {
        StoryReaction existing = one(
            "select r from StoryReaction r where r.story.id = :story and r.user = :me",
            StoryReaction.class,
            Map.of("story", storyId, "me", me)
        );
        if (existing != null) {
            em.remove(existing);
        }
        return true;
    }

    private Map<String, Object> storyReactions(Profile me, Long storyId) {
        Story story = em.find(Story.class, storyId);
        if (story == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Story bulunamadı");
        }
        StoryReaction mine = one(
            "select r from StoryReaction r where r.story.id = :story and r.user = :me",
            StoryReaction.class,
            Map.of("story", storyId, "me", me)
        );
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", storyReactionsSummary(storyId));
        result.put("my_reaction", mine == null ? null : mine.getReactionType().name().toLowerCase(Locale.ROOT));
        return result;
    }

    private List<Map<String, Object>> userReviews(Long targetUserId) {
        Profile target = support.requireProfile(targetUserId);
        return em.createQuery(
            "select r from UserReview r left join fetch r.reviewer where r.reviewedUser = :target order by r.createdAt desc",
            UserReview.class
        )
            .setParameter("target", target)
            .getResultList()
            .stream()
            .map(review -> {
                Map<String, Object> row = support.toMap(review);
                row.put("profiles", support.profileEmbed(review.getReviewer()));
                return row;
            })
            .toList();
    }

    private boolean markStoryViewed(Profile me, Long storyId) {
        StoryView existing = one(
            "select v from StoryView v where v.story.id = :id and v.viewer = :me",
            StoryView.class,
            Map.of("id", storyId, "me", me)
        );
        if (existing == null) {
            StoryView view = new StoryView();
            view.setStory(em.getReference(Story.class, storyId));
            view.setViewer(me);
            view.setViewedAt(Instant.now());
            em.persist(view);
        }
        return true;
    }

    private String createStory(Profile me, Map<String, Object> args) {
        Instant now = Instant.now();
        Story story = new Story();
        story.setAuthor(me);
        story.setContentType("image".equalsIgnoreCase(str(args.get("target_content_type"))) ? StoryContentType.IMAGE : StoryContentType.TEXT);
        story.setBody(str(args.get("target_body")));
        story.setMediaUrl(str(args.get("target_media_url")));
        story.setIsPublic(bool(args.get("target_is_public")));
        story.setShareWithFollowers(bool(args.get("target_share_with_followers"), true));
        story.setCreatedAt(now);
        story.setExpiresAt(now.plus(24, ChronoUnit.HOURS));

        Long eventId = id(args.get("target_event_id"));
        Event event = eventId == null ? null : em.find(Event.class, eventId);
        story.setEvent(event);

        Object location = args.get("target_location");
        if (location instanceof Map<?, ?> loc) {
            story.setLocationName(str(loc.get("name")));
            Object lat = loc.get("latitude");
            Object lng = loc.get("longitude");
            if (lat instanceof Number n) story.setLatitude(BigDecimal.valueOf(n.doubleValue()));
            if (lng instanceof Number n) story.setLongitude(BigDecimal.valueOf(n.doubleValue()));
        }

        em.persist(story);

        Long primaryCommunityId = null;
        Object communityIds = args.get("target_community_ids");
        if (communityIds instanceof List<?> ids) {
            for (Object raw : ids) {
                Long communityId = support.parseLong(raw);
                if (communityId == null) continue;
                if (primaryCommunityId == null) primaryCommunityId = communityId;
                StoryCommunityTarget target = new StoryCommunityTarget();
                target.setStory(story);
                target.setCommunity(em.getReference(Community.class, communityId));
                target.setCreatedAt(now);
                em.persist(target);
            }
        }

        if (event != null && event.getCommunity() != null && primaryCommunityId == null) {
            primaryCommunityId = event.getCommunity().getId();
        }

        StoryGroup group = resolveStoryGroup(me, event, primaryCommunityId, story.getLocationName(), story.getLatitude(), story.getLongitude(), story.getExpiresAt());
        story.setStoryGroup(group);

        attachHashtags(story, args.get("target_hashtags"), now);
        attachElements(story, args.get("target_elements"), now);

        return support.stringify(story.getId());
    }

    private StoryGroup resolveStoryGroup(Profile me, Event event, Long communityId, String locationName, BigDecimal latitude, BigDecimal longitude, Instant expiresAt) {
        Long eventId = event == null ? null : event.getId();
        Long primaryCommunityId = communityId;
        if (event != null && event.getCommunity() != null && primaryCommunityId == null) {
            primaryCommunityId = event.getCommunity().getId();
        }

        Instant now = Instant.now();
        Optional<StoryGroup> existing;
        if (eventId == null && primaryCommunityId == null) {
            existing = storyGroupRepository.findByAuthorIdAndEventIdIsNullAndCommunityIdIsNullAndExpiresAtAfter(me.getId(), now);
        } else if (eventId != null && primaryCommunityId == null) {
            existing = storyGroupRepository.findByAuthorIdAndEventIdAndCommunityIdIsNullAndExpiresAtAfter(me.getId(), eventId, now);
        } else if (eventId == null) {
            existing = storyGroupRepository.findByAuthorIdAndEventIdIsNullAndCommunityIdAndExpiresAtAfter(me.getId(), primaryCommunityId, now);
        } else {
            existing = storyGroupRepository.findByAuthorIdAndEventIdAndCommunityIdAndExpiresAtAfter(me.getId(), eventId, primaryCommunityId, now);
        }

        StoryGroup group = existing.orElse(null);
        if (group != null) {
            if (group.getExpiresAt() != null && expiresAt != null && expiresAt.isAfter(group.getExpiresAt())) {
                group.setExpiresAt(expiresAt);
            }
            return group;
        }

        group = new StoryGroup();
        group.setAuthor(me);
        group.setEvent(event);
        if (primaryCommunityId != null) {
            group.setCommunity(em.getReference(Community.class, primaryCommunityId));
        }
        group.setLocationName(locationName);
        group.setLatitude(latitude);
        group.setLongitude(longitude);
        group.setCreatedAt(now);
        group.setExpiresAt(expiresAt);
        em.persist(group);
        return group;
    }

    private void attachHashtags(Story story, Object rawHashtags, Instant now) {
        if (!(rawHashtags instanceof List<?> list)) return;
        Set<String> seen = new HashSet<>();
        for (Object raw : list) {
            String name = normalizeHashtag(str(raw));
            if (name.isBlank() || !seen.add(name)) continue;
            Hashtag hashtag = hashtagRepository
                .findByNormalizedName(name)
                .orElseGet(() -> {
                    Hashtag h = new Hashtag();
                    h.setName("#" + name);
                    h.setNormalizedName(name);
                    h.setUsageCount(0L);
                    h.setCreatedAt(now);
                    h.setUpdatedAt(now);
                    h.setIsActive(true);
                    em.persist(h);
                    return h;
                });
            hashtag.setUsageCount(hashtag.getUsageCount() + 1);
            hashtag.setUpdatedAt(now);
            StoryHashtag link = new StoryHashtag();
            link.setStory(story);
            link.setHashtag(hashtag);
            link.setCreatedAt(now);
            em.persist(link);
        }
    }

    private String normalizeHashtag(String input) {
        if (input == null) return "";
        String cleaned = input.startsWith("#") ? input.substring(1) : input;
        return cleaned.trim().toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}0-9_-]", "");
    }

    @SuppressWarnings("unchecked")
    private void attachElements(Story story, Object rawElements, Instant now) {
        if (!(rawElements instanceof List<?> list)) return;
        int order = 0;
        for (Object raw : list) {
            if (!(raw instanceof Map<?, ?> map)) continue;
            StoryElement element = new StoryElement();
            element.setStory(story);
            element.setElementType(parseElementType(str(map.get("type"))));
            element.setContent(str(map.get("content")));
            element.setPositionX(parseDouble(map.get("position_x")));
            element.setPositionY(parseDouble(map.get("position_y")));
            element.setScale(parseDouble(map.get("scale")));
            element.setRotation(parseDouble(map.get("rotation")));
            element.setColor(str(map.get("color")));
            element.setBackgroundColor(str(map.get("background_color")));
            element.setFontSize(parseInt(map.get("font_size")));
            element.setWidth(parseDouble(map.get("width")));
            element.setHeight(parseDouble(map.get("height")));
            Object metadata = map.get("metadata");
            element.setMetadataJson(metadata == null ? null : metadata.toString());
            element.setSortOrder(order++);
            element.setCreatedAt(now);
            em.persist(element);
        }
    }

    private StoryElementType parseElementType(String type) {
        if (type == null) return StoryElementType.TEXT;
        try {
            return StoryElementType.valueOf(type.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return StoryElementType.TEXT;
        }
    }

    private Double parseDouble(Object value) {
        if (value instanceof Number n) return n.doubleValue();
        return null;
    }

    private Integer parseInt(Object value) {
        if (value instanceof Number n) return n.intValue();
        return null;
    }

    private List<Map<String, Object>> searchHashtags(String query, int limit) {
        String normalized = normalizeHashtag(query);
        String like = "%" + normalized + "%";
        List<Hashtag> hashtags = em
            .createQuery("select h from Hashtag h where h.isActive = true and lower(h.normalizedName) like :q order by h.usageCount desc", Hashtag.class)
            .setParameter("q", like)
            .setMaxResults(Math.max(limit, 1))
            .getResultList();
        return hashtags.stream().map(h -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("hashtag_id", support.stringify(h.getId()));
            row.put("name", h.getName());
            row.put("normalized_name", h.getNormalizedName());
            row.put("usage_count", h.getUsageCount());
            return row;
        }).toList();
    }

    private List<Map<String, Object>> cityRadar(Profile me, String city) {
        String normalizedCity = city == null || city.isBlank() ? null : city.trim().toLowerCase(Locale.ROOT);
        String jpql =
            "select e from CityEvent e where e.status = :status and e.startsAt >= :since" +
            (normalizedCity == null ? "" : " and lower(e.city) = :city") +
            " order by e.startsAt";
        var query = em.createQuery(jpql, CityEvent.class);
        query.setParameter("status", CityEventStatus.PUBLISHED);
        query.setParameter("since", Instant.now().minus(2, ChronoUnit.HOURS));
        if (normalizedCity != null) {
            query.setParameter("city", normalizedCity);
        }
        return query.getResultList().stream().map(event -> {
            Map<String, Object> row = support.toMap(event);
            row.put("event_id", support.stringify(event.getId()));
            long interested = em.createQuery("select count(i) from CityEventInterest i where i.cityEvent = :e", Long.class).setParameter("e", event).getSingleResult();
            long companions = em.createQuery("select count(i) from CityEventInterest i where i.cityEvent = :e and i.lookingForCompany = true", Long.class).setParameter("e", event).getSingleResult();
            CityEventInterest mine = one(
                "select i from CityEventInterest i where i.cityEvent = :e and i.user = :me",
                CityEventInterest.class,
                Map.of("e", event, "me", me)
            );
            row.put("interested_count", interested);
            row.put("companion_count", companions);
            row.put("is_interested", mine != null);
            row.put("is_looking_for_company", mine != null && Boolean.TRUE.equals(mine.getLookingForCompany()));
            return row;
        }).toList();
    }

    private boolean setCityInterest(Profile me, Long eventId, boolean looking) {
        CityEvent event = em.find(CityEvent.class, eventId);
        CityEventInterest interest = one(
            "select i from CityEventInterest i where i.cityEvent = :e and i.user = :me",
            CityEventInterest.class,
            Map.of("e", event, "me", me)
        );
        if (interest == null) {
            interest = new CityEventInterest();
            interest.setCityEvent(event);
            interest.setUser(me);
            interest.setCreatedAt(Instant.now());
        }
        interest.setLookingForCompany(looking);
        interest.setUpdatedAt(Instant.now());
        em.merge(interest);
        return true;
    }

    private boolean clearCityInterest(Profile me, Long eventId) {
        CityEventInterest interest = one(
            "select i from CityEventInterest i where i.cityEvent.id = :id and i.user = :me",
            CityEventInterest.class,
            Map.of("id", eventId, "me", me)
        );
        if (interest != null) em.remove(interest);
        return true;
    }

    private List<Map<String, Object>> ticketOffers(Long eventId) {
        return em
            .createQuery("select o from CityEventTicketOffer o where o.cityEvent.id = :id", CityEventTicketOffer.class)
            .setParameter("id", eventId)
            .getResultList()
            .stream()
            .map(support::toMap)
            .toList();
    }

    private Map<String, Object> issueRedemption(Profile me, Long offerId) {
        PartnerOffer offer = em.find(PartnerOffer.class, offerId);
        PartnerOfferRedemption redemption = new PartnerOfferRedemption();
        redemption.setOffer(offer);
        redemption.setVenue(offer.getVenue());
        redemption.setUser(me);
        redemption.setToken(UUID.randomUUID());
        redemption.setRedemptionCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        redemption.setStatus(RedemptionStatus.ISSUED);
        redemption.setIssuedAt(Instant.now());
        redemption.setExpiresAt(Instant.now().plus(2, ChronoUnit.HOURS));
        em.persist(redemption);
        return support.toMap(redemption);
    }

    private Map<String, Object> redeemOffer(String code, String fallback) {
        String token = code == null ? fallback : code;
        List<PartnerOfferRedemption> matches = em
            .createQuery("select r from PartnerOfferRedemption r where r.redemptionCode = :code", PartnerOfferRedemption.class)
            .setParameter("code", token)
            .setMaxResults(1)
            .getResultList();
        if (matches.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kod bulunamadı");
        }
        PartnerOfferRedemption redemption = matches.get(0);
        redemption.setStatus(RedemptionStatus.REDEMED);
        redemption.setRedeemedAt(Instant.now());
        return support.toMap(redemption);
    }

    private boolean registerPush(Profile me, Map<String, Object> args) {
        String token = str(args.get("target_expo_push_token"));
        PushToken existing = one("select t from PushToken t where t.deviceToken = :token", PushToken.class, Map.of("token", token));
        if (existing == null) {
            existing = new PushToken();
            existing.setDeviceToken(token);
            existing.setCreatedAt(Instant.now());
        }
        existing.setUser(me);
        existing.setPlatform("ios".equalsIgnoreCase(str(args.get("target_platform"))) ? PushPlatform.IOS : PushPlatform.ANDROID);
        existing.setDeviceName(str(args.get("target_device_name")));
        existing.setIsActive(true);
        existing.setLastSeenAt(Instant.now());
        em.merge(existing);
        return true;
    }

    private boolean deactivatePush(String token) {
        PushToken existing = one("select t from PushToken t where t.deviceToken = :token", PushToken.class, Map.of("token", token));
        if (existing != null) {
            existing.setIsActive(false);
        }
        return true;
    }

    private List<Map<String, Object>> eventTickets(Long eventId) {
        return eventTicketRepository.findByEvent_Id(eventId).stream()
            .filter(t -> t.getStatus() == EventTicketStatus.ACTIVE)
            .map(ticket -> {
                Map<String, Object> row = support.toMap(ticket);
                int sold = ticket.getSoldQuantity() == null ? 0 : ticket.getSoldQuantity();
                row.put("available_quantity", Math.max(0, ticket.getQuantity() - sold));
                return row;
            })
            .toList();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> createTicketOrder(Profile me, Map<String, Object> args) {
        Object rawItems = args.get("target_items");
        if (!(rawItems instanceof List<?> list)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bilet kalemleri eksik");
        }
        List<PaymentService.TicketRequest> items = list.stream()
            .map(item -> {
                Map<String, Object> map = (Map<String, Object>) item;
                return new PaymentService.TicketRequest(
                    support.parseLong(map.get("ticket_id")),
                    support.parseLong(map.get("quantity")).intValue()
                );
            })
            .toList();
        Order order = paymentService.createOrder(me, items);
        return orderDetail(me, order.getId());
    }

    private Map<String, Object> initiatePayment(Profile me, Map<String, Object> args) {
        Long orderId = id(args.get("target_order_id"));
        String provider = str(args.get("target_provider"));
        String idempotencyKey = str(args.get("target_idempotency_key"));
        String callbackUrl = str(args.get("target_callback_url"));
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (!order.getUser().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu sipariş sizin değil");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Idempotency key gerekli");
        }
        PaymentProviderType providerType;
        try {
            providerType = PaymentProviderType.valueOf(provider == null ? "IYZICO" : provider.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz ödeme sağlayıcısı");
        }
        PaymentInitiationResult result = paymentService.initiatePayment(order, providerType, idempotencyKey,
            callbackUrl == null ? "/payments/callback" : callbackUrl);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("success", result.success());
        out.put("checkout_url", result.checkoutUrl());
        out.put("client_token", result.clientToken());
        out.put("provider_transaction_id", result.providerTransactionId());
        out.put("error", result.errorMessage());
        return out;
    }

    private Map<String, Object> handlePaymentCallback(Map<String, Object> args) {
        String transactionId = str(args.get("target_transaction_id"));
        String payload = str(args.get("target_payload"));
        String provider = str(args.get("target_provider"));
        if (transactionId == null || transactionId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction id gerekli");
        }
        PaymentProviderType providerType;
        try {
            providerType = PaymentProviderType.valueOf(provider == null ? "IYZICO" : provider.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz ödeme sağlayıcısı");
        }
        Payment payment = paymentService.handleCallback(transactionId, payload, providerType);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("payment_id", support.stringify(payment.getId()));
        out.put("status", payment.getStatus().name().toLowerCase(Locale.ROOT));
        out.put("order_id", support.stringify(payment.getOrder().getId()));
        return out;
    }

    private List<Map<String, Object>> myTickets(Profile me) {
        return ticketRepository.findByUser_Id(me.getId()).stream()
            .map(ticket -> {
                Map<String, Object> row = support.toMap(ticket);
                row.put("event", support.toMap(ticket.getEvent()));
                row.put("ticket_type", support.toMap(ticket.getOrderItem().getTicket()));
                return row;
            })
            .toList();
    }

    private Map<String, Object> orderDetail(Profile me, Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (!order.getUser().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu sipariş sizin değil");
        }
        Map<String, Object> row = support.toMap(order);
        List<Map<String, Object>> items = orderItemRepository.findByOrder_Id(orderId).stream().map(item -> {
            Map<String, Object> itemRow = support.toMap(item);
            itemRow.put("ticket", support.toMap(item.getTicket()));
            itemRow.put("event", support.toMap(item.getTicket().getEvent()));
            return itemRow;
        }).toList();
        row.put("items", items);
        Payment payment = one("select p from Payment p where p.order.id = :id", Payment.class, Map.of("id", orderId));
        if (payment != null) {
            row.put("payment", support.toMap(payment));
        }
        return row;
    }

    private boolean cancelTicketOrder(Profile me, Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (!order.getUser().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu sipariş sizin değil");
        }
        paymentService.cancelOrder(order);
        return true;
    }

    private CommunityMember membership(Profile me, Long communityId) {
        return one(
            "select m from CommunityMember m where m.user = :me and m.community.id = :id",
            CommunityMember.class,
            Map.of("me", me, "id", communityId)
        );
    }

    private void remove(Class<?> type, Long id) {
        Object entity = em.find(type, id);
        if (entity != null) em.remove(entity);
    }

    private long count(String jpql, Profile profile) {
        return em.createQuery(jpql, Long.class).setParameter("p", profile).getSingleResult();
    }

    private long count(String jpql, Profile profile, Long id) {
        return em.createQuery(jpql, Long.class).setParameter("p", profile).setParameter("id", id).getSingleResult();
    }

    private <T> T one(String jpql, Class<T> type, Map<String, Object> params) {
        var query = em.createQuery(jpql, type);
        params.forEach(query::setParameter);
        List<T> results = query.setMaxResults(1).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    private Long id(Object value) {
        return support.parseLong(value);
    }

    private String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Instant instant(Object value) {
        if (value == null || String.valueOf(value).isBlank()) return null;
        try {
            return Instant.parse(String.valueOf(value));
        } catch (java.time.format.DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz tarih aralığı");
        }
    }

    private boolean bool(Object value) {
        return bool(value, false);
    }

    private boolean bool(Object value, boolean fallback) {
        return value == null ? fallback : Boolean.parseBoolean(String.valueOf(value));
    }

    private int num(Object value, int fallback) {
        Long parsed = support.parseLong(value);
        return parsed == null ? fallback : parsed.intValue();
    }
}
