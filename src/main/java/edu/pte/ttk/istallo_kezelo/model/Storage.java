package edu.pte.ttk.istallo_kezelo.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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


    //Getters
    public Long getId() {
        return id;
    }

    public Double getAmountInUse() {
        return amountInUse;
    }

    public Double getAmountStored() {
        return amountStored;
    }

    public LocalDate getLastReducedDate() {
        return lastReducedDate;
    }

    public Item getItem() {
        return item;
    }

    //Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setAmountInUse(Double amountInUse) {
        this.amountInUse = amountInUse;
    }

    public void setAmountStored(Double amountStored) {
        this.amountStored = amountStored;
    }

    public void setLastReducedDate(LocalDate lastReducedDate) {
        this.lastReducedDate = lastReducedDate;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    
}
