package edu.pte.ttk.istallo_kezelo.dto;

import edu.pte.ttk.istallo_kezelo.model.ItemCategory;
import edu.pte.ttk.istallo_kezelo.model.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private Long id;
    private String name;
    private ItemType itemType;
    private ItemCategory itemCategory;
}
