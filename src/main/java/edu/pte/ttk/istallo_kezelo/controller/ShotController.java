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

/**
 * REST controller for shot CRUD and related queries.
 */
@RestController
@RequestMapping("/api/shots")
public class ShotController {
    private final ShotService shotService;

    public ShotController(ShotService shotService){
        this.shotService = shotService;
    }

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


    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    @GetMapping()
    public List<ShotDTO> getAllShots(Authentication auth) {
        List<Shot> shots = shotService.getAllShots(auth);
        return (shots).stream().map(ShotMapper::toDTO).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    @GetMapping("/{shotId}")
    public ShotDTO getShotById(@PathVariable Long shotId, Authentication auth) {
        return ShotMapper.toDTO(shotService.getShotById(shotId, auth));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    @GetMapping("/horseId/{horseId}")
    public List<ShotDTO> getAllShotsOfHorseById(@PathVariable Long horseId, Authentication auth) {
        List<Shot> shots = shotService.getShotsByHorseId(horseId, auth);
        return shots.stream().map(ShotMapper::toDTO).toList(); 
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PatchMapping("/{shotId}")
    public ResponseEntity<String> updateShot (@PathVariable Long shotId, @RequestBody ShotDTO dto, Authentication auth){
        shotService.updateShot(shotId, dto, auth);
        return ResponseEntity.ok("Oltás sikeresen frissítve.");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{shotId}")
    public ResponseEntity<String> deleteShot(@PathVariable Long shotId, Authentication auth){
        shotService.deleteShotById(shotId, auth);
        return ResponseEntity.ok("Oltás sikeresen törölve.");
    }


}
