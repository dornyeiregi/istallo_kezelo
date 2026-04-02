package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity representing a treatment definition and schedule metadata.
 */
@Getter
@Setter
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

    @Column(name = "frequency_value")
    private Integer frequencyValue;

    @Column(name = "frequency_unit")
    private String frequencyUnit;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "treatment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorseTreatment> horsesTreated = new ArrayList<>();
}
