package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.ItemDTO;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemCategory;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void createItem_savesValidItem() {
        Item item = new Item();
        item.setName("Straw");
        item.setItemType(ItemType.BEDDING);
        item.setItemCategory(ItemCategory.OBJECT);
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.createItem(item);

        assertSame(item, result);
    }

    @Test
    void createItem_throwsWhenTypeOrCategoryMissing() {
        Item item = new Item();
        item.setName("Incomplete");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> itemService.createItem(item));

        assertEquals("A tétel típusa és kategóriája kötelező.", exception.getMessage());
    }

    @Test
    void createItem_throwsWhenFeedUnitAmountNonPositive() {
        Item item = new Item();
        item.setName("Hay");
        item.setItemType(ItemType.HAY);
        item.setItemCategory(ItemCategory.CONSUMABLE);
        item.setFeedUnitAmount(0.0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> itemService.createItem(item));

        assertEquals("Az etetési adag mennyiségnek pozitívnak kell lennie.", exception.getMessage());
    }

    @Test
    void createItem_throwsWhenObjectCategoryHasConsumableType() {
        Item item = new Item();
        item.setName("Hay");
        item.setItemType(ItemType.HAY);
        item.setItemCategory(ItemCategory.OBJECT);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> itemService.createItem(item));

        assertEquals("Eszköz kategóriában csak GÉP, KELLÉK vagy ALOM típus engedélyezett.", exception.getMessage());
    }

    @Test
    void createItem_throwsWhenConsumableCategoryHasObjectType() {
        Item item = new Item();
        item.setName("Fork");
        item.setItemType(ItemType.ACCESSORY);
        item.setItemCategory(ItemCategory.CONSUMABLE);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> itemService.createItem(item));

        assertEquals("Takarmány kategóriában csak HAY, FEED vagy SUPPLEMENT engedélyezett.", exception.getMessage());
    }

    @Test
    void basicRepositoryMethods_delegateDirectly() {
        Item item = ServiceTestSupport.item(1L, "Hay");
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertEquals(List.of(item), itemService.getAllItems());
        assertSame(item, itemService.getItemById(1L));
        assertNull(itemService.getItemById(2L));

        itemService.deleteItem(1L);

        verify(itemRepository).deleteById(1L);
    }
}
