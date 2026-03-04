package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.dto.HorseFeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.mapper.HorseFeedSchedMapper;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.service.HorseFeedSchedService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/horseFeedScheds")
public class HorseFeedSchedController {

    private final HorseFeedSchedService horseFeedSchedService;

    public HorseFeedSchedController(HorseFeedSchedService horseFeedSchedService) {
        this.horseFeedSchedService = horseFeedSchedService;
    }

    @PostMapping()
    public HorseFeedSchedDTO addHorseToFeedSched(@RequestBody HorseFeedSchedDTO dto){
        HorseFeedSched link = horseFeedSchedService.addHorseToFeedSched(dto.getFeedSchedId(), dto.getHorseId());
        return HorseFeedSchedMapper.toDTO(link);
    }

    @GetMapping()
    public List<HorseFeedSchedDTO> getAllHorseFeedScheds(){
        List<HorseFeedSched> links = horseFeedSchedService.getAllHorseFeedScheds();
        return links.stream().map(HorseFeedSchedMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public HorseFeedSchedDTO getHorseFeedSchedById(@PathVariable Long id){
        HorseFeedSched link = horseFeedSchedService.getHorseFeedSchedById(id);
        return HorseFeedSchedMapper.toDTO(link);
    }

    @GetMapping("/horseId/{horseId}")
    public List<HorseFeedSchedDTO> getFeedSchedsForHorse(@PathVariable Long horseId){
        List<HorseFeedSched> links = horseFeedSchedService.getFeedSchedsForHorse(horseId);
        return links.stream().map(HorseFeedSchedMapper::toDTO).toList();
    }

}
