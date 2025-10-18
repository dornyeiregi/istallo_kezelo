package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // Új tétel létrehozása
    @Transactional
    public Item createItem(Item item) {
        return itemRepository.save(item);   
    }

    // Összes tétel lekérdezése
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // Tétel lekérdezése id alapján
    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    // Tétel frissítése
    @Transactional
    public Item updateItem(Long id, Item item) {
        Item existingItem = itemRepository.findById(id).orElse(null);
        if (existingItem == null) {
            throw new RuntimeException("Tétel nem található.");
        }
        if(item.getName() != null){
        existingItem.setName(item.getName());}
        if(item.getItemType() != null){
        existingItem.setItemType(item.getItemType());}
        if(item.getItemCategory() != null){
        existingItem.setItemCategory(item.getItemCategory());}
        return itemRepository.save(existingItem);
    }

    // Tétel törlése
    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
