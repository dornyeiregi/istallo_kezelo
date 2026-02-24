package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.dto.ItemDTO;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Item createItem(Item item) {
        return itemRepository.save(item);   
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    @Transactional
    public Item updateItem(Long id, ItemDTO dto) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tétel nem található."));
        if (dto.getName() != null) {
            existingItem.setName(dto.getName());
        }
        if (dto.getItemType() != null) {
            existingItem.setItemType(dto.getItemType());
        }
        if (dto.getItemCategory() != null) {
            existingItem.setItemCategory(dto.getItemCategory());
        }
        return itemRepository.save(existingItem);
    }

    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
