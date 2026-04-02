package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDate;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entitás egy lóhoz és domain objektumhoz kötött naptári eseményhez.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "calendar_event")
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @Column(name = "description")
    private String description;

    /**
     * Létrehoz egy eseményt ló, típus és dátum alapján.
     *
     * @param horse     érintett ló
     * @param eventType esemény típusa
     * @param eventDate esemény dátuma
     */
    public CalendarEvent(Horse horse, EventType eventType, LocalDate eventDate) {
        this.horse = horse;
        this.eventType = eventType;
        this.eventDate = eventDate;
    }

}
