package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import java.util.Objects;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.dto.StorageDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.StableRepository;
import edu.pte.ttk.istallo_kezelo.repository.StorageRepository;

@Service
public class StorageService {
    private final StorageRepository storageRepository;
    private final ItemRepository itemRepository;
    private final FeedSchedItemRepository feedSchedItemRepository;
    private final HorseFeedSchedRepository horseFeedSchedRepository;
    private final StableRepository stableRepository;

    public StorageService(StorageRepository storageRepository,
                          ItemRepository itemRepository,
                          FeedSchedItemRepository feedSchedItemRepository,
                          HorseFeedSchedRepository horseFeedSchedRepository,
                          StableRepository stableRepository) {
        this.storageRepository = storageRepository;
        this.itemRepository = itemRepository;
        this.feedSchedItemRepository = feedSchedItemRepository;
        this.horseFeedSchedRepository = horseFeedSchedRepository;
        this.stableRepository = stableRepository;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Storage createStorage(StorageDTO dto){
        Item item = itemRepository.findById(dto.getItemId())
            .orElseThrow(() -> new RuntimeException("Tétel nem található."));
        if (dto.getAmountStored() != null && dto.getAmountStored() < 0) {
            throw new RuntimeException("A tárolt mennyiség nem lehet negatív.");
        }
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
            if (dto.getAmountStored() < 0) {
                throw new RuntimeException("A tárolt mennyiség nem lehet negatív.");
            }
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
        double totalInUse = calculateAmountInUse(storage.getItem());
        storage.setAmountInUse(totalInUse);
        storageRepository.save(storage);
    }

    @Transactional
    public void syncAllAmountsInUse() {
        List<Storage> storages = storageRepository.findAll();
        for (Storage storage : storages) {
            Item item = storage.getItem();
            if (item.getItemType() == ItemType.BEDDING) {
                storage.setAmountInUse(beddingUsageForItem(item.getId()));
                continue;
            }
            storage.setAmountInUse(calculateFeedAmountInUse(item.getId()));
        }
        storageRepository.saveAll(storages);
    }

    private double calculateAmountInUse(Item item) {
        if (item.getItemType() == ItemType.BEDDING) {
            return beddingUsageForItem(item.getId());
        }
        return calculateFeedAmountInUse(item.getId());
    }

    private double calculateFeedAmountInUse(Long itemId) {
        List<FeedSchedItem> links = feedSchedItemRepository.findByItem_Id(itemId);
        double totalInUse = 0;
        for (FeedSchedItem link : links) {
            Long feedSchedId = link.getFeedSched().getId();
            totalInUse += link.getAmount() * horseFeedSchedRepository.countActiveByFeedSchedId(feedSchedId);
        }
        return totalInUse;
    }

    private double beddingUsageForItem(Long itemId) {
        return stableRepository.findAll().stream()
            .flatMap(stable -> stable.getStableItems().stream())
            .filter(link -> link.getItem().getId().equals(itemId))
            .map(link -> link.getUsageKg())
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .sum();
    }
}
