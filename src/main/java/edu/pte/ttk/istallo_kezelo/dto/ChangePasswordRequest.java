package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Adatátviteli objektum a(z) ChangePasswordRequest adatcseréhez.
 */
@Data
@AllArgsConstructor
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;

    /**
     * Üres konstruktor a szerializáláshoz.
     */
    public ChangePasswordRequest() {
    }
}
