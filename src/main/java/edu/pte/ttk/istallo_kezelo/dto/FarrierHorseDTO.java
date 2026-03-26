package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FarrierHorseDTO {
    private Long horseId;
    private String horseName;
    private Integer shoeCount;
    private String note;
}
