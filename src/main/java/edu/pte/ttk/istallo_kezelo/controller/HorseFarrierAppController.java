package edu.pte.ttk.istallo_kezelo.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import edu.pte.ttk.istallo_kezelo.dto.HorseFarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.mapper.HorseFarrierAppMapper;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.service.HorseFarrierAppService;

@RestController
@RequestMapping("/api/horseFarrierApps")
public class HorseFarrierAppController {

    private final HorseFarrierAppService horseFarrierAppService;

    public HorseFarrierAppController(HorseFarrierAppService horseFarrierAppService) {
        this.horseFarrierAppService = horseFarrierAppService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PostMapping
    public HorseFarrierAppDTO addHorseToFarrierApp(@RequestBody HorseFarrierAppDTO dto, Authentication auth) {
        HorseFarrierApp link = horseFarrierAppService.addHorseToFarrierApp(dto.getFarrierAppId(), dto.getHorseId(), auth);
        return HorseFarrierAppMapper.toDTO(link);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping
    public List<HorseFarrierAppDTO> getAllHorseFarrierApps(Authentication auth) {
        List<HorseFarrierApp> links = horseFarrierAppService.getAllHorseFarrierApps(auth);
        return links.stream().map(HorseFarrierAppMapper::toDTO).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/{id}")
    public HorseFarrierAppDTO getHorseFarrierAppById(@PathVariable Long id, Authentication auth) {
        return HorseFarrierAppMapper.toDTO(horseFarrierAppService.getHorseFarrierAppById(id, auth));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/byHorseId/{horseId}")
    public List<HorseFarrierAppDTO> getFarrierAppsForHorse(@PathVariable Long horseId, Authentication auth) {
        List<HorseFarrierApp> links = horseFarrierAppService.getFarrierAppsForHorse(horseId, auth);
        return links.stream().map(HorseFarrierAppMapper::toDTO).toList();
    }

}
