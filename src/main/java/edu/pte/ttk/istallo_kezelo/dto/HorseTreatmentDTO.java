package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Adatátviteli objektum a(z) HorseTreatment adatcseréhez.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorseTreatmentDTO {
    private Long horseId;
    private Long treatmentId;
    private String horseName;
    private String treatmentName;
    private LocalDate date;
}
