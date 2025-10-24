package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.HorseTreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.service.HorseTreatmentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/horseTreatments")
public class HorseTreatmentController {

    private final HorseTreatmentService horseTreatmentService;

    public HorseTreatmentController(HorseTreatmentService horseTreatmentService){
        this.horseTreatmentService = horseTreatmentService;
    }

    // Kezelés hozzáadása lóhoz
    @PostMapping()
    public HorseTreatmentDTO addTreatmentToHorse(@RequestBody HorseTreatmentDTO dto) {
        HorseTreatment link = horseTreatmentService.addTreatmentToHorse(dto.getTreatmentId(), dto.getHorseId());
        return toDTO(link);
    }


    // Összes link lekérdezése
    @GetMapping()
    public List<HorseTreatmentDTO> getAllHorseTreatments() {
        List<HorseTreatment> links = horseTreatmentService.getAllHorseTreatments();
        return links.stream().map(this::toDTO).toList();
    }


    // Link lekérdezése id alapján
    @GetMapping("/{id}")
    public HorseTreatmentDTO getHorsetreatmentById(@PathVariable Long id) {
        return toDTO(horseTreatmentService.getHorseTreatmentById(id));
    }
    
    // Egy ló minden kezelésének lekérdezése ló id alapján
    @GetMapping("/byHorseId/{horseId}")
    public List<HorseTreatmentDTO> getHorseTreatmentsByHorseid(@PathVariable Long horseId) {
        List<HorseTreatment> links = horseTreatmentService.getTreatmentsForHorse(horseId);
        return links.stream().map(this::toDTO).toList();
    }

    // Kezelt lovak lekérdezése kezelés id alapján
    @GetMapping("/byTreatmentId/{treatmentId}")
    public List<HorseTreatmentDTO> getHorseTreatmentsbyTreatmentId(@PathVariable Long treatmentId){
        List<HorseTreatment> links = horseTreatmentService.getHorsesByTreatment(treatmentId);
        return links.stream().map(this::toDTO).toList();
    }
    

    // Kezelés eltávolítása lótól
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeHorseTreatment(@PathVariable Long id){
        HorseTreatment link = horseTreatmentService.getHorseTreatmentById(id);
        horseTreatmentService.removeTreatmentFromHorse(link.getTreatment().getTreatmentId(), link.getHorse().getId());
        return ResponseEntity.ok("Link sikeresen törölve.");
    }
    

    private HorseTreatmentDTO toDTO(HorseTreatment link){
        HorseTreatmentDTO dto = new HorseTreatmentDTO();
        dto.setHorseId(link.getHorse().getId());
        dto.setTreatmentId(link.getTreatment().getTreatmentId());
        dto.setTreatmentName(link.getTreatment().getTreatmentName());
        dto.setHorseName(link.getHorse().getHorseName());
        dto.setDate(link.getTreatment().getDate());
        return dto;
    }


}
