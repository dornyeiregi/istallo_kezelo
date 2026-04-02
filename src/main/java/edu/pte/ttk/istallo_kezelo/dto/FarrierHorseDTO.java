package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Adatátviteli objektum a(z) FarrierHorse adatcseréhez.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FarrierHorseDTO {
    private Long horseId;
    private String horseName;
    private Integer shoeCount;
    private String note;
}
