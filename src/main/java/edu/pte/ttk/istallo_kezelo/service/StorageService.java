package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.dto.StorageDTO;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.StorageRepository;

@Service
public class StorageService {
    private final StorageRepository storageRepository;
    private final ItemRepository itemRepository;

    public StorageService(StorageRepository storageRepository, ItemRepository itemRepository){
        this.storageRepository = storageRepository;
        this.itemRepository = itemRepository;
    }

    // Új tároló hozzáadása
    @Transactional
    public Storage createStorage(StorageDTO dto){
        Item item = itemRepository.findById(dto.itemId)
            .orElseThrow(() -> new RuntimeException("Tétel nem található."));

        Storage storage = new Storage();
        storage.setAmountInUse(dto.amountInUse);
        storage.setAmountStored(dto.amountStored);
        storage.setItem(item);

        return storageRepository.save(storage);
    }

    // Összes tároló lekérdezése
    public List<Storage> getAllStorages(){
        return storageRepository.findAll();
    }

    // Tároló lekérdezése id alapján
    public Storage getStorageById(Long id){
        return storageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tároló nem található."));
    }

    // Tároló lekérdezése tárolt tétel alapján
    public Storage getStorageByItemId(Long itemId){
        return storageRepository.findByItem_ItemId(itemId);
    }

    // Tároló frissítése
    @Transactional
    public Storage updateStorage(Long id, StorageDTO dto){
        Storage existingStorage = storageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tároló nem található."));
        
        if (dto.amountInUse != null) {
            existingStorage.setAmountInUse(dto.amountInUse);
        }
        if (dto.amountStored != null) {
            existingStorage.setAmountStored(dto.amountStored);
        }
        // nem lehet a tételt frissíteni
        
        return storageRepository.save(existingStorage);
    }

    // Tároló törlése
    public void deleteStorage(Long id){
        storageRepository.deleteById(id);
    }

    

}
