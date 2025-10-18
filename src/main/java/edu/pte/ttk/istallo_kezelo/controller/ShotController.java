package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.ShotDTO;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.service.ShotService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
    @PostMapping()
    public ShotDTO createShot(@RequestBody ShotDTO dto) {
        Shot shot = new Shot();
        shot.setDate(dto.date);
        shot.setShotName(dto.shotName);
        shot.setFrequencyUnit(dto.frequencyUnit);
        shot.setFrequencyValue(dto.frequencyValue);

        Shot savedShot = shotService.saveShot(shot);

        return toDTO(savedShot);
    }

    // Összes oltás lekérdezése
    @GetMapping()
    public List<ShotDTO> getAllShots() {
        Iterable<Shot> shots = shotService.getAllShots();
        return ((List<Shot>) shots).stream()
            .map(this::toDTO).toList();
    }

    // Oltás lekérdezése id alapján
    @GetMapping("/{shotId}")
    public ShotDTO getShotById(@PathVariable Long shotId) {
        return toDTO(shotService.getShotById(shotId));
    }

    // Ló összes oltásának lekérdezése id alapján
    @GetMapping("/horseId/{horseId}")
    public List<ShotDTO> getAllShotsOfHorseById(@PathVariable Long horseId) {
        List<Shot> shots = shotService.getShotsByHorseId(horseId);
        return shots.stream().map(this::toDTO).toList(); 
    }
    
    // Ló összes oltásának lekérdezése ló neve alapján
    @GetMapping("/horseName/{horseName}")
    public List<ShotDTO> getAllShotsOfHorseByName(@PathVariable String horseName) {
        List<Shot> shots = shotService.getShotsByHorseName(horseName);
        return shots.stream().map(this::toDTO).toList();
    }
    
    // Oltás frissítése
    @PatchMapping("/{shotId}")
    public ResponseEntity<Void> updateShot (@PathVariable Long shotId, @RequestBody ShotDTO dto){
        shotService.updateShot(shotId, dto);
        return ResponseEntity.ok().build();
    }

    // Oltás törlése
    @DeleteMapping("/{shotId}")
    public ResponseEntity<Void> deleteShot(@PathVariable Long shotId){
        shotService.deleteShotById(shotId);
        return ResponseEntity.ok().build();
    }


    private ShotDTO toDTO(Shot shot){
        ShotDTO dto = new ShotDTO();
        dto.shotName = shot.getShotName();
        dto.date = shot.getDate();
        dto.frequencyUnit = shot.getFrequencyUnit();
        dto.frequencyValue = shot.getFrequencyValue();
        dto.horseIds = shot.getHorses_treated().stream()
            .map(hs -> hs.getHorse().getId()).toList();
        return dto;
    }
    
    

}
