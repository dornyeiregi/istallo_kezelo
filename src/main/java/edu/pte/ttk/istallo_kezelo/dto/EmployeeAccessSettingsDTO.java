package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Adatátviteli objektum a(z) EmployeeAccessSettings adatcseréhez.
 */
@Data
@AllArgsConstructor
public class EmployeeAccessSettingsDTO {
    private Boolean viewShots;
    private Boolean viewTreatments;
    private Boolean viewFarrierApps;

    /**
     * Üres konstruktor a szerializáláshoz.
     */
    public EmployeeAccessSettingsDTO() {
    }
}
