package edu.pte.ttk.istallo_kezelo.model;

import java.util.ArrayList;
import java.util.List;
import edu.pte.ttk.istallo_kezelo.model.enums.*;
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
@Table(name = "feed_sched")
public class FeedSched {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "feed_time", nullable = false)
    private FeedTime feedTime;

    @Column(name = "description", nullable = true)
    private String description;

    @OneToMany(mappedBy = "feedSched", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorseFeedSched> horseFeedScheds = new ArrayList<>();

    @OneToMany(mappedBy = "feedSched", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedSchedItem> feedSchedItems = new ArrayList<>();
}
