package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Adatátviteli objektum a(z) FeedSchedItem adatcseréhez.
 */
@Data
@AllArgsConstructor
public class FeedSchedItemDTO {
    private Long feedSchedId;
    private Long itemId;
    private String itemName;
    private String feedDescription;
    private Double amount;

    /**
     * Üres konstruktor a szerializáláshoz.
     */
    public FeedSchedItemDTO() {
    }
}
