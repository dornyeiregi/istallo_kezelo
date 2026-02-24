package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.ItemDTO;
import edu.pte.ttk.istallo_kezelo.model.Item;

public final class ItemMapper {

    private ItemMapper() {}

    public static ItemDTO toDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setItemId(item.getId());
        dto.setName(item.getName());
        dto.setItemType(item.getItemType());
        dto.setItemCategory(item.getItemCategory());
        return dto;
    }
}
