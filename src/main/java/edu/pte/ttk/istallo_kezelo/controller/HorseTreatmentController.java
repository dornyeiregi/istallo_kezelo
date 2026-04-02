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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * REST controller for horse-treatment links.
 */
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

}
