package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.HorseShotDTO;
import edu.pte.ttk.istallo_kezelo.mapper.HorseShotMapper;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.service.HorseShotService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/horseShots")
public class HorseShotController {
    private final HorseShotService horseShotService;

    public HorseShotController(HorseShotService horseShotService){
        this.horseShotService = horseShotService;
    }

    // Oltás hozzáadása lóhoz
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PostMapping()
    public HorseShotDTO addShotToHorse(@RequestBody HorseShotDTO dto, Authentication auth) {
        HorseShot link = horseShotService.addShotToHorse(dto.getShotId(), dto.getHorseId(), auth);
        return HorseShotMapper.toDTO(link);
    }

    // Összes link lekérdezése
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping()
    public List<HorseShotDTO> getAllHorseShots(Authentication auth) {
        List<HorseShot> links = horseShotService.getAllHorseShots(auth);
        return links.stream().map(HorseShotMapper::toDTO).toList();
    }
    
    // Link lekérdezése id alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/{horseShotId}")
    public HorseShotDTO getHorseShotById(@PathVariable Long horseShotId, Authentication auth) {
        return HorseShotMapper.toDTO(horseShotService.getHorseShotById(horseShotId, auth));
    }

    // Oltáshoz tartozó összes ló lekérdezése
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/byShotId/{shotId}")
    public List<HorseShotDTO> getHorseShotsByShotId(@PathVariable Long shotId, Authentication auth) {
        List<HorseShot> links = horseShotService.getHorseForShot(shotId, auth);
        return links.stream().map(HorseShotMapper::toDTO).toList();
    }
    
    // Lóhoz tartozó összes oltás lekérdezése
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/byHorseId/{horseId}")
    public List<HorseShotDTO> getShotsOfHorse(@PathVariable Long horseId, Authentication auth) {
        List<HorseShot> links = horseShotService.getShotsForHorse(horseId, auth);
        return links.stream().map(HorseShotMapper::toDTO).toList();
    }

    // Oltás eltávolítása lóból
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeHorseShot(@PathVariable Long id, Authentication auth){
        HorseShot link = horseShotService.getHorseShotById(id, auth);
        horseShotService.removeShotFromHorse(link.getShot().getId(), link.getHorse().getId(), auth);
        return ResponseEntity.ok("Link sikeresen törölve.");
    }
}
