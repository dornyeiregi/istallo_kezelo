package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.HorseShotDTO;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.service.HorseShotService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
    @PostMapping()
    public HorseShotDTO addShotToHorse(@RequestBody HorseShotDTO dto) {
        HorseShot link = horseShotService.addShotToHorse(dto.getShotId(), dto.getHorseId());
        return toDtO(link);
    }

    // Összes link lekérdezése
    @GetMapping()
    public List<HorseShotDTO> getAllHorseShots() {
        List<HorseShot> links = horseShotService.getAllHorseShots();
        return links.stream().map(this::toDtO).toList();
    }
    
    // Link lekérdezése id alapján
    @GetMapping("/{horseShotId}")
    public HorseShotDTO getHorseShotById(@PathVariable Long horseShotId) {
        return toDtO(horseShotService.getHorseShotById(horseShotId));
    }

    // Oltáshoz tartozó összes ló lekérdezése
    @GetMapping("/byShotId/{shotId}")
    public List<HorseShotDTO> getMethodName(@PathVariable Long shotId) {
        List<HorseShot> links = horseShotService.getHorseForShot(shotId);
        return links.stream().map(this::toDtO).toList();
    }
    
    // Lóhoz tartozó összes oltás lekérdezése
    @GetMapping("/byHorseId/{horseId}")
    public List<HorseShotDTO> getShotsOfHorse(@PathVariable Long horseId) {
        List<HorseShot> links = horseShotService.getShotsForHorse(horseId);
        return links.stream().map(this::toDtO).toList();
    }

    // Oltás eltávolítása lóból
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeHorseShot(@PathVariable Long id){
        HorseShot link = horseShotService.getHorseShotById(id);
        horseShotService.removeShotFromHorse(link.getShot().getShotId(), link.getHorse().getId());
        return ResponseEntity.ok("Link sikeresen törölve.");
    }


    private HorseShotDTO toDtO(HorseShot link){
        HorseShotDTO dto = new HorseShotDTO();
        dto.setShotId(link.getShot().getShotId());
        dto.setHorseId(link.getHorse().getId());
        dto.setHorseName(link.getHorse().getHorseName());
        dto.setShotName(link.getShot().getShotName());
        dto.setDate(link.getShot().getDate());
        dto.setFrequencyUnit(link.getShot().getFrequencyUnit());
        dto.setFrequencyValue(link.getShot().getFrequencyValue());
        return dto;
    }
}
