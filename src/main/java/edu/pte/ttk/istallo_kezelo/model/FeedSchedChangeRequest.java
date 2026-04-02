package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entitás etetési ütemterv módosítási kérelemhez.
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "feed_sched_change_request")
public class FeedSchedChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_sched_id", nullable = false)
    private FeedSched feedSched;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_user_id", nullable = false)
    private User requestedBy;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "requested_morning", nullable = true)
    private Boolean requestedMorning;

    @Column(name = "requested_noon", nullable = true)
    private Boolean requestedNoon;

    @Column(name = "requested_evening", nullable = true)
    private Boolean requestedEvening;

    @Column(name = "requested_description", nullable = true, columnDefinition = "TEXT")
    private String requestedDescription;

    @Column(name = "requested_horse_ids", nullable = true, columnDefinition = "TEXT")
    private String requestedHorseIds;

    @Column(name = "requested_item_ids", nullable = true, columnDefinition = "TEXT")
    private String requestedItemIds;

    @Column(name = "requested_item_amounts", nullable = true, columnDefinition = "TEXT")
    private String requestedItemAmounts;

    /**
     * Üres konstruktor a JPA-hoz.
     */
    public FeedSchedChangeRequest() {
    }
}
