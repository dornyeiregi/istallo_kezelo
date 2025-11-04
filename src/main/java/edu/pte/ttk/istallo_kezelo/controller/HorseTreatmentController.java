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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public HorseTreatmentDTO addTreatmentToHorse(@RequestBody HorseTreatmentDTO dto, Authentication auth) {
        HorseTreatment link = horseTreatmentService.addTreatmentToHorse(dto.getTreatmentId(), dto.getHorseId(), auth);
        return toDTO(link);
    }


    // Összes link lekérdezése
    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseTreatmentDTO> getAllHorseTreatments(Authentication auth) {
        List<HorseTreatment> links = horseTreatmentService.getAllHorseTreatments(auth);
        return links.stream().map(this::toDTO).toList();
    }


    // Link lekérdezése id alapján
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public HorseTreatmentDTO getHorsetreatmentById(@PathVariable Long id, Authentication auth) {
        return toDTO(horseTreatmentService.getHorseTreatmentById(id, auth));
    }
    
    // Egy ló minden kezelésének lekérdezése ló id alapján
    @GetMapping("/byHorseId/{horseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseTreatmentDTO> getHorseTreatmentsByHorseid(@PathVariable Long horseId, Authentication auth) {
        List<HorseTreatment> links = horseTreatmentService.getTreatmentsForHorse(horseId, auth);
        return links.stream().map(this::toDTO).toList();
    }

    // Kezelt lovak lekérdezése kezelés id alapján
    @GetMapping("/byTreatmentId/{treatmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseTreatmentDTO> getHorseTreatmentsbyTreatmentId(@PathVariable Long treatmentId, Authentication auth){
        List<HorseTreatment> links = horseTreatmentService.getHorsesByTreatment(treatmentId, auth);
        return links.stream().map(this::toDTO).toList();
    }
    

    // Kezelés eltávolítása lótól
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<String> removeHorseTreatment(@PathVariable Long id, Authentication auth){
        HorseTreatment link = horseTreatmentService.getHorseTreatmentById(id, auth);
        horseTreatmentService.removeTreatmentFromHorse(link.getTreatment().getTreatmentId(), link.getHorse().getId(), auth);
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
