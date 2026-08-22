package com.bialem.backend.web.rest;

import com.bialem.backend.service.CalendarService;
import com.bialem.backend.service.dto.CalendarResponseDTO;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/app/calendar")
public class AppCalendarResource {
  private final CalendarService service; public AppCalendarResource(CalendarService service){this.service=service;}
  @GetMapping public CalendarResponseDTO get(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate startDate,@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate endDate){return service.get(startDate,endDate);}
}
