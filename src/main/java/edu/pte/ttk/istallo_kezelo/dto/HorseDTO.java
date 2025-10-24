package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;

import edu.pte.ttk.istallo_kezelo.model.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorseDTO {
    private String horseName;
    private LocalDate dob;
    private Sex sex;
    private String ownerName;
    private Long ownerId;
    private String stableName;
    private Long stableId;
    private String microchipNum;
    private String passportNum;
    private String additional;
}