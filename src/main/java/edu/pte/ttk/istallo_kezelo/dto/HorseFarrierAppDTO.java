package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorseFarrierAppDTO {
    private Long horseId;
    private Long farrierAppId;
    private String horseName;
    private String farrierName;
    private LocalDate appointmentDate;
    private Boolean shoes;
}
