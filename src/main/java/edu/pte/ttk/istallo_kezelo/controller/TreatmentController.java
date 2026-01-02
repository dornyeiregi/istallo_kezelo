package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.TreatmentDTO;
import edu.pte.ttk.istallo_kezelo.mapper.TreatmentMapper;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.service.TreatmentService;

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
@RequestMapping("/api/treatments")
public class TreatmentController {

    private final TreatmentService treatmentService;

    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    // Új kezelés létrehozása
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public TreatmentDTO createTreatment(@RequestBody TreatmentDTO dto, Authentication auth) {
        Treatment treatment = new Treatment();
        treatment.setTreatmentName(dto.getTreatmentName());
        treatment.setDescription(dto.getDescription());
        treatment.setDate(dto.getDate());

        Treatment saved = treatmentService.saveTreatment(treatment, auth);
        return TreatmentMapper.toDTO(saved);
    }

    // Összes kezelés lekérdezése
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<TreatmentDTO> getAllTreatments(Authentication auth) {
        List<Treatment> treatments = treatmentService.getAllTreatments(auth);
        return treatments.stream().map(TreatmentMapper::toDTO).toList();
    }

    // Kezelés lekérdezése ID alapján
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public TreatmentDTO getTreatmentById(@PathVariable Long id, Authentication auth) {
        Treatment treatment = treatmentService.getTreatmentById(id, auth);
        return TreatmentMapper.toDTO(treatment);
    }

    // Egy ló minden kezelésének lekérdezése ló ID alapján
    @GetMapping("/horseId/{horseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<TreatmentDTO> getAllTreatmentsOfHorseById(@PathVariable Long horseId, Authentication auth) {
        List<Treatment> treatments = treatmentService.getTreatmentsByHorseId(horseId, auth);
        return treatments.stream().map(TreatmentMapper::toDTO).toList();
    }

    // Egy ló minden kezelésének lekérdezése ló név alapján
    @GetMapping("/horseName/{horseName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<TreatmentDTO> getAllTreatmentsOfHorseByName(@PathVariable String horseName, Authentication auth) {
        List<Treatment> treatments = treatmentService.getTreatmentsByHorseName(horseName, auth);
        return treatments.stream().map(TreatmentMapper::toDTO).toList();
    }

    // Kezelés frissítése
    @PatchMapping("/{treatmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<String> updateTreatment(@PathVariable Long treatmentId, @RequestBody TreatmentDTO dto, Authentication auth) {
        treatmentService.updateTreatment(treatmentId, dto, auth);
        return ResponseEntity.ok("Kezelés sikeresen frissítve.");
    }

    // Kezelés törlése
    @DeleteMapping("/{treatmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<String> deleteTreatment(@PathVariable Long treatmentId, Authentication auth) {
        treatmentService.deleteTreatmentById(treatmentId, auth);
        return ResponseEntity.ok("Kezelés sikeresen törölve.");
    }
}
