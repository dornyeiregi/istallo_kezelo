package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

/**
 * Adatátviteli objektum a(z) FeedSched adatcseréhez.
 */
@Data
@AllArgsConstructor
public class FeedSchedDTO {
    private long feedSchedId;
    private Boolean feedMorning;
    private Boolean feedNoon;
    private Boolean feedEvening;
    private String description;
    private List<Long> horseIds;
    private List<Long> itemIds;
    private List<FeedSchedItemAmountDTO> items;

    /**
     * Üres konstruktor a szerializáláshoz.
     */
    public FeedSchedDTO() {
    }
}
