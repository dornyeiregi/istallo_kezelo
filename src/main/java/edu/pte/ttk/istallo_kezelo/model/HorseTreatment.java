package edu.pte.ttk.istallo_kezelo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entitás ló–kezelés kapcsolathoz.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "horse_treatment")
public class HorseTreatment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;
}
