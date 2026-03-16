package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
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

    @Column(name = "requested_horse_ids", nullable = true, columnDefinition = "TEXT")
    private String requestedHorseIds;

    @Column(name = "requested_item_ids", nullable = true, columnDefinition = "TEXT")
    private String requestedItemIds;
}
