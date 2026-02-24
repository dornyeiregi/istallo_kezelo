package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.dto.FarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.mapper.FarrierAppMapper;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.service.FarrierAppService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<FarrierAppDTO> createFarrierApp(@RequestBody FarrierAppDTO dto, Authentication auth) {
        FarrierApp created = farrierAppService.createFarrierApp(dto, auth);
        return ResponseEntity.ok(FarrierAppMapper.toDTO(created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<FarrierAppDTO> getAllFarrierApps(Authentication auth) {
        List<FarrierApp> farrierApps = farrierAppService.getAllFarrierApps(auth);
        return farrierApps.stream().map(FarrierAppMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public FarrierAppDTO getFarrierAppById(@PathVariable Long id, Authentication auth) {
        FarrierApp farrierApp = farrierAppService.getFarrierAppById(id, auth);
        if (farrierApp == null) {
            throw new RuntimeException("Patkolás nem található.");
        }
        return FarrierAppMapper.toDTO(farrierApp);
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<FarrierAppDTO> getFarrierAppsByDate(@PathVariable LocalDate date, Authentication auth) {
        List<FarrierApp> farrierApps = farrierAppService.getFarrierAppsByDate(date, auth);
        return farrierApps.stream().map(FarrierAppMapper::toDTO).toList();
    }

    @GetMapping("/farrierName/{farrierName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<FarrierAppDTO> getFarrierAppsByFarrierName(@PathVariable String farrierName, Authentication auth) {
        List<FarrierApp> farrierApps = farrierAppService.getFarrierAppsByFarrierName(farrierName, auth);
        return farrierApps.stream().map(FarrierAppMapper::toDTO).toList();
    }

    @GetMapping("/horseName/{horseName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<FarrierAppDTO> getFarrierAppsByHorseName(@PathVariable String horseName, Authentication auth) {
        List<FarrierApp> farrierApps = farrierAppService.getFarrierAppsByHorseName(horseName, auth);
        return farrierApps.stream().map(FarrierAppMapper::toDTO).toList();
    }

    @GetMapping("/horseId/{horseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<FarrierAppDTO> getFarrierAppsByHorseId(@PathVariable Long horseId, Authentication auth) {
        List<FarrierApp> farrierApps = farrierAppService.getFarrierAppByHorseId(horseId, auth);
        return farrierApps.stream().map(FarrierAppMapper::toDTO).toList();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<String> updateFarrierApp(@PathVariable Long id, @RequestBody FarrierAppDTO dto, Authentication auth) {
        farrierAppService.updateFarrierApp(id, dto, auth);
        return ResponseEntity.ok("Patkolás sikeresen frissítve.");
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deleteFarrierApp(@PathVariable Long id) {
        farrierAppService.deleteFarrierApp(id);
        return ResponseEntity.ok("Patkolás sikeresen törölve.");
    }
}
