package edu.pte.ttk.istallo_kezelo.service;

import org.springframework.security.access.prepost.PreAuthorize;
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

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Stable saveStable(Stable stable) {
        return stableRepository.save(stable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Stable> getAllStables() {
        return stableRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Optional<Stable> getStableById(Long id) {
        return stableRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
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
