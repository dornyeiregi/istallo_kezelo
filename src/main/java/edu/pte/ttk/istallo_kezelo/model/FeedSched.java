package edu.pte.ttk.istallo_kezelo.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entitás etetési ütemterv sablonhoz és időpont jelölésekhez.
 */
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

    @Column(name = "feed_morning", nullable = false)
    private boolean feedMorning;

    @Column(name = "feed_noon", nullable = false)
    private boolean feedNoon;

    @Column(name = "feed_evening", nullable = false)
    private boolean feedEvening;

    @Column(name = "description", nullable = true)
    private String description;

    @OneToMany(mappedBy = "feedSched", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorseFeedSched> horseFeedScheds = new ArrayList<>();

    @OneToMany(mappedBy = "feedSched", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedSchedItem> feedSchedItems = new ArrayList<>();
}
