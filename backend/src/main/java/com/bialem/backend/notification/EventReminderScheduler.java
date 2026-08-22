package com.bialem.backend.notification;

import com.bialem.backend.domain.EventParticipant;
import com.bialem.backend.domain.enumeration.EventParticipantStatus;
import com.bialem.backend.domain.enumeration.EventStatus;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EventReminderScheduler {

    @PersistenceContext
    private EntityManager entityManager;

    private final NotificationEventPublisher publisher;

    public EventReminderScheduler(NotificationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Scheduled(cron = "0 */15 * * * *")
    @Transactional(readOnly = true)
    public void publishUpcomingEventReminders() {
        publishWindow(24, 15);
        publishWindow(1, 15);
    }

    private void publishWindow(int hoursBefore, int toleranceMinutes) {
        Instant center = Instant.now().plus(hoursBefore, ChronoUnit.HOURS);
        Instant from = center.minus(toleranceMinutes, ChronoUnit.MINUTES);
        Instant to = center.plus(toleranceMinutes, ChronoUnit.MINUTES);
        List<EventParticipant> participants = entityManager
            .createQuery(
                "select p from EventParticipant p join fetch p.user u join fetch u.user join fetch p.event e " +
                "where p.status = :participantStatus and e.status = :eventStatus and e.startsAt between :from and :to",
                EventParticipant.class
            )
            .setParameter("participantStatus", EventParticipantStatus.APPROVED)
            .setParameter("eventStatus", EventStatus.PUBLISHED)
            .setParameter("from", from)
            .setParameter("to", to)
            .getResultList();

        for (EventParticipant participant : participants) {
            Map<String, Object> variables = new LinkedHashMap<>();
            variables.put("recipientUserId", participant.getUser().getUser().getId());
            variables.put("referenceType", "EVENT");
            variables.put("referenceId", participant.getEvent().getId());
            variables.put("route", "/events/" + participant.getEvent().getId());
            variables.put("eventTitle", participant.getEvent().getTitle());
            variables.put("startsAt", participant.getEvent().getStartsAt().toString());
            publisher.publish(
                new NotificationEvent(
                    NotificationEventType.EVENT_REMINDER,
                    "event-reminder:" + hoursBefore + ":" + participant.getEvent().getId() + ":" + participant.getUser().getId(),
                    variables
                )
            );
        }
    }
}
