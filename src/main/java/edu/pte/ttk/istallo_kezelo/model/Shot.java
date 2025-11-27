package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
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

    // Bi-directional relationship with HorseShot
    @OneToMany(mappedBy = "shot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorseShot> horses_treated = new ArrayList<>();


    // Constructors, getters, and setters
    public Long getId() {
        return id;
    }

    public String getShotName() {
        return shotName;
    }

    public LocalDate getDate(){
        return date;
    }

    public Integer getFrequencyValue(){
        return frequencyValue;
    }

    public String getFrequencyUnit(){
        return frequencyUnit;
    }

    public LocalDate getNextShotDate(){
        ChronoUnit unit = ChronoUnit.valueOf(frequencyUnit.toUpperCase());
        return date.plus(frequencyValue, unit);
    }

    public List<HorseShot> getHorses_treated() {
        return horses_treated;
    }

    // Setters

    public void setShotName(String shotName) {
        this.shotName = shotName;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setFrequencyValue(Integer value){
        this.frequencyValue = value;
    }

    public void setFrequencyUnit(String unit){
        this.frequencyUnit = unit;
    }


}