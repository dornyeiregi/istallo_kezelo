package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "farrier_app")
public class FarrierApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "farrier_app_id", nullable = false)
    private Long id;

    @Column(name = "farrier_name", nullable = true)
    private String farrierName;

    @Column(name = "farrier_phone", nullable = true)
    private String farrierPhone;

    @Column(name = "date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "shoes", nullable = false)
    private Boolean shoes;

    @OneToMany(mappedBy = "farrierApp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorseFarrierApp> horsesDone = new ArrayList<>();

    public List<HorseFarrierApp> getHorses_done() {
        return horsesDone;
    }
}
