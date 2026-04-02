package edu.pte.ttk.istallo_kezelo.dto;

import java.util.List;
import edu.pte.ttk.istallo_kezelo.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Adatátviteli objektum a(z) HorseFeedSched adatcseréhez.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorseFeedSchedDTO {
    private Long horseId;
    private Long feedSchedId;
    private String horseName;
    private String feedDescription;
    private List<Item> items;
}
