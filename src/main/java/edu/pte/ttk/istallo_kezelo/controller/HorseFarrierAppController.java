package edu.pte.ttk.istallo_kezelo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.pte.ttk.istallo_kezelo.dto.HorseFarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.service.HorseFarrierAppService;

@RestController
@RequestMapping("/api/horseFarrierApps")
public class HorseFarrierAppController {

    private final HorseFarrierAppService horseFarrierAppService;

    public HorseFarrierAppController(HorseFarrierAppService horseFarrierAppService) {
        this.horseFarrierAppService = horseFarrierAppService;
    }

    // Link létrehozása ló és patkolás között
    @PostMapping
    public HorseFarrierAppDTO addHorseToFarrierApp(@RequestBody HorseFarrierAppDTO dto) {
        HorseFarrierApp link = horseFarrierAppService.addHorseToFarrierApp(dto.getFarrierAppId(), dto.getHorseId());
        return toDTO(link);
    }

    // Összes link lekérdezése
    @GetMapping
    public List<HorseFarrierAppDTO> getAllHorseFarrierApps() {
        List<HorseFarrierApp> links = horseFarrierAppService.getAllHorseFarrierApps();
        return links.stream().map(this::toDTO).toList();
    }

    // Link lekérdezése id alapján
    @GetMapping("/{id}")
    public HorseFarrierAppDTO getHorseFarrierAppById(@PathVariable Long id) {
        return toDTO(horseFarrierAppService.getHorseFarrierAppById(id));
    }

    // Összes ló lekérdezése patkolás id alapján
    @GetMapping("/byFarrierAppId/{farrierAppId}")
    public List<HorseFarrierAppDTO> getHorsesForFarrierApp(@PathVariable Long farrierAppId) {
        List<HorseFarrierApp> links = horseFarrierAppService.getHorsesForFarrierApp(farrierAppId);
        return links.stream().map(this::toDTO).toList();
    }

    // Összes patkolás lekérdezése ló id alapján
    @GetMapping("/byHorseId/{horseId}")
    public List<HorseFarrierAppDTO> getFarrierAppsForHorse(@PathVariable Long horseId) {
        List<HorseFarrierApp> links = horseFarrierAppService.getFarrierAppsForHorse(horseId);
        return links.stream().map(this::toDTO).toList();
    }

    // Link törlése
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeHorseFromFarrierApp(@PathVariable Long id) {
        HorseFarrierApp link = horseFarrierAppService.getHorseFarrierAppById(id);
        horseFarrierAppService.removeHorseFromFarrierApp(link.getFarrierApp().getId(), link.getHorse().getId());
        return ResponseEntity.ok("Link sikeresen törölve.");
    }

    private HorseFarrierAppDTO toDTO(HorseFarrierApp link) {
        HorseFarrierAppDTO dto = new HorseFarrierAppDTO();
        dto.setHorseId(link.getHorse().getId());
        dto.setFarrierAppId(link.getFarrierApp().getId());
        dto.setHorseName(link.getHorse().getHorseName());
        dto.setAppointmentDate(link.getFarrierApp().getAppointmentDate());
        dto.setShoes(link.getFarrierApp().getShoes());
        return dto;
    }
}
