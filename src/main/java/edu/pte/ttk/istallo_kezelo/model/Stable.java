package edu.pte.ttk.istallo_kezelo.model;

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

    public Stable(String stableName){
        this.stableName = stableName;
    }

    public List<Horse> getHorsesInStable() {
        return horses;
    }
}
