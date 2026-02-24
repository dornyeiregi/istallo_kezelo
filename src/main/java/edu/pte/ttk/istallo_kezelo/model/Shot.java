package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
@Table(name = "shot")
public class Shot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shot_id", nullable = false)
    private Long id;

    @Column(name = "shot_name", nullable = false)
    private String shotName;

    @Column(name = "frequency_value", nullable = true)
    private Integer frequencyValue;

    @Column(name = "frequency_unit", nullable = true)
    private String frequencyUnit;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "shot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorseShot> horses_treated = new ArrayList<>();

    public LocalDate getNextShotDate(){
        ChronoUnit unit = ChronoUnit.valueOf(frequencyUnit.toUpperCase());
        return date.plus(frequencyValue, unit);
    }
}