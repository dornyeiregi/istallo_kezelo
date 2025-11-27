package edu.pte.ttk.istallo_kezelo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;


@Entity
@Table(name = "stable")
public class Stable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stable_id", nullable = false)
    private Long id;

    @Column(name = "stable_name", nullable = false, unique = true)
    private String stableName;

    @OneToMany(mappedBy = "stable", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Horse> horses = new ArrayList<>();


    // Constructors
    public Stable(){

    }

    public Stable(String stableName){
        this.stableName = stableName;
    }


    // Getters and Setters

    // Getters
    public Long getId() {
        return id;
    }

    public String getStableName() {
        return stableName;
    }

    public List<Horse> getHorsesInStable() {
        return horses;
    }
    

    // Setters
    public void setStableName(String stableName) {
        this.stableName = stableName;
    }

}
