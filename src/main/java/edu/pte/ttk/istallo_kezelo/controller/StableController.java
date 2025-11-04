package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.HorseDTO;
import edu.pte.ttk.istallo_kezelo.dto.StableDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
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

    // Új istálló létrehozása
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public StableDTO createStable(@RequestBody StableDTO dto){
        Stable stable = new Stable();
        stable.setStableName(dto.getStableName());
        
        Stable savedStable = stableService.saveStable(stable);

        return toDTO(savedStable);
    }
    
    // Összes istálló lekérdezése
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<StableDTO> getAllStables(){
        return stableService.getAllStables().stream().map(this::toDTO).toList();
    }

    // Istálló lekérdezése id alapján
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public StableDTO getStableByID(@PathVariable Long id) {
        Stable stable = stableService.getStableById(id)
            .orElseThrow(() -> new RuntimeException("Istálló nem található."));
        if (stable == null) {
            throw new RuntimeException("Istálló nem található.");
        }
        return toDTO(stable);
    }

    // Istálló lekérdezése név alapján
    @GetMapping("/stableName")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public StableDTO getStableByName(@RequestParam String stableName) {
        Stable stable = stableService.getStableByName(stableName);

        return toDTO(stable);
    }

    // Istálló frissítése
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public StableDTO updateStablePartially(@PathVariable Long id, @RequestBody StableDTO dto){
        Stable existingStable = stableService.getStableById(id)
            .orElseThrow(() -> new RuntimeException("Istálló nem található."));

        if(dto.getStableName() != null) {existingStable.setStableName(dto.getStableName());}
        
        Stable savedStable = stableService.saveStable(existingStable);

        return toDTO(savedStable);
    }


    // Istálló törlése
    @DeleteMapping("/{stableId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deleteStable(@PathVariable Long stableId){
        stableService.deleteStableById(stableId);
        return ResponseEntity.ok("Istálló sikeresen törölve.");
    }


    private StableDTO toDTO(Stable stable){
        StableDTO dto = new StableDTO();
        dto.setStableName(stable.getStableName());
        dto.setHorses(stable.getHorsesInStable().stream().map(this::toHorseDTO).toList());
        return dto;
    }

    private HorseDTO toHorseDTO(Horse horse){
        HorseDTO dto = new HorseDTO();
        dto.setHorseName(horse.getHorseName());
        dto.setDob(horse.getDob());
        dto.setSex(horse.getSex());
        dto.setOwnerName(horse.getOwner().getUserLname() + " "
            + horse.getOwner().getUserFname());
        dto.setOwnerId(horse.getOwner().getId());
        dto.setStableName(horse.getStable().getStableName());
        dto.setStableId(horse.getStable().getStableId());
        dto.setMicrochipNum(horse.getMicrochipNum());
        dto.setPassportNum(horse.getPassportNum());
        dto.setAdditional(horse.getAdditional());
        return dto;
    }

}
