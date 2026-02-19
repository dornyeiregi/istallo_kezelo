package edu.pte.ttk.istallo_kezelo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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

    // Constructors, getters, and setters

    // Getters

    public Horse getHorse() {
        return horse;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    // Setters

    public void setHorse(Horse horse) {
        this.horse = horse;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

}
