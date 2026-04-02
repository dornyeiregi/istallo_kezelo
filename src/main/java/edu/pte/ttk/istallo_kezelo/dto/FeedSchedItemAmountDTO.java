package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Adatátviteli objektum a(z) FeedSchedItemAmount adatcseréhez.
 */
@Data
@AllArgsConstructor
public class FeedSchedItemAmountDTO {
    private Long itemId;
    private Double amount;

    /**
     * Üres konstruktor a szerializáláshoz.
     */
    public FeedSchedItemAmountDTO() {
    }
}
