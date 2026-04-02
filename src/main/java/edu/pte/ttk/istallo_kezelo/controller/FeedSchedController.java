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


/**
 * Etetési ütemtervek CRUD és változtatási kérelmek vezérlője.
 */
@RestController
@RequestMapping("/api/feedScheds")
public class FeedSchedController {

    private final FeedSchedService feedSchedService;

    /**
     * Létrehozza a vezérlőt a szükséges szolgáltatással.
     *
     * @param feedSchedService etetési ütemterv szolgáltatás
     */
    public FeedSchedController(FeedSchedService feedSchedService){
        this.feedSchedService = feedSchedService;
    }

    /**
     * Etetési ütemterv létrehozása (owner esetén kérelemként).
     *
     * @param dto  ütemterv adatok
     * @param auth hitelesítési adatok
     * @return státusz üzenet
     */
    @PostMapping
    public ResponseEntity<String> createFeedSched(@RequestBody FeedSchedDTO dto, Authentication auth) {
        boolean isOwner = auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"));
        if (isOwner) {
            feedSchedService.createFeedSchedRequest(dto, auth);
            return ResponseEntity.accepted().body("Kérés elküldve. Jóváhagyás után lép életbe.");
        }
        FeedSched createdFeedSched = feedSchedService.createFeedSched(dto);
        return ResponseEntity.ok("Etetési napló sikeresen létrehozva.");
    }

    /**
     * Összes etetési ütemterv lekérése.
     *
     * @return ütemtervek listája
     */
    @GetMapping
    public List<FeedSchedDTO> getAllFeedScheds() {
        List<FeedSched> feedScheds = feedSchedService.getAllFeedScheds();
        return feedScheds.stream().map(FeedSchedMapper::toDTO).toList();
    }

    /**
     * Etetési ütemterv lekérése azonosító alapján.
     *
     * @param id ütemterv azonosító
     * @return ütemterv DTO
     */
    @GetMapping("/{id}")
    public FeedSchedDTO getFeedSchedById(@PathVariable Long id) {
        FeedSched feedSched = feedSchedService.getFeedSchedById(id);
        if (feedSched == null) {
            throw new RuntimeException("Etetési napló nem található.");
        }
        return FeedSchedMapper.toDTO(feedSched);
    }

    /**
     * Etetési ütemtervek lekérése ló azonosító alapján.
     *
     * @param horseId ló azonosító
     * @return ütemtervek listája
     */
    @GetMapping("/horseId/{horseId}")
    public List<FeedSchedDTO> getFeedSchedsByHorseId(@PathVariable Long horseId) {
        List<FeedSched> feedScheds = feedSchedService.getFeedSchedByHorseId(horseId);
        return feedScheds.stream().map(FeedSchedMapper::toDTO).toList();
    }

    /**
     * Etetési ütemterv frissítése (owner esetén kérelemként).
     *
     * @param id   ütemterv azonosító
     * @param dto  módosítási adatok
     * @param auth hitelesítési adatok
     * @return státusz üzenet
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<String> updateFeedSched(@PathVariable Long id, @RequestBody FeedSchedDTO dto, Authentication auth) {
        boolean applied = feedSchedService.updateFeedSched(id, dto, auth);
        if (applied) {
            return ResponseEntity.ok("Etetési napló sikeresen frissítve.");
        }
        return ResponseEntity.accepted().body("Kérés elküldve. Jóváhagyás után lép életbe.");
    }

    /**
     * Etetési ütemterv törlése.
     *
     * @param id ütemterv azonosító
     * @return státusz üzenet
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFeedSched(@PathVariable Long id){
        feedSchedService.deleteFeedSched(id);
        return ResponseEntity.ok("Etetési napló sikeresen törölve.");
    }

    /**
     * Összes változtatási kérelem lekérése (admin).
     *
     * @return kérelmek listája
     */
    @GetMapping("/requests")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<FeedSchedChangeRequestDTO> getChangeRequests() {
        List<FeedSchedChangeRequest> requests = feedSchedService.getAllChangeRequests();
        return requests.stream()
            .map(r -> FeedSchedChangeRequestMapper.toDTO(
                r,
                feedSchedService.parseIds(r.getRequestedHorseIds()),
                feedSchedService.parseIds(r.getRequestedItemIds()),
                feedSchedService.parseItemAmounts(r.getRequestedItemAmounts())
            ))
            .toList();
    }

    /**
     * Saját változtatási kérelmek lekérése.
     *
     * @param auth hitelesítési adatok
     * @return kérelmek listája
     */
    @GetMapping("/requests/mine")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ROLE_OWNER')")
    public List<FeedSchedChangeRequestDTO> getMyChangeRequests(Authentication auth) {
        List<FeedSchedChangeRequest> requests = feedSchedService.getMyChangeRequests(auth);
        return requests.stream()
            .map(r -> FeedSchedChangeRequestMapper.toDTO(
                r,
                feedSchedService.parseIds(r.getRequestedHorseIds()),
                feedSchedService.parseIds(r.getRequestedItemIds()),
                feedSchedService.parseItemAmounts(r.getRequestedItemAmounts())
            ))
            .toList();
    }

    /**
     * Változtatási kérelem jóváhagyása.
     *
     * @param id kérelem azonosító
     * @return státusz üzenet
     */
    @PatchMapping("/requests/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> approveChangeRequest(@PathVariable Long id) {
        feedSchedService.approveChangeRequest(id);
        return ResponseEntity.ok("Kérés jóváhagyva.");
    }

    /**
     * Változtatási kérelem elutasítása.
     *
     * @param id kérelem azonosító
     * @return státusz üzenet
     */
    @DeleteMapping("/requests/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> rejectChangeRequest(@PathVariable Long id) {
        feedSchedService.rejectChangeRequest(id);
        return ResponseEntity.ok("Kérés elutasítva.");
    }
}
