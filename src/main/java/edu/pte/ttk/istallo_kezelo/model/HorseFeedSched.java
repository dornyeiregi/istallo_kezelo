package edu.pte.ttk.istallo_kezelo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "horse_feed_sched")
public class HorseFeedSched {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_sched_id", nullable = false)
    private FeedSched feedSched;

    // Constructors, getters, and setters

    // Getters

    public Horse getHorse() {
        return horse;
    }

    public FeedSched getFeedSched() {
        return feedSched;
    }

    // Setters

    public void setHorse(Horse horse) {
        this.horse = horse;
    }

    public void setFeedSched(FeedSched feedSched) {
        this.feedSched = feedSched;
    }

}
