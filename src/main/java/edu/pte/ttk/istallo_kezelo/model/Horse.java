package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import edu.pte.ttk.istallo_kezelo.config.EmptyStringToNullConverter;
import edu.pte.ttk.istallo_kezelo.model.enums.Sex;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "horse")
public class Horse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "horse_name", nullable = false)
    private String horseName;

    @Column(name = "dob", nullable = true)
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    private Sex sex;

    @Column(name = "passport_num", nullable = true, unique = true)
    private String passportNum;

    @Convert(converter = EmptyStringToNullConverter.class)
    @Column(name = "microchip_num", nullable = true, unique = true)
    private String microchipNum;

    @Column(name = "additional", nullable = true)
    private String additional;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stable_id", nullable = false)
    private Stable stable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User owner;

    @OneToMany(mappedBy = "horse", orphanRemoval = true)
    private List<HorseFarrierApp> farrierApps = new ArrayList<>();

    @OneToMany(mappedBy = "horse", orphanRemoval = true)
    private List<HorseFeedSched> feedScheds = new ArrayList<>();

    @OneToMany(mappedBy = "horse", orphanRemoval = true)
    private List<HorseShot> shots = new ArrayList<>();

    @OneToMany(mappedBy = "horse", orphanRemoval = true)
    private List<HorseTreatment> treatments = new ArrayList<>();

    // Getters
    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getHorseName() {
        return horseName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public Sex getSex() {
        return sex;
    }

    public String getPassportNum() {
        return passportNum;
    }

    public String getMicrochipNum() {
        return microchipNum;
    }

    public String getAdditional() {
        return additional;
    }

    public Stable getStable(){
        return stable;
    }

    public List<HorseFarrierApp> getFarrierApps() {
        return farrierApps;
    }

    public List<HorseFeedSched> getFeedScheds() {
        return feedScheds;
    }

    public List<HorseShot> getShots() {
        return shots;
    }

    public List<HorseTreatment> getTreatments() { 
        return treatments; 
    }

    // Setters
    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setHorseName(String horseName) {
        this.horseName = horseName;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }

    public void setMicrochipNum(String microchipNum) {
        this.microchipNum = microchipNum;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public void setStable(Stable stable) {
        this.stable = stable;
    }

    // Bi-directional methods
    public void addFarrierApp(HorseFarrierApp app) {
        farrierApps.add(app);
        app.setHorse(this);
    }

    public void removeFarrierApp(HorseFarrierApp app) {
        farrierApps.remove(app);
        app.setHorse(null);
    }

    public void addFeedSched(HorseFeedSched sched) {
        feedScheds.add(sched);
        sched.setHorse(this);
    }

    public void removeFeedSched(HorseFeedSched sched) {
        feedScheds.remove(sched);
        sched.setHorse(null);
    }

    public void addShot(HorseShot shot) {
        shots.add(shot);
        shot.setHorse(this);
    }

    public void removeShot(HorseShot shot) {
        shots.remove(shot);
        shot.setHorse(null);
    }

    public void addTreatment(HorseTreatment treatment) {
        treatments.add(treatment);
        treatment.setHorse(this);
    }

    public void removeTreatment(HorseTreatment treatment) {
        treatments.remove(treatment);
        treatment.setHorse(null);
    }
}