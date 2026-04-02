package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Adatátviteli objektum a(z) FeedSchedChangeRequest adatcseréhez.
 */
@Data
@AllArgsConstructor
public class FeedSchedChangeRequestDTO {
    private Long id;
    private Long feedSchedId;
    private Boolean requestedMorning;
    private Boolean requestedNoon;
    private Boolean requestedEvening;
    private String description;
    private String requestedByName;
    private LocalDateTime requestedAt;
    private List<Long> horseIds;
    private List<Long> itemIds;
    private List<FeedSchedItemAmountDTO> items;

    /**
     * Üres konstruktor a szerializáláshoz.
     */
    public FeedSchedChangeRequestDTO() {
    }
}
