package edu.pte.ttk.istallo_kezelo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.dto.CalendarEventDTO;
import edu.pte.ttk.istallo_kezelo.mapper.CalendarEventMapper;
import edu.pte.ttk.istallo_kezelo.model.CalendarEvent;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.repository.CalendarEventRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;
    private final HorseRepository horseRepository;

    public CalendarEventService(CalendarEventRepository calendarEventRepository,
                                HorseRepository horseRepository) {
        this.calendarEventRepository = calendarEventRepository;
        this.horseRepository = horseRepository;
    }

    // Új esemény létrehozása
    public CalendarEventDTO createEvent(Long horseId,
                                        EventType eventType,
                                        LocalDate eventDate,
                                        Long relatedEntityId) {

        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new EntityNotFoundException("Ló nem található: " + horseId));

        CalendarEvent event = new CalendarEvent(horse, eventType, eventDate);
        event.setRelatedEntityId(relatedEntityId);

        CalendarEvent saved = calendarEventRepository.save(event);
        return CalendarEventMapper.toDTO(saved);
    }

    // ============ Események naptárhoz adása (shots / farrier / treatments) ============

    /**
     * Shot/FarrierApp/Treatment mentésekor hívjuk meg.
     * Ha már létezik event (eventType + relatedEntityId alapján) -> update
     * Ha nem létezik -> create
     */
    public CalendarEvent syncFromDomain(Horse horse,
                                        EventType eventType,
                                        LocalDate eventDate,
                                        Long relatedEntityId) {

        if (horse == null) throw new IllegalArgumentException("horse null");
        if (eventType == null) throw new IllegalArgumentException("eventType null");
        if (eventDate == null) throw new IllegalArgumentException("eventDate null");
        if (relatedEntityId == null) throw new IllegalArgumentException("relatedEntityId null");

        CalendarEvent event = calendarEventRepository
                .findByEventTypeAndRelatedEntityIdAndHorse_Id(eventType, relatedEntityId, horse.getId())
                .orElseGet(CalendarEvent::new);

        event.setHorse(horse);
        event.setEventType(eventType);
        event.setEventDate(eventDate);
        event.setRelatedEntityId(relatedEntityId);

        return calendarEventRepository.save(event);
    }

    /**
     * Domain rekord törlésekor hívjuk meg.
     */
    public void deleteFromDomain(EventType eventType, Long relatedEntityId) {
        if (eventType == null || relatedEntityId == null) return;
        calendarEventRepository.deleteByEventTypeAndRelatedEntityId(eventType, relatedEntityId);
    }

    /**
     * Domain rekord és ló kapcsolat törlésekor hívjuk meg.
     */
    public void deleteFromDomain(EventType eventType, Long relatedEntityId, Long horseId) {
        if (eventType == null || relatedEntityId == null || horseId == null) return;
        calendarEventRepository.deleteByEventTypeAndRelatedEntityIdAndHorse_Id(
                eventType,
                relatedEntityId,
                horseId
        );
    }

    // ================================================================================

    // Esemény lekérdezése id alapján
    @Transactional(readOnly = true)
    public CalendarEventDTO getById(Long eventId) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Esemény nem található: " + eventId));
        return CalendarEventMapper.toDTO(event);
    }

    // Esemény lekérdezése ló id alapján
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getHorseEvents(Long horseId) {
        return calendarEventRepository.findByHorse_IdOrderByEventDateAsc(horseId)
                .stream()
                .map(CalendarEventMapper::toDTO)
                .toList();
    }

    // Események lekérdezése (opcionális dátum intervallummal)
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getAllEvents(LocalDate start, LocalDate end) {
        if (start != null && end != null) {
            return calendarEventRepository.findByEventDateBetweenOrderByEventDateAsc(start, end)
                    .stream()
                    .map(CalendarEventMapper::toDTO)
                    .toList();
        }
        return calendarEventRepository.findAllByOrderByEventDateAsc()
                .stream()
                .map(CalendarEventMapper::toDTO)
                .toList();
    }

    // Esemény lekérdezése ló id és időpont alapján
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getHorseEventsInRange(Long horseId,
                                                        LocalDate start,
                                                        LocalDate end) {
        return calendarEventRepository
                .findByHorse_IdAndEventDateBetweenOrderByEventDateAsc(horseId, start, end)
                .stream()
                .map(CalendarEventMapper::toDTO)
                .toList();
    }

    // Esemény lekérdezése istálló id és időpont alapján
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getStableEventsInRange(Long stableId,
                                                         LocalDate start,
                                                         LocalDate end) {
        return calendarEventRepository
                .findByHorse_Stable_IdAndEventDateBetweenOrderByEventDateAsc(stableId, start, end)
                .stream()
                .map(CalendarEventMapper::toDTO)
                .toList();
    }

    // Esemény frissítése (kézi / API)
    public CalendarEventDTO updateEvent(Long eventId,
                                        EventType eventType,
                                        LocalDate eventDate,
                                        Long relatedEntityId) {

        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Esemény nem található: " + eventId));

        if (eventType != null) event.setEventType(eventType);
        if (eventDate != null) event.setEventDate(eventDate);

        // Safeguard: ne nullázd le véletlenül, ha PATCH-ben nem küldöd
        if (relatedEntityId != null) {
            event.setRelatedEntityId(relatedEntityId);
        }

        return CalendarEventMapper.toDTO(calendarEventRepository.save(event));
    }

    // Eseményhez kapcsolt ló lecserélése
    public CalendarEventDTO changeHorse(Long eventId, Long newHorseId) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Esemény nem található: " + eventId));

        Horse horse = horseRepository.findById(newHorseId)
                .orElseThrow(() -> new EntityNotFoundException("Ló nem található: " + newHorseId));

        event.setHorse(horse);
        return CalendarEventMapper.toDTO(calendarEventRepository.save(event));
    }

    // Esemény törlése (kézi / API)
    public void deleteEvent(Long eventId) {
        if (!calendarEventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Esemény nem található: " + eventId);
        }
        calendarEventRepository.deleteById(eventId);
    }
}
