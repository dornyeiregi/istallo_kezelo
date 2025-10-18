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
        HorseShot link = horseShotService.addShotToHorse(dto.shotId, dto.horseId);
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
    @GetMapping("/byShot/{shotId}")
    public List<HorseShotDTO> getMethodName(@PathVariable Long shotId) {
        List<HorseShot> links = horseShotService.getHorseForShot(shotId);
        return links.stream().map(this::toDtO).toList();
    }
    
    // Lóhoz tartozó összes oltás lekérdezése
    @GetMapping("/byHorse/{horseId}")
    public List<HorseShotDTO> getShotsOfHorse(@PathVariable Long horseId) {
        List<HorseShot> links = horseShotService.getShotsForHorse(horseId);
        return links.stream().map(this::toDtO).toList();
    }

    // Oltás eltávolítása lóból
    @DeleteMapping("/{id}")
    public void removeHorseShot(@PathVariable Long id){
        HorseShot link = horseShotService.getHorseShotById(id);
        horseShotService.removeShotFromHorse(link.getShot().getShotId(), link.getHorse().getId());
    }


    private HorseShotDTO toDtO(HorseShot link){
        HorseShotDTO dto = new HorseShotDTO();
        dto.shotId = link.getShot().getShotId();
        dto.horseId = link.getHorse().getId();
        dto.horseName = link.getHorse().getHorseName();
        dto.shotName = link.getShot().getShotName();
        dto.date = link.getShot().getDate();
        dto.frequencyUnit = link.getShot().getFrequencyUnit();
        dto.frequencyValue = link.getShot().getFrequencyValue();
        return dto;
    }
}
