package edu.pte.ttk.istallo_kezelo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
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

    // Constructors, getters, and setters

    // Getters
    public FeedSched getFeedSched() {
        return feedSched;
    }

    public Item getItem() {
        return item;
    }

    public Double getAmount() {
        return amount;
    }

    // Setters
    public void setFeedSched(FeedSched feedSched) {
        this.feedSched = feedSched;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
}
