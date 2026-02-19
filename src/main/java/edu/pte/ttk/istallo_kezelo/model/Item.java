package edu.pte.ttk.istallo_kezelo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import edu.pte.ttk.istallo_kezelo.model.enums.*;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_category", nullable = false)
    private ItemCategory itemCategory;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedSchedItem> feedSchedItems = new ArrayList<>(); 

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Storage> storages = new ArrayList<>();
    
    // Constructors, getters, and setters

    //Getters
    public Long getId() {
        return id;  
    }

    public String getName() {
        return name;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public List<FeedSchedItem> getFeedSchedItems() {
        return feedSchedItems;
    }

    //Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public void setItemCategory(ItemCategory itemCategory) {
        this.itemCategory = itemCategory;
    }


}
