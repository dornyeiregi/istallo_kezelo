package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Storage createStorage(StorageDTO dto){
        Item item = itemRepository.findById(dto.getItemId())
            .orElseThrow(() -> new RuntimeException("Tétel nem található."));

        Storage storage = new Storage();
        storage.setAmountInUse(dto.getAmountInUse());
        storage.setAmountStored(dto.getAmountStored());
        storage.setItem(item);

        return storageRepository.save(storage);
    }

    // Összes tároló lekérdezése
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Storage> getAllStorages(){
        return storageRepository.findAll();
    }

    // Tároló lekérdezése id alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Storage getStorageById(Long id){
        return storageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tároló nem található."));
    }

    // Tároló lekérdezése tárolt tétel alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Storage getStorageByItemId(Long itemId){
        return storageRepository.findByItem_ItemId(itemId);
    }

    // Tároló frissítése
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Storage updateStorage(Long id, StorageDTO dto){
        Storage existingStorage = storageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tároló nem található."));
        
        if (dto.getAmountInUse() != null) {
            existingStorage.setAmountInUse(dto.getAmountInUse());
        }
        if (dto.getAmountStored() != null) {
            existingStorage.setAmountStored(dto.getAmountStored());
        }
        // nem lehet a tételt frissíteni
        
        return storageRepository.save(existingStorage);
    }

    // Tároló törlése
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteStorage(Long id){
        storageRepository.deleteById(id);
    }

    

}
