package edu.pte.ttk.istallo_kezelo.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import edu.pte.ttk.istallo_kezelo.dto.CalendarEventDTO;
import edu.pte.ttk.istallo_kezelo.service.CalendarEventService;

@RestController
@RequestMapping("/api/calendar-events")
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    public CalendarEventController(CalendarEventService calendarEventService) {
        this.calendarEventService = calendarEventService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public CalendarEventDTO createEvent(@RequestBody CalendarEventDTO dto, Authentication auth) {
        if (dto.getHorseId() == null) {
            throw new RuntimeException("A horseId megadása kötelező.");
        }
        if (dto.getEventDate() == null) {
            throw new RuntimeException("Az eventDate megadása kötelező.");
        }
        if (dto.getEventType() == null) {
            throw new RuntimeException("Az eventType megadása kötelező.");
        }
        return calendarEventService.createEvent(
                dto.getHorseId(),
                dto.getEventType(),
                dto.getEventDate(),
                dto.getRelatedEntityId(),
                dto.getDescription()
        );
    }

    @GetMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public CalendarEventDTO getEventById(@PathVariable Long eventId, Authentication auth) {
        return calendarEventService.getById(eventId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public List<CalendarEventDTO> getAllEvents(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Authentication auth
    ) {
        return calendarEventService.getAllEventsForAuth(start, end, auth);
    }

    @GetMapping("/horse/{horseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public List<CalendarEventDTO> getHorseEvents(
            @PathVariable Long horseId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Authentication auth
    ) {
        if (start != null && end != null) {
            return calendarEventService.getHorseEventsInRange(horseId, start, end);
        }
        return calendarEventService.getHorseEvents(horseId);
    }

    @GetMapping("/stable/{stableId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<CalendarEventDTO> getStableEventsInRange(
            @PathVariable Long stableId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Authentication auth
    ) {
        if (start == null || end == null) {
            throw new RuntimeException("A start és end dátum megadása kötelező.");
        }
        return calendarEventService.getStableEventsInRange(stableId, start, end);
    }

    @PatchMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public CalendarEventDTO updateEventPartially(
            @PathVariable Long eventId,
            @RequestBody CalendarEventDTO dto,
            Authentication auth
    ) {
        return calendarEventService.updateEvent(
                eventId,
                dto.getEventType(),
                dto.getEventDate(),
                dto.getRelatedEntityId(),
                dto.getDescription()
        );
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        calendarEventService.deleteEvent(eventId);
        return ResponseEntity.ok().build();
    }
}
