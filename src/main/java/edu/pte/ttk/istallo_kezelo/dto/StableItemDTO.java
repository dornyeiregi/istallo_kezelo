package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Adatátviteli objektum a(z) StableItem adatcseréhez.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StableItemDTO {
    private Long itemId;
    private Double usageKg;
    private String itemName;
}
