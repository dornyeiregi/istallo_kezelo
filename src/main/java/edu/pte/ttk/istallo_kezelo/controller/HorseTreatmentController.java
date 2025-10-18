package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.HorsetreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.service.HorseTreatmentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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
    public HorsetreatmentDTO addTreatmentToHorse(@RequestBody HorsetreatmentDTO dto) {
        HorseTreatment link = horseTreatmentService.addTreatmentToHorse(dto.treatmentId, dto.horseId);
        return toDTO(link);
    }


    // Összes link lekérdezése
    @GetMapping()
    public List<HorsetreatmentDTO> getAllHorseTreatments() {
        List<HorseTreatment> links = horseTreatmentService.getAllHorseTreatments();
        return links.stream().map(this::toDTO).toList();
    }


    // Link lekérdezése id alapján
    @GetMapping("/{id}")
    public HorsetreatmentDTO getHorsetreatmentById(@PathVariable Long id) {
        return toDTO(horseTreatmentService.getHorseTreatmentById(id));
    }
    
    // Egy ló minden kezelésének lekérdezése ló id alapján
    @GetMapping("/byHorse/{horseId}")
    public List<HorsetreatmentDTO> getHorseTreatmentsByHorseid(@PathVariable Long horseId) {
        List<HorseTreatment> links = horseTreatmentService.getTreatmentsForHorse(horseId);
        return links.stream().map(this::toDTO).toList();
    }

    // Kezelés eltávolítása lótól
    @DeleteMapping("/{id}")
    public void removeHorseTreatment(@PathVariable Long id){
        HorseTreatment link = horseTreatmentService.getHorseTreatmentById(id);
        horseTreatmentService.removeTreatmentFromHorse(link.getTreatment().getTreatmentId(), link.getHorse().getId());
    }
    

    private HorsetreatmentDTO toDTO(HorseTreatment link){
        HorsetreatmentDTO dto = new HorsetreatmentDTO();
        dto.horseId = link.getHorse().getId();
        dto.treatmentId = link.getTreatment().getTreatmentId();
        return dto;
    }


}
