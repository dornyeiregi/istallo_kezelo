package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageDTO {
    private Long storageId;
    private Double amountInUse;
    private Double amountStored;
    private Long itemId;
    private String itemName;
    private Integer daysRemaining;
    private String warningLevel;
    private java.time.LocalDate lastChecked;
}
