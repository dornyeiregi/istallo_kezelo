package edu.pte.ttk.istallo_kezelo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "horse_shot")
public class HorseShot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long horseShotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shot_id", nullable = false)
    private Shot shot;
    
    // Constructors, getters, and setters

    // Getters

    public Horse getHorse() {
        return horse;
    }

    public Shot getShot() {
        return shot;
    }

    // Setters

    public void setHorse(Horse horse) {
        this.horse = horse;
    }

    public void setShot(Shot shot) {
        this.shot = shot;
    }

}
