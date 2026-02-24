package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.dto.StableDTO;
import edu.pte.ttk.istallo_kezelo.mapper.StableMapper;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.service.StableService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/stables")
public class StableController {

    private final StableService stableService;

    public StableController(StableService stableService){
        this.stableService = stableService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public StableDTO createStable(@RequestBody StableDTO dto){
        Stable stable = new Stable();
        stable.setStableName(dto.getStableName());
        Stable savedStable = stableService.saveStable(stable);
        return StableMapper.toDTO(savedStable);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<StableDTO> getAllStables(){
        return stableService.getAllStables().stream().map(StableMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public StableDTO getStableByID(@PathVariable Long id) {
        Stable stable = stableService.getStableById(id)
            .orElseThrow(() -> new RuntimeException("Istálló nem található."));
        if (stable == null) {
            throw new RuntimeException("Istálló nem található.");
        }
        return StableMapper.toDTO(stable);
    }

    @GetMapping("/stableName")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public StableDTO getStableByName(@RequestParam String stableName) {
        Stable stable = stableService.getStableByName(stableName);
        return StableMapper.toDTO(stable);
    }

    @PatchMapping("/{stableId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public StableDTO updateStablePartially(@PathVariable Long stableId, @RequestBody StableDTO dto) {
        Stable existingStable = stableService.getStableById(stableId)
            .orElseThrow(() -> new RuntimeException("Istálló nem található."));
        if (existingStable == null) {
            throw new RuntimeException("Istálló nem található: " + stableId);
        }

        if (dto.getStableName() != null && !dto.getStableName().isBlank()) {
            existingStable.setStableName(dto.getStableName());
        }

        Stable savedStable = stableService.saveStable(existingStable);
        return StableMapper.toDTO(savedStable);
    }

    @DeleteMapping("/{stableId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deleteStable(@PathVariable Long stableId){
        stableService.deleteStableById(stableId);
        return ResponseEntity.ok("Istálló sikeresen törölve.");
    }
}
