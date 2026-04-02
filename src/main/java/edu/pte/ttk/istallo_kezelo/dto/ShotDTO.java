package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Adatátviteli objektum a(z) Shot adatcseréhez.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShotDTO {
    private Long shotId;
    private String shotName;
    private Integer frequencyValue;
    private String frequencyUnit;
    private LocalDate date;
    private List<Long> horseIds;
}
