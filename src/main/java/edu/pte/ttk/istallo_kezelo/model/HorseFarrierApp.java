package edu.pte.ttk.istallo_kezelo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "horse_farrier_app")
public class HorseFarrierApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farrier_app_id", nullable = false)
    private FarrierApp farrierApp;

    // Constructors, getters, and setters

    // Getters

    public Horse getHorse() {
        return horse;
    }

    public FarrierApp getFarrierApp() {
        return farrierApp;
    }

    // Setters

    public void setHorse(Horse horse) {
        this.horse = horse;
    }

    public void setFarrierApp(FarrierApp farrierApp) {
        this.farrierApp = farrierApp;
    }
    

}
