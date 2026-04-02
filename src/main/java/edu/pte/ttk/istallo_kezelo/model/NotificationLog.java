package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entitás értesítések naplózásához, duplikáció elkerülésére.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "notification_log",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_notification_unique",
            columnNames = {"event_type", "entity_id", "horse_id", "days_before"}
        )
    }
)
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Long id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "horse_id")
    private Long horseId;

    @Column(name = "days_before", nullable = false)
    private Integer daysBefore;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
}
