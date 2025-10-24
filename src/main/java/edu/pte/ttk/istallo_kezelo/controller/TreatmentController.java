package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.TreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.service.TreatmentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {
    private final TreatmentService treatmentService;

    public TreatmentController(TreatmentService treatmentService){
        this.treatmentService = treatmentService;
    }

    // Új kezelés hozzáadása
    @PostMapping()
    public TreatmentDTO createTreatment(@RequestBody TreatmentDTO dto) {
        Treatment treatment = new Treatment();
        treatment.setTreatmentName(dto.getTreatmentName());
        treatment.setDescription(dto.getDescription());
        treatment.setDate(dto.getDate());

        Treatment savedTreatment = treatmentService.saveTreatment(treatment);

        return toDTO(savedTreatment);
    }

    // Összes kezelés lekérdezése
    @GetMapping()
    public List<TreatmentDTO> getAllTreatments() {
        List<Treatment> treatments = treatmentService.getAllTreatments();
        return treatments.stream().map(this::toDTO).toList();
    }

    // Kezelés lekérdezése id alapján
    @GetMapping("/{id}")
    public TreatmentDTO getTreatmentById(@PathVariable Long id) {
        return toDTO(treatmentService.getTreatmentById(id));
    }

    // Egy ló minden kezelésének lekérdezése ló id alapján
    @GetMapping("/byHorseId/{horseId}")
    public List<TreatmentDTO> getAllTreatmentsOfHorseById(@PathVariable Long horseId) {
        List<Treatment> treatments = treatmentService.getTreatmentsByHorseId(horseId);
        return treatments.stream().map(this::toDTO).toList();
    }

    
    // Egy ló minden kezelésének lekérdezése ló neve alapján
    @GetMapping("/byHorseName/{horseName}")
    public List<TreatmentDTO> getAllTreatmentsOfHorseByName(@PathVariable String horseName) {
        List<Treatment> treatments = treatmentService.getTreatmentsByHorseName(horseName);
        return treatments.stream().map(this::toDTO).toList();
    }
    
    
    // Kezelés frissítése
    @PatchMapping("/{treatmentId}")
    public ResponseEntity<String> updateTreatment(@PathVariable Long treatmentId, @RequestBody TreatmentDTO dto){
        treatmentService.updateTreatment(treatmentId, dto);
        return ResponseEntity.ok("Kezelés sikeresen frissítve.");
    }

    // Kezelés törlése
    @DeleteMapping("/{treatmentId}")
    public ResponseEntity<String> deleteTreatment(@PathVariable Long treatmentId){
        treatmentService.deleteTreatmentById(treatmentId);
        return ResponseEntity.ok("Kezelés sikeresen törölve.");
    }

    private TreatmentDTO toDTO(Treatment treatment){
        TreatmentDTO dto = new TreatmentDTO();
        dto.setTreatmentId(treatment.getTreatmentId());
        dto.setTreatmentName(treatment.getTreatmentName());
        dto.setDescription(treatment.getDescription());
        dto.setDate(treatment.getDate());
        return dto;
    }
}
