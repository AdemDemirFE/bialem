package com.bialem.backend.service;

import com.bialem.backend.domain.*;
import com.bialem.backend.domain.enumeration.*;
import com.bialem.backend.repository.*;
import com.bialem.backend.security.SecurityUtils;
import com.bialem.backend.service.dto.CalendarResponseDTO;
import java.time.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service @Transactional(readOnly = true)
public class CalendarService {
  private final EventRepository events; private final CityEventRepository cityEvents; private final ProfileRepository profiles; private final EventParticipantRepository participants;
  public CalendarService(EventRepository e, CityEventRepository c, ProfileRepository p, EventParticipantRepository ep){events=e;cityEvents=c;profiles=p;participants=ep;}
  public CalendarResponseDTO get(LocalDate start, LocalDate end){
    if(start==null||end==null||end.isBefore(start)||start.plusMonths(3).isBefore(end)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Geçerli ve en fazla 3 aylık tarih aralığı girin");
    String login=SecurityUtils.getCurrentUserLogin().orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    Profile me=profiles.findOneByUser_Login(login).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Profil bulunamadı"));
    ZoneId zone=ZoneId.of("Europe/Istanbul"); Instant from=start.atStartOfDay(zone).toInstant(), to=end.plusDays(1).atStartOfDay(zone).toInstant();
    List<Event> be=events.findCalendarEvents(from,to,EventStatus.PUBLISHED); List<CityEvent> ce=me.getCity()==null||me.getCity().isBlank()?List.of():cityEvents.findCalendarEvents(from,to,me.getCity().trim(),CityEventStatus.PUBLISHED);
    List<Profile> birthdays=profiles.findBirthdaysInRange(start,end); Map<Long,Long> counts=new HashMap<>(); if(!be.isEmpty()) for(Object[] row:participants.countCalendarParticipants(be.stream().map(Event::getId).toList())) counts.put((Long)row[0],(Long)row[1]);
    List<CalendarResponseDTO.Item> items=new ArrayList<>();
    for(Event e:be) items.add(new CalendarResponseDTO.Item("BIALEM_EVENT",String.valueOf(e.getId()),e.getTitle(),e.getDescription(),e.getStartsAt().atZone(zone).toLocalDate(),e.getStartsAt(),e.getEndsAt(),e.getCoverImageUrl(),e.getLocationName(),e.getCommunity()==null?null:e.getCommunity().getName(),null,"/event/"+e.getId(),counts.getOrDefault(e.getId(),0L),null,null));
    for(CityEvent e:ce) items.add(new CalendarResponseDTO.Item("CITY_EVENT",String.valueOf(e.getId()),e.getTitle(),e.getDescription(),e.getStartsAt().atZone(zone).toLocalDate(),e.getStartsAt(),e.getEndsAt(),e.getCoverImageUrl(),e.getVenueName(),e.getCategory(),e.getPriceLabel(),"/city-event/"+e.getId(),null,null,null));
    int year=start.getYear(); for(Profile p:birthdays){LocalDate birthday=p.getBirthDate();LocalDate occurrence=safeBirthday(year,birthday);if(occurrence.isBefore(start))occurrence=safeBirthday(year+1,birthday);int age=Period.between(birthday,occurrence).getYears();items.add(new CalendarResponseDTO.Item("BIRTHDAY",String.valueOf(p.getId()),p.getDisplayName()+" doğum günü",null,occurrence,null,null,p.getAvatarUrl(),null,null,null,"/user/"+p.getId(),null,p.getUsername(),age));}
    items.sort(Comparator.comparing(CalendarResponseDTO.Item::date).thenComparing(i->i.startsAt()==null?Instant.MAX:i.startsAt())); Instant now=Instant.now(); List<CalendarResponseDTO.Item> upcoming=items.stream().filter(i->i.startsAt()!=null&&!i.startsAt().isBefore(now)).limit(10).toList();
    return new CalendarResponseDTO(new CalendarResponseDTO.Range(start,end),new CalendarResponseDTO.Counts(be.size(),ce.size(),birthdays.size(),items.size()),items,upcoming);
  }
  private LocalDate safeBirthday(int year,LocalDate birth){int day=Math.min(birth.getDayOfMonth(),Month.of(birth.getMonthValue()).length(Year.isLeap(year)));return LocalDate.of(year,birth.getMonthValue(),day);}
}
