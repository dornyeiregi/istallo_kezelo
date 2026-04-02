package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Adatátviteli objektum a(z) CalendarEvent adatcseréhez.
 */
@Data
@AllArgsConstructor
public class CalendarEventDTO {
    private Long id;
    private Long horseId;
    private String horseName;
    private Long stableId;
    private EventType eventType;
    private LocalDate eventDate;
    private Long relatedEntityId;
    private String description;

    /**
     * Üres konstruktor a szerializáláshoz.
     */
    public CalendarEventDTO() {
    }
}
