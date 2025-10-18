package edu.pte.ttk.istallo_kezelo.controller;

import java.util.List;

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
        HorseFarrierApp link = horseFarrierAppService.addHorseToFarrierApp(dto.farrierAppId, dto.horseId);
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
    @GetMapping("/byFarrierApp/{farrierAppId}")
    public List<HorseFarrierAppDTO> getHorsesForFarrierApp(@PathVariable Long farrierAppId) {
        List<HorseFarrierApp> links = horseFarrierAppService.getHorsesForFarrierApp(farrierAppId);
        return links.stream().map(this::toDTO).toList();
    }

    // Összes patkolás lekérdezése ló id alapján
    @GetMapping("/byHorse/{horseId}")
    public List<HorseFarrierAppDTO> getFarrierAppsForHorse(@PathVariable Long horseId) {
        List<HorseFarrierApp> links = horseFarrierAppService.getFarrierAppsForHorse(horseId);
        return links.stream().map(this::toDTO).toList();
    }

    // Link törlése
    @DeleteMapping("/{id}")
    public void removeHorseFromFarrierApp(@PathVariable Long id) {
        HorseFarrierApp link = horseFarrierAppService.getHorseFarrierAppById(id);
        horseFarrierAppService.removeHorseFromFarrierApp(link.getFarrierApp().getId(), link.getHorse().getId());
    }

    private HorseFarrierAppDTO toDTO(HorseFarrierApp link) {
        HorseFarrierAppDTO dto = new HorseFarrierAppDTO();
        dto.horseId = link.getHorse().getId();
        dto.farrierAppId = link.getFarrierApp().getId();
        dto.horseName = link.getHorse().getHorseName();
        dto.appointmentDate = link.getFarrierApp().getAppointmentDate();
        dto.shoes = link.getFarrierApp().getShoes();
        return dto;
    }
}
