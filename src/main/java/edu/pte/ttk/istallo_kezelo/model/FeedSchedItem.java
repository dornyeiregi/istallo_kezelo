package edu.pte.ttk.istallo_kezelo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "feed_sched_item")
public class FeedSchedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // Created in postgres??

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_sched_id", nullable = false)
    private FeedSched feedSched;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // Constructors, getters, and setters

    // Getters
    public FeedSched getFeedSched() {
        return feedSched;
    }

    public Item getItem() {
        return item;
    }

    // Setters
    public void setFeedSched(FeedSched feedSched) {
        this.feedSched = feedSched;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    
}
