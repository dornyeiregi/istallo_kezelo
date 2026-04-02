package edu.pte.ttk.istallo_kezelo.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemCategory;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.repository.StorageRepository;

/**
 * Scheduled service for daily stock consumption and sync.
 */
@Service
public class StorageConsumptionService {

    private final StorageRepository storageRepository;
    private final StorageService storageService;
    private final StorageAlertService storageAlertService;

    public StorageConsumptionService(StorageRepository storageRepository,
                                     StorageService storageService,
                                     StorageAlertService storageAlertService) {
        this.storageRepository = storageRepository;
        this.storageService = storageService;
        this.storageAlertService = storageAlertService;
    }

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void reduceConsumablesDaily() {
        LocalDate today = LocalDate.now();
        storageService.syncAllAmountsInUse();
        List<Storage> storages = new ArrayList<>();
        storages.addAll(storageRepository.findByItem_ItemCategory(ItemCategory.CONSUMABLE));
        storages.addAll(storageRepository.findByItem_ItemType(ItemType.BEDDING));
        List<Storage> unique = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        for (Storage storage : storages) {
            if (storage.getId() == null || seen.add(storage.getId())) {
                unique.add(storage);
            }
        }
        List<Storage> updatedToday = new ArrayList<>();
        for (Storage s : unique) {
            if (today.equals(s.getLastReducedDate())) continue;
            Double used = s.getAmountInUse();
            Double stored = s.getAmountStored();
            if (used == null || stored == null) continue;
            if (used <= 0) {
                s.setLastReducedDate(today);
                updatedToday.add(s);
                continue;
            }
            long daysToReduce;
            if (s.getLastReducedDate() == null) {
                daysToReduce = 1;
            } else {
                daysToReduce = ChronoUnit.DAYS.between(s.getLastReducedDate(), today);
            }
            if (daysToReduce <= 0) {
                s.setLastReducedDate(today);
                updatedToday.add(s);
                continue;
            }
            double newStored = stored - used * daysToReduce;
            if (newStored < 0) newStored = 0;

            s.setAmountStored(newStored);
            s.setLastReducedDate(today);
            updatedToday.add(s);
        }
        storageAlertService.notifyLowStock(updatedToday);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void reduceConsumablesOnStartup() {
        reduceConsumablesDaily();
    }
}
