package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "treatment")
public class Treatment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "treatment_id", nullable = false)
    private Long id;

    @Column(name = "treatment_name", nullable = false)
    private String treatmentName;

    @Column(name = "description")
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    // Bi-directional relationship with HorseTreatment
    @OneToMany(mappedBy = "treatment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorseTreatment> horsesTreated = new ArrayList<>();



    // Constructors, getters, and setters
    public Long getId() {
        return id;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<HorseTreatment> getHorses_treated() {
        return horsesTreated;
    }

    // Setters

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
