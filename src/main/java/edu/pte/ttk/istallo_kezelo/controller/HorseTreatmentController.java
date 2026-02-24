package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.dto.HorseTreatmentDTO;
import edu.pte.ttk.istallo_kezelo.mapper.HorseTreatmentMapper;
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

    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public HorseTreatmentDTO addTreatmentToHorse(@RequestBody HorseTreatmentDTO dto, Authentication auth) {
        HorseTreatment link = horseTreatmentService.addTreatmentToHorse(dto.getTreatmentId(), dto.getHorseId(), auth);
        return HorseTreatmentMapper.toDTO(link);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseTreatmentDTO> getAllHorseTreatments(Authentication auth) {
        List<HorseTreatment> links = horseTreatmentService.getAllHorseTreatments(auth);
        return links.stream().map(HorseTreatmentMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public HorseTreatmentDTO getHorsetreatmentById(@PathVariable Long id, Authentication auth) {
        return HorseTreatmentMapper.toDTO(horseTreatmentService.getHorseTreatmentById(id, auth));
    }
    
    @GetMapping("/byHorseId/{horseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseTreatmentDTO> getHorseTreatmentsByHorseid(@PathVariable Long horseId, Authentication auth) {
        List<HorseTreatment> links = horseTreatmentService.getTreatmentsForHorse(horseId, auth);
        return links.stream().map(HorseTreatmentMapper::toDTO).toList();
    }

    @GetMapping("/byTreatmentId/{treatmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseTreatmentDTO> getHorseTreatmentsbyTreatmentId(@PathVariable Long treatmentId, Authentication auth){
        List<HorseTreatment> links = horseTreatmentService.getHorsesByTreatment(treatmentId, auth);
        return links.stream().map(HorseTreatmentMapper::toDTO).toList();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<String> removeHorseTreatment(@PathVariable Long id, Authentication auth){
        HorseTreatment link = horseTreatmentService.getHorseTreatmentById(id, auth);
        horseTreatmentService.removeTreatmentFromHorse(link.getTreatment().getId(), link.getHorse().getId(), auth);
        return ResponseEntity.ok("Link sikeresen törölve.");
    }
}
