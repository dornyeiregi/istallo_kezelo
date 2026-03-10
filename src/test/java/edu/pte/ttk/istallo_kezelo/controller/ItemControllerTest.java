package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.ItemDTO;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemCategory;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.service.ItemService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void createItem_returnsMappedDto() {
        ItemDTO dto = new ItemDTO(null, "Hay", ItemType.FEED, ItemCategory.CONSUMABLE);
        Item saved = ControllerTestSupport.item(1L, "Hay");
        when(itemService.createItem(any(Item.class))).thenReturn(saved);

        ItemDTO result = itemController.createItem(dto);

        assertEquals(1L, result.getItemId());
        assertEquals("Hay", result.getName());
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemService).createItem(captor.capture());
        assertEquals("Hay", captor.getValue().getName());
    }

    @Test
    void getAllItems_returnsMappedDtos() {
        when(itemService.getAllItems()).thenReturn(List.of(ControllerTestSupport.item(1L, "Hay")));

        List<ItemDTO> result = itemController.getAllItems();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getItemId());
    }

    @Test
    void getItemById_throwsWhenMissing() {
        when(itemService.getItemById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> itemController.getItemById(1L));
    }

    @Test
    void updateItem_returnsMappedDto() {
        Item updated = ControllerTestSupport.item(1L, "Hay");
        when(itemService.updateItem(1L, new ItemDTO())).thenReturn(updated);

        var response = itemController.updateItem(1L, new ItemDTO());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getItemId());
    }

    @Test
    void deleteItem_returnsOk() {
        var response = itemController.deleteItem(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tétel sikeresen törölve.", response.getBody());
        verify(itemService).deleteItem(1L);
    }
}
