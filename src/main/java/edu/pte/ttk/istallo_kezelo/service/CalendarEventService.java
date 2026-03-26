package edu.pte.ttk.istallo_kezelo.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
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

    public CalendarEventDTO createEvent(Long horseId,
                                        EventType eventType,
                                        LocalDate eventDate,
                                        Long relatedEntityId,
                                        String description) {
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new EntityNotFoundException("Ló nem található: " + horseId));
        CalendarEvent event = new CalendarEvent(horse, eventType, eventDate);
        event.setRelatedEntityId(relatedEntityId);
        event.setDescription(description);
        CalendarEvent saved = calendarEventRepository.save(event);
        return CalendarEventMapper.toDTO(saved);
    }

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

    public void deleteFromDomain(EventType eventType, Long relatedEntityId) {
        if (eventType == null || relatedEntityId == null) return;
        calendarEventRepository.deleteByEventTypeAndRelatedEntityId(eventType, relatedEntityId);
    }

    public void deleteFromDomain(EventType eventType, Long relatedEntityId, Long horseId) {
        if (eventType == null || relatedEntityId == null || horseId == null) return;
        calendarEventRepository.deleteByEventTypeAndRelatedEntityIdAndHorse_Id(
                eventType,
                relatedEntityId,
                horseId
        );
    }

    @Transactional(readOnly = true)
    public CalendarEventDTO getById(Long eventId) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Esemény nem található: " + eventId));
        return CalendarEventMapper.toDTO(event);
    }

    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getHorseEvents(Long horseId) {
        return calendarEventRepository.findByHorse_IdOrderByEventDateAsc(horseId)
                .stream()
                .map(CalendarEventMapper::toDTO)
                .toList();
    }

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

    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getAllEventsForAuth(LocalDate start, LocalDate end, Authentication auth) {
        if (auth == null || isAdminOrEmployee(auth)) {
            return getAllEvents(start, end);
        }
        String username = auth.getName();
        if (start != null && end != null) {
            return calendarEventRepository
                .findByHorse_Owner_UsernameAndEventDateBetweenOrderByEventDateAsc(username, start, end)
                .stream()
                .map(CalendarEventMapper::toDTO)
                .toList();
        }
        return calendarEventRepository
            .findByHorse_Owner_UsernameOrderByEventDateAsc(username)
            .stream()
            .map(CalendarEventMapper::toDTO)
            .toList();
    }

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

    public CalendarEventDTO updateEvent(Long eventId,
                                        EventType eventType,
                                        LocalDate eventDate,
                                        Long relatedEntityId,
                                        String description) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Esemény nem található: " + eventId));
        if (eventType != null) event.setEventType(eventType);
        if (eventDate != null) event.setEventDate(eventDate);
        if (relatedEntityId != null) {
            event.setRelatedEntityId(relatedEntityId);
        }
        if (description != null) {
            event.setDescription(description);
        }
        return CalendarEventMapper.toDTO(calendarEventRepository.save(event));
    }

    public CalendarEventDTO changeHorse(Long eventId, Long newHorseId) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Esemény nem található: " + eventId));
        Horse horse = horseRepository.findById(newHorseId)
                .orElseThrow(() -> new EntityNotFoundException("Ló nem található: " + newHorseId));
        event.setHorse(horse);
        return CalendarEventMapper.toDTO(calendarEventRepository.save(event));
    }

    public void deleteEvent(Long eventId) {
        if (!calendarEventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Esemény nem található: " + eventId);
        }
        calendarEventRepository.deleteById(eventId);
    }

    private boolean isAdminOrEmployee(Authentication auth) {
        if (auth == null) return false;
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_EMPLOYEE"));
    }
}
