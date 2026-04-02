package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.CalendarEventDTO;
import edu.pte.ttk.istallo_kezelo.model.CalendarEvent;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.CalendarEventRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for CalendarEventService behavior.
 */
@ExtendWith(MockitoExtension.class)
class CalendarEventServiceTest {

    @Mock
    private CalendarEventRepository calendarEventRepository;

    @Mock
    private HorseRepository horseRepository;

    @InjectMocks
    private CalendarEventService calendarEventService;

    @Test
    void createEvent_savesAndMapsCalendarEvent() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        CalendarEvent savedEvent = ServiceTestSupport.calendarEvent(10L, horse, EventType.SHOT, LocalDate.of(2026, 4, 5));
        savedEvent.setRelatedEntityId(99L);

        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(calendarEventRepository.save(any(CalendarEvent.class))).thenReturn(savedEvent);

        CalendarEventDTO result = calendarEventService.createEvent(2L, EventType.SHOT, LocalDate.of(2026, 4, 5), 99L, null);

        assertEquals(10L, result.getId());
        assertEquals(2L, result.getHorseId());
        assertEquals(EventType.SHOT, result.getEventType());
        assertEquals(99L, result.getRelatedEntityId());
    }
}
