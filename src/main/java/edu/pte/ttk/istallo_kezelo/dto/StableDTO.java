package edu.pte.ttk.istallo_kezelo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Adatátviteli objektum a(z) Stable adatcseréhez.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StableDTO {
    private Long stableId;
    private String stableName;
    private Double strawUsageKg;
    private List<StableItemDTO> stableItems;
    public List<HorseDTO> horses;
}
