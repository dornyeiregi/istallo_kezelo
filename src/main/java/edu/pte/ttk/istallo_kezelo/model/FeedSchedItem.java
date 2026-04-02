package edu.pte.ttk.istallo_kezelo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entitás etetési ütemterv és tételek összekapcsolásához mennyiséggel.
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "feed_sched_item")
public class FeedSchedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_sched_id", nullable = false)
    private FeedSched feedSched;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "amount", nullable = false)
    private Double amount;

    /**
     * Üres konstruktor a JPA-hoz.
     */
    public FeedSchedItem() {
    }
}
