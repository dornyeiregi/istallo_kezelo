package edu.pte.ttk.istallo_kezelo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemCategory;
import edu.pte.ttk.istallo_kezelo.repository.StorageRepository;

@Service
public class StorageConsumptionService {

    private final StorageRepository storageRepository;
    private final StorageService storageService;

    public StorageConsumptionService(StorageRepository storageRepository, StorageService storageService) {
        this.storageRepository = storageRepository;
        this.storageService = storageService;
    }

    /**
     * Minden nap 00:05-kor lefut
     * Csökkenti a tárolt mennyiséget minden CONSUMABLE tételnél.
     */
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void reduceConsumablesDaily() {
        LocalDate today = LocalDate.now();

        storageService.syncAllAmountsInUse();

        List<Storage> storages = storageRepository.findByItem_ItemCategory(ItemCategory.CONSUMABLE);

        for (Storage s : storages) {
            if (today.equals(s.getLastReducedDate())) continue;

            Double used = s.getAmountInUse();
            Double stored = s.getAmountStored();

            if (used == null || stored == null) continue;
            if (used <= 0) {
                s.setLastReducedDate(today);
                continue;
            }

            double newStored = stored - used;
            if (newStored < 0) newStored = 0;

            s.setAmountStored(newStored);
            s.setLastReducedDate(today);
        }
    }
}
