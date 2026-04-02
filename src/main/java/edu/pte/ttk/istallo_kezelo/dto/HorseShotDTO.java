package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Adatátviteli objektum a(z) HorseShot adatcseréhez.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorseShotDTO {
    private Long horseId;
    private Long shotId;
    private String horseName;
    private String shotName;
    private LocalDate date;
    private Integer frequencyValue;
    private String frequencyUnit;
}
