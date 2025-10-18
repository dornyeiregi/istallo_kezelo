package edu.pte.ttk.istallo_kezelo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "storage")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_id", nullable = false)
    private Long storageId;

    @Column(name = "amount_in_use", nullable = false)
    private Double amountInUse;

    @Column(name = "amount_stored", nullable = false)
    private Double amountStored;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // Constructors, getters, and setters

    //Getters
    public Long getStorageId() {
        return storageId;
    }

    public Double getAmountInUse() {
        return amountInUse;
    }

    public Double getAmountStored() {
        return amountStored;
    }

    public Item getItem() {
        return item;
    }

    //Setters
    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    public void setAmountInUse(Double amountInUse) {
        this.amountInUse = amountInUse;
    }

    public void setAmountStored(Double amountStored) {
        this.amountStored = amountStored;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    
}
