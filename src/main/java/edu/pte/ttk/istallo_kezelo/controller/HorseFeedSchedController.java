package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pte.ttk.istallo_kezelo.dto.HorseFeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.service.HorseFeedSchedService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
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

    // Ló hozzáadása etetési naplóhoz
    @PostMapping()
    public HorseFeedSchedDTO addHorseToFeedSched(@RequestBody HorseFeedSchedDTO dto){
        HorseFeedSched link = horseFeedSchedService.addHorseToFeedSched(dto.feedSchedId, dto.horseId);
        return toDTO(link);
    }

    // Összes link lekérdezése
    @GetMapping()
    public List<HorseFeedSchedDTO> getAllHorseFeedScheds(){
        List<HorseFeedSched> links = horseFeedSchedService.getAllHorseFeedScheds();
        return links.stream().map(this::toDTO).toList();
    }

    // Link lekérdezése id alapján
    @GetMapping("/{id}")
    public HorseFeedSchedDTO getHorseFeedSchedById(@PathVariable Long id){
        HorseFeedSched link = horseFeedSchedService.getHorseFeedSchedById(id);
        return toDTO(link);
    }

    // Összes ló lekérdezése etetési naplóhoz
    @GetMapping("/byFeedSched/{feedSchedId}")
    public List<HorseFeedSchedDTO> getHorsesForFeedSched(@PathVariable Long feedSchedId){
        List<HorseFeedSched> links = horseFeedSchedService.getHorsesForFeedSChed(feedSchedId);
        return links.stream().map(this::toDTO).toList();
    }

    // Lóhoz tartozó összes etetési napló lekérdezése
    @GetMapping("/byHorse/{horseId}")
    public List<HorseFeedSchedDTO> getFeedSchedsForHorse(@PathVariable Long horseId){
        List<HorseFeedSched> links = horseFeedSchedService.getFeedSchedsForHorse(horseId);
        return links.stream().map(this::toDTO).toList();
    }

    // Ló eltávolítása etetési naplóból
    @DeleteMapping("/{id}")
    public void removeHorseFromFeedSched(@PathVariable Long id){
        HorseFeedSched link = horseFeedSchedService.getHorseFeedSchedById(id);
        horseFeedSchedService.removeHorseFromFeedSched(link.getFeedSched().getFeedSchedid(), link.getHorse().getId());
    }
    

    private HorseFeedSchedDTO toDTO(HorseFeedSched horseFeedSched){
        HorseFeedSchedDTO dto = new HorseFeedSchedDTO();
        dto.horseId = horseFeedSched.getHorse().getId();
        dto.feedSchedId = horseFeedSched.getFeedSched().getFeedSchedid();
        dto.feedDescription = horseFeedSched.getFeedSched().getDescription();
        dto.horseName = horseFeedSched.getHorse().getHorseName();
        return dto;
    }
}
