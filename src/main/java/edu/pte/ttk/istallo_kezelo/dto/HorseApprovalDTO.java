package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Adatátviteli objektum a(z) HorseApproval adatcseréhez.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorseApprovalDTO {
    private Long stableId;
    private Long feedSchedId;
}
