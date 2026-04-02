package edu.pte.ttk.istallo_kezelo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.CalendarEvent;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;

/**
 * Spring Data repository a(z) CalendarEvent entitásokhoz.
 */
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    /**
     * Események lekérése ló szerint dátum szerint növekvő sorrendben.
     *
     * @param horseId ló azonosító
     * @return események listája
     */
    List<CalendarEvent> findByHorse_IdOrderByEventDateAsc(Long horseId);

    /**
     * Események lekérése ló szerint adott dátumtartományban.
     *
     * @param horseId ló azonosító
     * @param start   kezdő dátum
     * @param end     záró dátum
     * @return események listája
     */
    List<CalendarEvent> findByHorse_IdAndEventDateBetweenOrderByEventDateAsc(
            Long horseId,
            LocalDate start,
            LocalDate end
    );

    /**
     * Istálló eseményeinek lekérése dátum szerint növekvő sorrendben.
     *
     * @param stableId istálló azonosító
     * @return események listája
     */
    List<CalendarEvent> findByHorse_Stable_IdOrderByEventDateAsc(Long stableId);

    /**
     * Istálló eseményeinek lekérése adott dátumtartományban.
     *
     * @param stableId istálló azonosító
     * @param start    kezdő dátum
     * @param end      záró dátum
     * @return események listája
     */
    List<CalendarEvent> findByHorse_Stable_IdAndEventDateBetweenOrderByEventDateAsc(
            Long stableId,
            LocalDate start,
            LocalDate end
    );

    /**
     * Minden esemény lekérése dátum szerint növekvő sorrendben.
     *
     * @return események listája
     */
    List<CalendarEvent> findAllByOrderByEventDateAsc();

    /**
     * Események lekérése adott dátumtartományban.
     *
     * @param start kezdő dátum
     * @param end   záró dátum
     * @return események listája
     */
    List<CalendarEvent> findByEventDateBetweenOrderByEventDateAsc(LocalDate start, LocalDate end);

    /**
     * Tulajdonos felhasználónév alapján lekéri az eseményeket.
     *
     * @param username tulajdonos felhasználónév
     * @return események listája
     */
    List<CalendarEvent> findByHorse_Owner_UsernameOrderByEventDateAsc(String username);

    /**
     * Tulajdonos eseményei adott dátumtartományban.
     *
     * @param username tulajdonos felhasználónév
     * @param start    kezdő dátum
     * @param end      záró dátum
     * @return események listája
     */
    List<CalendarEvent> findByHorse_Owner_UsernameAndEventDateBetweenOrderByEventDateAsc(
        String username,
        LocalDate start,
        LocalDate end
    );

    /**
     * Esemény keresése típus és kapcsolt entitás azonosító alapján.
     *
     * @param eventType       esemény típusa
     * @param relatedEntityId kapcsolt entitás azonosító
     * @return opcionális esemény
     */
    Optional<CalendarEvent> findByEventTypeAndRelatedEntityId(EventType eventType, Long relatedEntityId);

    /**
     * Esemény keresése típus, kapcsolt entitás és ló alapján.
     *
     * @param eventType       esemény típusa
     * @param relatedEntityId kapcsolt entitás azonosító
     * @param horseId         ló azonosító
     * @return opcionális esemény
     */
    Optional<CalendarEvent> findByEventTypeAndRelatedEntityIdAndHorse_Id(
            EventType eventType,
            Long relatedEntityId,
            Long horseId
    );

    /**
     * Esemény törlése típus és kapcsolt entitás azonosító alapján.
     *
     * @param eventType       esemény típusa
     * @param relatedEntityId kapcsolt entitás azonosító
     */
    void deleteByEventTypeAndRelatedEntityId(EventType eventType, Long relatedEntityId);

    /**
     * Esemény törlése típus, kapcsolt entitás és ló alapján.
     *
     * @param eventType       esemény típusa
     * @param relatedEntityId kapcsolt entitás azonosító
     * @param horseId         ló azonosító
     */
    void deleteByEventTypeAndRelatedEntityIdAndHorse_Id(EventType eventType, Long relatedEntityId, Long horseId);
}
