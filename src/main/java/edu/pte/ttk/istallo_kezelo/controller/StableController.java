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
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'OWNER')")
    public List<StableDTO> getAllStables(){
        return stableService.getAllStables().stream().map(StableMapper::toDTO).toList();
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
