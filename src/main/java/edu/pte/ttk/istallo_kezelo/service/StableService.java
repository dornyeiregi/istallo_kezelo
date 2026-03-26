package edu.pte.ttk.istallo_kezelo.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.repository.StableRepository;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import java.util.List;
import java.util.Optional;

@Service
public class StableService {

    private final StableRepository stableRepository;
    private final StorageService storageService;
    private final ItemRepository itemRepository;

    public StableService(StableRepository stableRepository, StorageService storageService, ItemRepository itemRepository) {
        this.stableRepository = stableRepository; 
        this.storageService = storageService;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Stable saveStable(Stable stable) {
        Stable saved = stableRepository.save(stable);
        storageService.syncAllAmountsInUse();
        return saved;
    }

    public Item requireBeddingItem(Long itemId) {
        if (itemId == null) return null;
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Tétel nem található."));
        if (item.getItemType() != ItemType.BEDDING) {
            throw new RuntimeException("Az alomhoz csak ALOM típusú tétel választható.");
        }
        return item;
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'OWNER')")
    public List<Stable> getAllStables() {
        return stableRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'OWNER')")
    public Optional<Stable> getStableById(Long id) {
        return stableRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'OWNER')")
    public Stable getStableByName(String stableName) {
        return stableRepository.findByStableName(stableName);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Stable updateStable(Long id, Stable stableDetails) {
        Stable stable = stableRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Istálló nem található."));
        stable.setStableName(stableDetails.getStableName());
        return stableRepository.save(stable);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteStableById(Long id) {
        stableRepository.deleteById(id);
    }
}
