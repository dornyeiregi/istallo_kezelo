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

/**
 * Naptári események kezelésére szolgáló alkalmazásszolgáltatás.
 */
@Service
@Transactional
public class CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;
    private final HorseRepository horseRepository;

    /**
     * Létrehozza a szolgáltatást a szükséges repository-kkal.
     *
     * @param calendarEventRepository naptári esemény repository
     * @param horseRepository         ló repository
     */
    public CalendarEventService(CalendarEventRepository calendarEventRepository,
                                HorseRepository horseRepository) {
        this.calendarEventRepository = calendarEventRepository;
        this.horseRepository = horseRepository;
    }

    /**
     * Új naptári eseményt hoz létre.
     *
     * @param horseId        ló azonosító
     * @param eventType      esemény típusa
     * @param eventDate      esemény dátuma
     * @param relatedEntityId kapcsolt entitás azonosító
     * @param description    leírás
     * @return létrehozott esemény DTO
     */
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

    /**
     * Domain objektumból szinkronizálja (létrehozza vagy frissíti) az eseményt.
     *
     * @param horse          ló
     * @param eventType      esemény típusa
     * @param eventDate      esemény dátuma
     * @param relatedEntityId kapcsolt entitás azonosító
     * @return mentett esemény entitás
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
     * Törli a domain objektumhoz tartozó eseményt.
     *
     * @param eventType      esemény típusa
     * @param relatedEntityId kapcsolt entitás azonosító
     */
    public void deleteFromDomain(EventType eventType, Long relatedEntityId) {
        if (eventType == null || relatedEntityId == null) return;
        calendarEventRepository.deleteByEventTypeAndRelatedEntityId(eventType, relatedEntityId);
    }

    /**
     * Törli a domain objektumhoz és lóhoz tartozó eseményt.
     *
     * @param eventType      esemény típusa
     * @param relatedEntityId kapcsolt entitás azonosító
     * @param horseId        ló azonosító
     */
    public void deleteFromDomain(EventType eventType, Long relatedEntityId, Long horseId) {
        if (eventType == null || relatedEntityId == null || horseId == null) return;
        calendarEventRepository.deleteByEventTypeAndRelatedEntityIdAndHorse_Id(
                eventType,
                relatedEntityId,
                horseId
        );
    }

    /**
     * Esemény lekérése azonosító alapján.
     *
     * @param eventId esemény azonosító
     * @return esemény DTO
     */
    @Transactional(readOnly = true)
    public CalendarEventDTO getById(Long eventId) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Esemény nem található: " + eventId));
        return CalendarEventMapper.toDTO(event);
    }

    /**
     * Egy ló összes eseményét adja vissza.
     *
     * @param horseId ló azonosító
     * @return események listája
     */
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getHorseEvents(Long horseId) {
        return calendarEventRepository.findByHorse_IdOrderByEventDateAsc(horseId)
                .stream()
                .map(CalendarEventMapper::toDTO)
                .toList();
    }

    /**
     * Minden eseményt visszaad opcionális dátumszűréssel.
     *
     * @param start kezdő dátum (opcionális)
     * @param end   záró dátum (opcionális)
     * @return események listája
     */
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

    /**
     * Visszaadja az eseményeket a felhasználó jogosultságai alapján.
     *
     * @param start kezdő dátum (opcionális)
     * @param end   záró dátum (opcionális)
     * @param auth  hitelesítési adatok
     * @return események listája
     */
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

    /**
     * Egy ló eseményeinek lekérése adott dátumtartományban.
     *
     * @param horseId ló azonosító
     * @param start   kezdő dátum
     * @param end     záró dátum
     * @return események listája
     */
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

    /**
     * Istálló eseményeinek lekérése adott dátumtartományban.
     *
     * @param stableId istálló azonosító
     * @param start    kezdő dátum
     * @param end      záró dátum
     * @return események listája
     */
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

    /**
     * Esemény adatainak frissítése.
     *
     * @param eventId         esemény azonosító
     * @param eventType       esemény típusa (opcionális)
     * @param eventDate       esemény dátuma (opcionális)
     * @param relatedEntityId kapcsolt entitás azonosító (opcionális)
     * @param description     leírás (opcionális)
     * @return frissített esemény DTO
     */
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

    /**
     * Eseményhez tartozó ló módosítása.
     *
     * @param eventId    esemény azonosító
     * @param newHorseId új ló azonosító
     * @return frissített esemény DTO
     */
    public CalendarEventDTO changeHorse(Long eventId, Long newHorseId) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Esemény nem található: " + eventId));
        Horse horse = horseRepository.findById(newHorseId)
                .orElseThrow(() -> new EntityNotFoundException("Ló nem található: " + newHorseId));
        event.setHorse(horse);
        return CalendarEventMapper.toDTO(calendarEventRepository.save(event));
    }

    /**
     * Esemény törlése.
     *
     * @param eventId esemény azonosító
     */
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
