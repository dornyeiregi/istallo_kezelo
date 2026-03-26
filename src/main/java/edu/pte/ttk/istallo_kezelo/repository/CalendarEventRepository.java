package edu.pte.ttk.istallo_kezelo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.pte.ttk.istallo_kezelo.model.CalendarEvent;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    List<CalendarEvent> findByHorse_IdOrderByEventDateAsc(Long horseId);

    List<CalendarEvent> findByHorse_IdAndEventDateBetweenOrderByEventDateAsc(
            Long horseId,
            LocalDate start,
            LocalDate end
    );

    List<CalendarEvent> findByHorse_Stable_IdOrderByEventDateAsc(Long stableId);

    List<CalendarEvent> findByHorse_Stable_IdAndEventDateBetweenOrderByEventDateAsc(
            Long stableId,
            LocalDate start,
            LocalDate end
    );

    List<CalendarEvent> findAllByOrderByEventDateAsc();

    List<CalendarEvent> findByEventDateBetweenOrderByEventDateAsc(LocalDate start, LocalDate end);

    List<CalendarEvent> findByHorse_Owner_UsernameOrderByEventDateAsc(String username);

    List<CalendarEvent> findByHorse_Owner_UsernameAndEventDateBetweenOrderByEventDateAsc(
        String username,
        LocalDate start,
        LocalDate end
    );

    Optional<CalendarEvent> findByEventTypeAndRelatedEntityId(EventType eventType, Long relatedEntityId);

    Optional<CalendarEvent> findByEventTypeAndRelatedEntityIdAndHorse_Id(
            EventType eventType,
            Long relatedEntityId,
            Long horseId
    );

    void deleteByEventTypeAndRelatedEntityId(EventType eventType, Long relatedEntityId);

    void deleteByEventTypeAndRelatedEntityIdAndHorse_Id(EventType eventType, Long relatedEntityId, Long horseId);
}
