package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedChangeRequestDTO;
import edu.pte.ttk.istallo_kezelo.mapper.FeedSchedChangeRequestMapper;
import edu.pte.ttk.istallo_kezelo.mapper.FeedSchedMapper;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedChangeRequest;
import edu.pte.ttk.istallo_kezelo.service.FeedSchedService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/api/feedScheds")
public class FeedSchedController {

    private final FeedSchedService feedSchedService;

    public FeedSchedController(FeedSchedService feedSchedService){
        this.feedSchedService = feedSchedService;
    }

    @PostMapping
    public ResponseEntity<FeedSchedDTO> createFeedSched(@RequestBody FeedSchedDTO dto) {
        FeedSched createdFeedSched = feedSchedService.createFeedSched(dto);
        return ResponseEntity.ok(FeedSchedMapper.toDTO(createdFeedSched));
    }

    @GetMapping
    public List<FeedSchedDTO> getAllFeedScheds() {
        List<FeedSched> feedScheds = feedSchedService.getAllFeedScheds();
        return feedScheds.stream().map(FeedSchedMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public FeedSchedDTO getFeedSchedById(@PathVariable Long id) {
        FeedSched feedSched = feedSchedService.getFeedSchedById(id);
        if (feedSched == null) {
            throw new RuntimeException("Etetési napló nem található.");
        }
        return FeedSchedMapper.toDTO(feedSched);
    }

    @GetMapping("/horseId/{horseId}")
    public List<FeedSchedDTO> getFeedSchedsByHorseId(@PathVariable Long horseId) {
        List<FeedSched> feedScheds = feedSchedService.getFeedSchedByHorseId(horseId);
        return feedScheds.stream().map(FeedSchedMapper::toDTO).toList();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<String> updateFeedSched(@PathVariable Long id, @RequestBody FeedSchedDTO dto, Authentication auth) {
        boolean applied = feedSchedService.updateFeedSched(id, dto, auth);
        if (applied) {
            return ResponseEntity.ok("Etetési napló sikeresen frissítve.");
        }
        return ResponseEntity.accepted().body("Kérés elküldve. Jóváhagyás után lép életbe.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFeedSched(@PathVariable Long id){
        feedSchedService.deleteFeedSched(id);
        return ResponseEntity.ok("Etetési napló sikeresen törölve.");
    }

    @GetMapping("/requests")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<FeedSchedChangeRequestDTO> getChangeRequests() {
        List<FeedSchedChangeRequest> requests = feedSchedService.getAllChangeRequests();
        return requests.stream()
            .map(r -> FeedSchedChangeRequestMapper.toDTO(
                r,
                feedSchedService.parseIds(r.getRequestedHorseIds()),
                feedSchedService.parseIds(r.getRequestedItemIds())
            ))
            .toList();
    }

    @PatchMapping("/requests/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> approveChangeRequest(@PathVariable Long id) {
        feedSchedService.approveChangeRequest(id);
        return ResponseEntity.ok("Kérés jóváhagyva.");
    }

    @DeleteMapping("/requests/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> rejectChangeRequest(@PathVariable Long id) {
        feedSchedService.rejectChangeRequest(id);
        return ResponseEntity.ok("Kérés elutasítva.");
    }
}
