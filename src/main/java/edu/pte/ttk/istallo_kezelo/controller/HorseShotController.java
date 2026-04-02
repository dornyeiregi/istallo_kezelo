package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.dto.HorseShotDTO;
import edu.pte.ttk.istallo_kezelo.mapper.HorseShotMapper;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.service.HorseShotService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

/**
 * REST controller for horse-shot links.
 */
@RestController
@RequestMapping("/api/horseShots")
public class HorseShotController {

    private final HorseShotService horseShotService;

    public HorseShotController(HorseShotService horseShotService){
        this.horseShotService = horseShotService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PostMapping()
    public HorseShotDTO addShotToHorse(@RequestBody HorseShotDTO dto, Authentication auth) {
        HorseShot link = horseShotService.addShotToHorse(dto.getShotId(), dto.getHorseId(), auth);
        return HorseShotMapper.toDTO(link);
    }

}
