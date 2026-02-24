package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.dto.StorageDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.StorageRepository;

@Service
public class StorageService {
    private final StorageRepository storageRepository;
    private final ItemRepository itemRepository;
    private final FeedSchedItemRepository feedSchedItemRepository;
    private final HorseFeedSchedRepository horseFeedSchedRepository;

    public StorageService(StorageRepository storageRepository,
                          ItemRepository itemRepository,
                          FeedSchedItemRepository feedSchedItemRepository,
                          HorseFeedSchedRepository horseFeedSchedRepository) {
        this.storageRepository = storageRepository;
        this.itemRepository = itemRepository;
        this.feedSchedItemRepository = feedSchedItemRepository;
        this.horseFeedSchedRepository = horseFeedSchedRepository;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Storage createStorage(StorageDTO dto){
        Item item = itemRepository.findById(dto.getItemId())
            .orElseThrow(() -> new RuntimeException("Tétel nem található."));
        Storage storage = new Storage();
        storage.setAmountInUse(0.0);
        storage.setAmountStored(dto.getAmountStored());
        storage.setItem(item);
        return storageRepository.save(storage);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Storage> getAllStorages(){
        return storageRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Storage getStorageById(Long id){
        return storageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tároló nem található."));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Storage getStorageByItemId(Long itemId){
        return storageRepository.findByItem_Id(itemId);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Storage updateStorage(Long id, StorageDTO dto){
        Storage existingStorage = storageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tároló nem található."));
        if (dto.getAmountStored() != null) {
            existingStorage.setAmountStored(dto.getAmountStored());
        }        
        return storageRepository.save(existingStorage);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteStorage(Long id){
        storageRepository.deleteById(id);
    }

    @Transactional
    public void syncAmountInUseForItem(Long itemId) {
        Storage storage = storageRepository.findByItem_Id(itemId);
        if (storage == null) {
            return;
        }
        List<FeedSchedItem> links = feedSchedItemRepository.findByItem_Id(itemId);
        double totalInUse = 0;
        for (FeedSchedItem link : links) {
            Long feedSchedId = link.getFeedSched().getId();
            totalInUse += link.getAmount() * horseFeedSchedRepository.countByFeedSchedId(feedSchedId);
        }
        storage.setAmountInUse(totalInUse);
        storageRepository.save(storage);
    }

    @Transactional
    public void syncAllAmountsInUse() {
        List<Storage> storages = storageRepository.findAll();
        for (Storage storage : storages) {
            Long itemId = storage.getItem().getId();
            List<FeedSchedItem> links = feedSchedItemRepository.findByItem_Id(itemId);
            double totalInUse = 0;
            for (FeedSchedItem link : links) {
                Long feedSchedId = link.getFeedSched().getId();
                totalInUse += link.getAmount() * horseFeedSchedRepository.countByFeedSchedId(feedSchedId);
            }
            storage.setAmountInUse(totalInUse);
        }
        storageRepository.saveAll(storages);
    }
}
