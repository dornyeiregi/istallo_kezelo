package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.ShotDTO;
import edu.pte.ttk.istallo_kezelo.mapper.ShotMapper;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.service.ShotService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/shots")
public class ShotController {
    private final ShotService shotService;

    public ShotController(ShotService shotService){
        this.shotService = shotService;
    }

    // Új oltás létrehozása
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PostMapping()
    public ShotDTO createShot(@RequestBody ShotDTO dto, Authentication auth) {
        Shot shot = new Shot();
        shot.setDate(dto.getDate());
        shot.setShotName(dto.getShotName());
        shot.setFrequencyUnit(dto.getFrequencyUnit());
        shot.setFrequencyValue(dto.getFrequencyValue());

        Shot savedShot = shotService.saveShot(shot, dto.getHorseIds(), auth);

        return ShotMapper.toDTO(savedShot);
    }


    // Összes oltás lekérdezése
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping()
    public List<ShotDTO> getAllShots(Authentication auth) {
        List<Shot> shots = shotService.getAllShots(auth);
        return (shots).stream().map(ShotMapper::toDTO).toList();
    }

    // Oltás lekérdezése id alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/{shotId}")
    public ShotDTO getShotById(@PathVariable Long shotId, Authentication auth) {
        return ShotMapper.toDTO(shotService.getShotById(shotId, auth));
    }

    // Ló összes oltásának lekérdezése id alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/horseId/{horseId}")
    public List<ShotDTO> getAllShotsOfHorseById(@PathVariable Long horseId, Authentication auth) {
        List<Shot> shots = shotService.getShotsByHorseId(horseId, auth);
        return shots.stream().map(ShotMapper::toDTO).toList(); 
    }
    
    // Ló összes oltásának lekérdezése ló neve alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/horseName/{horseName}")
    public List<ShotDTO> getAllShotsOfHorseByName(@PathVariable String horseName, Authentication auth) {
        List<Shot> shots = shotService.getShotsByHorseName(horseName, auth);
        return shots.stream().map(ShotMapper::toDTO).toList();
    }
    
    // Oltás frissítése
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PatchMapping("/{shotId}")
    public ResponseEntity<String> updateShot (@PathVariable Long shotId, @RequestBody ShotDTO dto, Authentication auth){
        shotService.updateShot(shotId, dto, auth);
        return ResponseEntity.ok("Oltás sikeresen frissítve.");
    }

    // Oltás törlése
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{shotId}")
    public ResponseEntity<String> deleteShot(@PathVariable Long shotId, Authentication auth){
        shotService.deleteShotById(shotId, auth);
        return ResponseEntity.ok("Oltás sikeresen törölve.");
    }


}
