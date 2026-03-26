package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAccessSettingsDTO {
    private Boolean viewShots;
    private Boolean viewTreatments;
    private Boolean viewFarrierApps;
}
