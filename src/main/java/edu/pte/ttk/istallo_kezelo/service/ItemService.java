package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.dto.ItemDTO;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemCategory;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;

/**
 * Application service for item CRUD and validation.
 */
@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Item createItem(Item item) {
        validateItemTypeCategory(item);
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
        if (dto.getFeedUnitAmount() != null) {
            existingItem.setFeedUnitAmount(dto.getFeedUnitAmount());
        }
        validateItemTypeCategory(existingItem);
        return itemRepository.save(existingItem);
    }

    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    private void validateItemTypeCategory(Item item) {
        ItemCategory category = item.getItemCategory();
        ItemType type = item.getItemType();
        Double feedUnitAmount = item.getFeedUnitAmount();

        if (category == null || type == null) {
            throw new RuntimeException("A tétel típusa és kategóriája kötelező.");
        }
        if (feedUnitAmount != null && feedUnitAmount <= 0) {
            throw new RuntimeException("Az etetési adag mennyiségnek pozitívnak kell lennie.");
        }

        if (category == ItemCategory.OBJECT) {
            if (type != ItemType.MACHINE && type != ItemType.ACCESSORY && type != ItemType.BEDDING) {
                throw new RuntimeException("Eszköz kategóriában csak GÉP, KELLÉK vagy ALOM típus engedélyezett.");
            }
            return;
        }

        if (category == ItemCategory.CONSUMABLE) {
            if (type == ItemType.HAY || type == ItemType.FEED || type == ItemType.SUPPLEMENT) {
                return;
            }
            throw new RuntimeException("Takarmány kategóriában csak HAY, FEED vagy SUPPLEMENT engedélyezett.");
        }
    }
}
