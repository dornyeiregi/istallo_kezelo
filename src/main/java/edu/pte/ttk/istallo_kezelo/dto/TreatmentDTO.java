package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreatmentDTO {
    private Long treatmentId;
    private String treatmentName;
    private String description;
    private LocalDate date;
}
