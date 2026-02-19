package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDate;

import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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

    public CalendarEvent(Horse horse, EventType eventType, LocalDate eventDate) {
        this.horse = horse;
        this.eventType = eventType;
        this.eventDate = eventDate;
    }

    // Getters
    public Long getId() { return id; }
    public Horse getHorse() { return horse; }
    public EventType getEventType() { return eventType; }
    public LocalDate getEventDate() { return eventDate; }
    public Long getRelatedEntityId() { return relatedEntityId; }

    // Setters
    public void setHorse(Horse horse) { this.horse = horse; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }
}
