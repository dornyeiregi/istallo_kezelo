package edu.pte.ttk.istallo_kezelo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PostMapping
    public HorseFarrierAppDTO addHorseToFarrierApp(@RequestBody HorseFarrierAppDTO dto, Authentication auth) {
        HorseFarrierApp link = horseFarrierAppService.addHorseToFarrierApp(dto.getFarrierAppId(), dto.getHorseId(), auth);
        return toDTO(link);
    }

    // Összes link lekérdezése
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping
    public List<HorseFarrierAppDTO> getAllHorseFarrierApps(Authentication auth) {
        List<HorseFarrierApp> links = horseFarrierAppService.getAllHorseFarrierApps(auth);
        return links.stream().map(this::toDTO).toList();
    }

    // Link lekérdezése id alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/{id}")
    public HorseFarrierAppDTO getHorseFarrierAppById(@PathVariable Long id, Authentication auth) {
        return toDTO(horseFarrierAppService.getHorseFarrierAppById(id, auth));
    }

    // Összes ló lekérdezése patkolás id alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/byFarrierAppId/{farrierAppId}")
    public List<HorseFarrierAppDTO> getHorsesForFarrierApp(@PathVariable Long farrierAppId, Authentication auth) {
        List<HorseFarrierApp> links = horseFarrierAppService.getHorsesForFarrierApp(farrierAppId, auth);
        return links.stream().map(this::toDTO).toList();
    }

    // Összes patkolás lekérdezése ló id alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/byHorseId/{horseId}")
    public List<HorseFarrierAppDTO> getFarrierAppsForHorse(@PathVariable Long horseId, Authentication auth) {
        List<HorseFarrierApp> links = horseFarrierAppService.getFarrierAppsForHorse(horseId, auth);
        return links.stream().map(this::toDTO).toList();
    }

    // Link törlése
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeHorseFromFarrierApp(@PathVariable Long id, Authentication auth) {
        HorseFarrierApp link = horseFarrierAppService.getHorseFarrierAppById(id, auth);
        horseFarrierAppService.removeHorseFromFarrierApp(link.getFarrierApp().getId(), link.getHorse().getId(), auth);
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
