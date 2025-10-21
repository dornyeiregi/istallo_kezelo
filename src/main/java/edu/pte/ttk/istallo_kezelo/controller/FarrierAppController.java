package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.FarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.service.FarrierAppService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/farrierApps")
public class FarrierAppController {

    private final FarrierAppService farrierAppService;

    public FarrierAppController(FarrierAppService farrierAppService) {
        this.farrierAppService = farrierAppService;
    }


    // Új patkolás hozzáadása
    @PostMapping
    public ResponseEntity<FarrierAppDTO> createFarrierApp(@RequestBody FarrierAppDTO dto) {
        FarrierApp created = farrierAppService.createFarrierApp(dto);
        return ResponseEntity.ok(toDTO(created));
    }


    // Összes patkolás lekérdezése
    @GetMapping
    public List<FarrierAppDTO> getAllFarrierApps() {
        Iterable<FarrierApp> farrierApps = farrierAppService.getAllFarrierApps();
        return ((List<FarrierApp>) farrierApps).stream()
                .map(this::toDTO)
                .toList();
    }

    // Patkolás lekérdezése id alaján
    @GetMapping("/{id}")
    public FarrierAppDTO getFarrierAppById(@PathVariable Long id) {
        FarrierApp farrierApp = farrierAppService.getFarrierAppById(id);
        if (farrierApp == null) {
            throw new RuntimeException("Patkolás nem található.");
        }
        return toDTO(farrierApp);
    }

    // Patkolás lekérdezése dátum alaján
    @GetMapping("/byDate/{date}")
    public List<FarrierAppDTO> getFarrierAppsByDate(@PathVariable LocalDate date) {
        List<FarrierApp> farrierApps = farrierAppService.getFarrierAppsByDate(date);
        return ((List<FarrierApp>) farrierApps).stream()
                .map(this::toDTO)
                .toList();
    }

    // Patkolás lekérdezése patkolókovács neve alaján
    @GetMapping("/byFarrierName/{farrierName}")
    public List<FarrierAppDTO> getFarrierAppsByFarrierName(@PathVariable String farrierName) {
        List<FarrierApp> farrierApps = farrierAppService.getFarrierAppsByFarrierName(farrierName);
        return ((List<FarrierApp>) farrierApps).stream()
                .map(this::toDTO)
                .toList();
    }

    // Patkolás lekérdezése ló neve alaján
    @GetMapping("/byHorseName/{horseName}")
    public List<FarrierAppDTO> getFarrierAppsByHorseName(@PathVariable String horseName) {
        List<FarrierApp> farrierApps = farrierAppService.getFarrierAppsByHorseName(horseName);
        return ((List<FarrierApp>)  farrierApps).stream()
                .map(this::toDTO)
                .toList();
    }

    // Patkolás lekérdezése ló id alaján
    @GetMapping("/byHorseId/{horseId}")
    public List<FarrierAppDTO> getFarrierAppsByHorseId(@PathVariable Long horseId) {
        Iterable<FarrierApp> farrierApps = farrierAppService.getFarrierAppByHorseId(horseId);
        return ((List<FarrierApp>) farrierApps).stream()
                .map(this::toDTO)
                .toList();
    }

    // Patkolás frissítése
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateFarrierApp(@PathVariable Long id, @RequestBody FarrierAppDTO dto) {
        farrierAppService.updateFarrierApp(id, dto);
        return ResponseEntity.ok("Patkolás sikeresen frissítve.");
    }
    
    // Patkolás törlése
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFarrierApp(@PathVariable Long id) {
        farrierAppService.deleteFarrierApp(id);
        return ResponseEntity.ok("Patkolás sikeresen törölve.");
    }

    private FarrierAppDTO toDTO(FarrierApp farrierApp) {
        FarrierAppDTO dto = new FarrierAppDTO();
        dto.appointmentDate = farrierApp.getAppointmentDate();
        dto.farrierPhone = farrierApp.getFarrierPhone();
        dto.farrierName = farrierApp.getFarrierName();
        dto.shoes = farrierApp.getShoes();
        dto.horseIds = farrierApp.getHorses_done().stream()
                .map(hfa -> hfa.getHorse().getId())
                .toList();
        return dto;
    }

}
