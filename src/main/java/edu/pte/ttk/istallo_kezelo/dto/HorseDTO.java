package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;

import edu.pte.ttk.istallo_kezelo.model.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorseDTO {
    private Long id;
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
    private Boolean isActive;
    private Boolean isPending;
    private Long feedSchedId;
}
