package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Adatátviteli objektum a(z) HorseFarrierApp adatcseréhez.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorseFarrierAppDTO {
    private Long horseId;
    private Long farrierAppId;
    private String horseName;
    private String farrierName;
    private LocalDate appointmentDate;
    private Integer shoeCount;
    private String note;
}
