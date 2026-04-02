package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.ItemDTO;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemCategory;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for ItemService behavior.
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void updateItem_updatesProvidedFields() {
        Item item = ServiceTestSupport.item(1L, "Old");
        ItemDTO dto = new ItemDTO(1L, "New", ItemType.SUPPLEMENT, ItemCategory.CONSUMABLE, 5.0);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.updateItem(1L, dto);

        assertEquals("New", result.getName());
        assertSame(ItemType.SUPPLEMENT, result.getItemType());
        assertSame(ItemCategory.CONSUMABLE, result.getItemCategory());
    }
}
