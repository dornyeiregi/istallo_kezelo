package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Adatátviteli objektum a(z) FarrierApp adatcseréhez.
 */
@Data
@AllArgsConstructor
public class FarrierAppDTO {
    private Long farrierAppId;
    private String farrierName;
    private String farrierPhone;
    private LocalDate appointmentDate;
    private Integer frequencyValue;
    private String frequencyUnit;
    private Boolean shoes;
    private List<Long> horseIds;
    private List<FarrierHorseDTO> horseDetails;

    /**
     * Üres konstruktor a szerializáláshoz.
     */
    public FarrierAppDTO() {
    }
}
