package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.CalendarEventDTO;
import edu.pte.ttk.istallo_kezelo.model.CalendarEvent;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.CalendarEventRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

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

        CalendarEventDTO result = calendarEventService.createEvent(2L, EventType.SHOT, LocalDate.of(2026, 4, 5), 99L, "desc");

        assertEquals(10L, result.getId());
        assertEquals(2L, result.getHorseId());
        assertEquals(EventType.SHOT, result.getEventType());
        assertEquals(99L, result.getRelatedEntityId());
    }

    @Test
    void createEvent_throwsWhenHorseMissing() {
        when(horseRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> calendarEventService.createEvent(2L, EventType.SHOT, LocalDate.of(2026, 4, 5), 99L, null));

        assertEquals("Ló nem található: 2", exception.getMessage());
    }

    @Test
    void syncFromDomain_updatesExistingEvent() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        CalendarEvent existing = ServiceTestSupport.calendarEvent(10L, horse, EventType.SHOT, LocalDate.of(2026, 4, 1));
        when(calendarEventRepository.findByEventTypeAndRelatedEntityIdAndHorse_Id(EventType.SHOT, 99L, 2L))
            .thenReturn(Optional.of(existing));
        when(calendarEventRepository.save(existing)).thenReturn(existing);

        CalendarEvent result = calendarEventService.syncFromDomain(horse, EventType.SHOT, LocalDate.of(2026, 4, 5), 99L);

        assertSame(existing, result);
        assertEquals(LocalDate.of(2026, 4, 5), existing.getEventDate());
    }

    @Test
    void syncFromDomain_throwsWhenRequiredArgumentNull() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));

        assertEquals("horse null", assertThrows(IllegalArgumentException.class,
            () -> calendarEventService.syncFromDomain(null, EventType.SHOT, LocalDate.of(2026, 4, 5), 99L)).getMessage());
        assertEquals("eventType null", assertThrows(IllegalArgumentException.class,
            () -> calendarEventService.syncFromDomain(horse, null, LocalDate.of(2026, 4, 5), 99L)).getMessage());
        assertEquals("eventDate null", assertThrows(IllegalArgumentException.class,
            () -> calendarEventService.syncFromDomain(horse, EventType.SHOT, null, 99L)).getMessage());
        assertEquals("relatedEntityId null", assertThrows(IllegalArgumentException.class,
            () -> calendarEventService.syncFromDomain(horse, EventType.SHOT, LocalDate.of(2026, 4, 5), null)).getMessage());
    }

    @Test
    void deleteFromDomain_variantsDelegateWhenArgumentsPresent() {
        calendarEventService.deleteFromDomain(EventType.SHOT, 99L);
        calendarEventService.deleteFromDomain(EventType.SHOT, 99L, 2L);
        calendarEventService.deleteFromDomain(null, 99L);
        calendarEventService.deleteFromDomain(EventType.SHOT, null, 2L);

        verify(calendarEventRepository).deleteByEventTypeAndRelatedEntityId(EventType.SHOT, 99L);
        verify(calendarEventRepository).deleteByEventTypeAndRelatedEntityIdAndHorse_Id(EventType.SHOT, 99L, 2L);
    }

    @Test
    void readMethods_mapRepositoryResults() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        CalendarEvent event = ServiceTestSupport.calendarEvent(10L, horse, EventType.SHOT, LocalDate.of(2026, 4, 5));
        when(calendarEventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(calendarEventRepository.findByHorse_IdOrderByEventDateAsc(2L)).thenReturn(List.of(event));
        when(calendarEventRepository.findByEventDateBetweenOrderByEventDateAsc(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30)))
            .thenReturn(List.of(event));
        when(calendarEventRepository.findAllByOrderByEventDateAsc()).thenReturn(List.of(event));
        when(calendarEventRepository.findByHorse_IdAndEventDateBetweenOrderByEventDateAsc(2L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30)))
            .thenReturn(List.of(event));
        when(calendarEventRepository.findByHorse_Stable_IdAndEventDateBetweenOrderByEventDateAsc(3L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30)))
            .thenReturn(List.of(event));

        assertEquals(10L, calendarEventService.getById(10L).getId());
        assertEquals(1, calendarEventService.getHorseEvents(2L).size());
        assertEquals(1, calendarEventService.getAllEvents(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30)).size());
        assertEquals(1, calendarEventService.getAllEvents(null, null).size());
        assertEquals(1, calendarEventService.getHorseEventsInRange(2L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30)).size());
        assertEquals(1, calendarEventService.getStableEventsInRange(3L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30)).size());
    }

    @Test
    void getAllEventsForAuth_usesRoleSpecificQueries() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        CalendarEvent event = ServiceTestSupport.calendarEvent(10L, horse, EventType.SHOT, LocalDate.of(2026, 4, 5));
        Authentication admin = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");

        when(calendarEventRepository.findAllByOrderByEventDateAsc()).thenReturn(List.of(event));
        when(calendarEventRepository.findByHorse_Owner_UsernameAndEventDateBetweenOrderByEventDateAsc(
            "anna",
            LocalDate.of(2026, 4, 1),
            LocalDate.of(2026, 4, 30)
        )).thenReturn(List.of(event));
        when(calendarEventRepository.findByHorse_Owner_UsernameOrderByEventDateAsc("anna")).thenReturn(List.of(event));

        assertEquals(1, calendarEventService.getAllEventsForAuth(null, null, admin).size());
        assertEquals(1, calendarEventService.getAllEventsForAuth(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30), ownerAuth).size());
        assertEquals(1, calendarEventService.getAllEventsForAuth(null, null, ownerAuth).size());
    }

    @Test
    void updateAndChangeHorse_modifyExistingEvent() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        Horse otherHorse = ServiceTestSupport.horse(4L, "Villam", owner, ServiceTestSupport.stable(3L, "Main"));
        CalendarEvent event = ServiceTestSupport.calendarEvent(10L, horse, EventType.SHOT, LocalDate.of(2026, 4, 5));

        when(calendarEventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(calendarEventRepository.save(event)).thenReturn(event);
        when(horseRepository.findById(4L)).thenReturn(Optional.of(otherHorse));

        CalendarEventDTO updated = calendarEventService.updateEvent(
            10L,
            EventType.TREATMENT,
            LocalDate.of(2026, 5, 1),
            88L,
            "updated"
        );
        CalendarEventDTO changed = calendarEventService.changeHorse(10L, 4L);

        assertEquals(EventType.TREATMENT, updated.getEventType());
        assertEquals(88L, updated.getRelatedEntityId());
        assertEquals(4L, changed.getHorseId());
    }

    @Test
    void deleteEvent_throwsWhenEventMissing() {
        when(calendarEventRepository.existsById(10L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> calendarEventService.deleteEvent(10L));

        assertEquals("Esemény nem található: 10", exception.getMessage());
    }

    @Test
    void deleteEvent_deletesWhenPresent() {
        when(calendarEventRepository.existsById(10L)).thenReturn(true);

        calendarEventService.deleteEvent(10L);

        verify(calendarEventRepository).deleteById(10L);
    }
}
