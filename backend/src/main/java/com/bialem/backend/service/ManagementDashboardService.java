package com.bialem.backend.service;

import com.bialem.backend.domain.enumeration.*;
import com.bialem.backend.service.dto.ManagementDashboardDTO;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ManagementDashboardService {
    private final EntityManager em;
    public ManagementDashboardService(EntityManager em) { this.em = em; }

    public ManagementDashboardDTO getDashboard() {
        Instant now = Instant.now();
        var users = new ManagementDashboardDTO.Users(
            count("select count(u) from User u"), count("select count(u) from User u where u.activated=true"),
            count("select count(u) from User u where u.activated=false"),
            count("select count(p) from Profile p where p.status=:value", "value", ProfileStatus.SUSPENDED),
            count("select count(u) from User u where u.createdDate>=:value", "value", now.truncatedTo(ChronoUnit.DAYS)),
            count("select count(u) from User u where u.createdDate>=:value", "value", now.minus(7, ChronoUnit.DAYS))
        );
        var communities = new ManagementDashboardDTO.Communities(
            count("select count(c) from Community c"), count("select count(c) from Community c where c.isDiscoverable=true"),
            count("select count(m) from CommunityMember m where m.status=:value", "value", CommunityMemberStatus.PENDING)
        );
        var events = new ManagementDashboardDTO.Events(
            count("select count(e) from Event e"),
            count("select count(e) from Event e where e.status=:status and e.startsAt>=:now", "status", EventStatus.PUBLISHED, "now", now),
            count("select count(e) from Event e where e.status=:value", "value", EventStatus.PENDING_APPROVAL)
        );
        var moderation = new ManagementDashboardDTO.Moderation(
            count("select count(r) from Report r where r.status=:value", "value", ReportStatus.OPEN),
            count("select count(p) from Post p where p.moderationStatus=:value", "value", ModerationStatus.FLAGGED),
            count("select count(c) from Comment c where c.moderationStatus=:value", "value", ModerationStatus.FLAGGED)
        );
        return new ManagementDashboardDTO(users, communities, events, moderation,
            new ManagementDashboardDTO.Communications(count("select count(n) from AppNotification n")));
    }

    private long count(String jpql, Object... parameters) {
        var query = em.createQuery(jpql, Long.class);
        for (int i=0; i<parameters.length; i+=2) query.setParameter((String) parameters[i], parameters[i+1]);
        return query.getSingleResult();
    }
}
