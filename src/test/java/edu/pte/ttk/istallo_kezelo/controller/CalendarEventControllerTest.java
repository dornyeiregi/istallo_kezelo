package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.CalendarEventDTO;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.service.CalendarEventService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CalendarEventControllerTest {

    @Mock
    private CalendarEventService calendarEventService;

    @InjectMocks
    private CalendarEventController calendarEventController;

    @Test
    void createEvent_returnsCreatedEvent() {
        CalendarEventDTO dto = new CalendarEventDTO(null, 7L, null, null, EventType.SHOT, LocalDate.of(2026, 3, 10), 9L, null);
        CalendarEventDTO created = new CalendarEventDTO(1L, 7L, "Csillag", 3L, EventType.SHOT, dto.getEventDate(), 9L, null);
        when(calendarEventService.createEvent(7L, EventType.SHOT, dto.getEventDate(), 9L, null)).thenReturn(created);

        CalendarEventDTO result = calendarEventController.createEvent(dto, ControllerTestSupport.auth("anna", "ROLE_OWNER"));

        assertEquals(created, result);
    }

    @Test
    void createEvent_throwsWhenHorseIdMissing() {
        CalendarEventDTO dto = new CalendarEventDTO();
        dto.setEventType(EventType.SHOT);
        dto.setEventDate(LocalDate.of(2026, 3, 10));

        assertThrows(RuntimeException.class, () ->
            calendarEventController.createEvent(dto, ControllerTestSupport.auth("anna", "ROLE_OWNER"))
        );
    }

    @Test
    void getEventById_returnsEvent() {
        CalendarEventDTO expected = new CalendarEventDTO(1L, 7L, "Csillag", 3L, EventType.SHOT, LocalDate.of(2026, 3, 10), 9L, null);
        when(calendarEventService.getById(1L)).thenReturn(expected);

        CalendarEventDTO result = calendarEventController.getEventById(1L, ControllerTestSupport.auth("anna", "ROLE_OWNER"));

        assertEquals(expected, result);
    }

    @Test
    void getAllEvents_returnsEventsFromService() {
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 3, 31);
        var auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        List<CalendarEventDTO> expected = List.of(new CalendarEventDTO(1L, 7L, "Csillag", 3L, EventType.SHOT, start, 9L, null));
        when(calendarEventService.getAllEventsForAuth(start, end, auth)).thenReturn(expected);

        List<CalendarEventDTO> result = calendarEventController.getAllEvents(start, end, auth);

        assertEquals(expected, result);
    }

    @Test
    void getHorseEvents_usesRangeServiceWhenDatesProvided() {
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 3, 31);
        List<CalendarEventDTO> expected = List.of(new CalendarEventDTO(1L, 7L, "Csillag", 3L, EventType.SHOT, start, 9L, null));
        when(calendarEventService.getHorseEventsInRange(7L, start, end)).thenReturn(expected);

        List<CalendarEventDTO> result = calendarEventController.getHorseEvents(7L, start, end, ControllerTestSupport.auth("anna", "ROLE_OWNER"));

        assertEquals(expected, result);
    }

    @Test
    void getStableEventsInRange_throwsWhenDateMissing() {
        assertThrows(RuntimeException.class, () ->
            calendarEventController.getStableEventsInRange(3L, null, LocalDate.of(2026, 3, 31), ControllerTestSupport.auth("admin", "ROLE_ADMIN"))
        );
    }

    @Test
    void updateEventPartially_returnsUpdatedEvent() {
        CalendarEventDTO dto = new CalendarEventDTO(null, null, null, null, EventType.TREATMENT, LocalDate.of(2026, 4, 1), 5L, null);
        CalendarEventDTO updated = new CalendarEventDTO(1L, 7L, "Csillag", 3L, EventType.TREATMENT, dto.getEventDate(), 5L, null);
        when(calendarEventService.updateEvent(1L, EventType.TREATMENT, dto.getEventDate(), 5L, null)).thenReturn(updated);

        CalendarEventDTO result = calendarEventController.updateEventPartially(1L, dto, ControllerTestSupport.auth("admin", "ROLE_ADMIN"));

        assertEquals(updated, result);
    }

    @Test
    void deleteEvent_returnsOk() {
        var response = calendarEventController.deleteEvent(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(calendarEventService).deleteEvent(1L);
    }
}
