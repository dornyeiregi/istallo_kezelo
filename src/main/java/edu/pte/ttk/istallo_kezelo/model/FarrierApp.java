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


    //Getters
    public Long getId() {
        return id;
    }

    public String getFarrierName() {
        return farrierName;
    }

    public String getFarrierPhone() {
        return farrierPhone;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public Boolean getShoes() {
        return shoes;
    }

    public List<HorseFarrierApp> getHorses_done() {
        return horsesDone;
    }


    //Setters
    public void setFarrierName(String farrierName) {
        this.farrierName = farrierName; 
    }

    public void setFarrierPhone(String farrierPhone) {
        this.farrierPhone = farrierPhone;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setShoes(Boolean shoes) {
        this.shoes = shoes;
    }

}
