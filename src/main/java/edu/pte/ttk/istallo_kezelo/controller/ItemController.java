package edu.pte.ttk.istallo_kezelo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.dto.ItemDTO;
import edu.pte.ttk.istallo_kezelo.mapper.ItemMapper;
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

    @PostMapping
    public ItemDTO createItem(@RequestBody ItemDTO dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setItemType(dto.getItemType());
        item.setItemCategory(dto.getItemCategory());
        Item saved = itemService.createItem(item);
        return ItemMapper.toDTO(saved);
    }

    @GetMapping()
    public List<ItemDTO> getAllItems() {
        return itemService.getAllItems().stream().map(ItemMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public ItemDTO getItemById(@PathVariable Long id) {
        Item item = itemService.getItemById(id);
        if (item == null) {
            throw new RuntimeException("Tétel nem található.");
        }
        return ItemMapper.toDTO(item);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @RequestBody ItemDTO dto) {
        Item updated = itemService.updateItem(id, dto);
        return ResponseEntity.ok(ItemMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok("Tétel sikeresen törölve.");
    }
}
