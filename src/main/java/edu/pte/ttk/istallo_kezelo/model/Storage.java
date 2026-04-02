package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entitás készletnyilvántartáshoz tételenként.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "storage")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_id", nullable = false)
    private Long id;

    @Column(name = "amount_in_use", nullable = false)
    private Double amountInUse;

    @Column(name = "amount_stored", nullable = false)
    private Double amountStored;

    @Column(name = "last_reduced_date")
    private LocalDate lastReducedDate;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}
