package edu.pte.ttk.istallo_kezelo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "feed_sched")
public class FeedSched {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedSchedid;

    @Enumerated(EnumType.STRING)
    @Column(name = "feed_time", nullable = false)
    private FeedTime feedTime;

    @Column(name = "description", nullable = true)
    private String description;

    // One-to-Many relationship with HorseFeedSched
    @OneToMany(mappedBy = "feedSched", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorseFeedSched> horseFeedScheds = new ArrayList<>();

    @OneToMany(mappedBy = "feedSched", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedSchedItem> feedSchedItems = new ArrayList<>();


    
    // Constructors, getters, and setters

    //Getters
    public Long getFeedSchedid() {
        return feedSchedid;
    }

    public FeedTime getFeedTime() {
        return feedTime;
    }

    public String getDescription() {
        return description;
    }

    public List<HorseFeedSched> getHorseFeedScheds() {
        return horseFeedScheds;
    }

    public List<FeedSchedItem> getFeedSchedItems(){
        return feedSchedItems;
    }



    //Setters

    /*public void setFeedSchedid(Long feedSchedid) {
        this.feedSchedid = feedSchedid;
    }
        */

    public void setFeedTime(FeedTime feedTime) {
        this.feedTime = feedTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHorseFeedScheds(List<HorseFeedSched> horseFeedScheds){
        this.horseFeedScheds = horseFeedScheds;
    }

    public void setFeedSchedItems(List<FeedSchedItem> feedSchedItems) {
        this.feedSchedItems = feedSchedItems;
    }
   
}
