package edu.pte.ttk.istallo_kezelo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.ItemDTO;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.service.ItemService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // Új tétel létrehozása
    @PostMapping
    public ItemDTO createItem(@RequestBody ItemDTO dto) {
        Item item = new Item();
        item.setName(dto.name);
        item.setItemType(dto.itemType);
        item.setItemCategory(dto.itemCategory);

        Item saved = itemService.createItem(item);
        return toDTO(saved);
    }

    // Összes tétel lekérdezése
    @GetMapping()
    public List<ItemDTO> getAllItems() {
        return itemService.getAllItems().stream()
            .map(this::toDTO)
            .toList();
    }

    // Tétel lekérdezése id alapján
    @GetMapping("/{id}")
    public ItemDTO getItemById(@PathVariable Long id) {
        Item item = itemService.getItemById(id);
        if (item == null) {
            throw new RuntimeException("Tétel nem található.");
        }
        return toDTO(item);
    }

    // Tétel frissítése
    @PatchMapping("/{id}")
    public ItemDTO updateItem(@PathVariable Long id, @RequestBody Item item) {
        Item updated = itemService.updateItem(id, item);
        return toDTO(updated);
    }

    // Tétel törlése
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }
        
    private ItemDTO toDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.id = item.getItemId();
        dto.name = item.getName();
        dto.itemType = item.getItemType();
        dto.itemCategory = item.getItemCategory();
        return dto;
    }
}
