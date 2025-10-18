package edu.pte.ttk.istallo_kezelo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.repository.StableRepository;
import edu.pte.ttk.istallo_kezelo.model.Stable;

import java.util.List;
import java.util.Optional;

@Service
public class StableService {

    private final StableRepository stableRepository;

    public StableService(StableRepository stableRepository) {
        this.stableRepository = stableRepository; 
    }

    // Új istálló létrehozása
    @Transactional
    public Stable saveStable(Stable stable) {
        return stableRepository.save(stable);
    }

    // Összes istálló lekérdezése
    public List<Stable> getAllStables() {
        return stableRepository.findAll();
    }

    // Istálló lekérdezése id alapján
    public Optional<Stable> getStableById(Long id) {
        return stableRepository.findById(id);
    }

    // Istálló lekérdezése név alapján
    public Stable getStableByName(String stableName) {
        return stableRepository.findByStableName(stableName);
    }

    // Istálló frissítése
    @Transactional
    public Stable updateStable(Long id, Stable stableDetails) {
        Stable stable = stableRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Istálló nem található."));
        stable.setStableName(stableDetails.getStableName());

        return stableRepository.save(stable);
    }

    // Istálló törlése
    @Transactional
    public void deleteStableById(Long id) {
        stableRepository.deleteById(id);
    }
}
