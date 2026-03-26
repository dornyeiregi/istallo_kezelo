package edu.pte.ttk.istallo_kezelo.model;

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
@Table(name = "horse_farrier_app")
public class HorseFarrierApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farrier_app_id", nullable = false)
    private FarrierApp farrierApp;

    @Column(name = "shoe_count", nullable = false)
    private Integer shoeCount = 0;

    @Column(name = "note")
    private String note;
}
