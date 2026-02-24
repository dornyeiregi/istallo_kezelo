package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemDTO;
import edu.pte.ttk.istallo_kezelo.mapper.FeedSchedItemMapper;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.service.FeedSchedItemService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/feedSchedItems")
public class FeedSchedItemController {

    private final FeedSchedItemService feedSchedItemService;

    public FeedSchedItemController(FeedSchedItemService feedSchedItemService) {
        this.feedSchedItemService = feedSchedItemService;
    }

    @PostMapping()
    public FeedSchedItemDTO addItemToFeedSched(@RequestBody FeedSchedItemDTO dto) {
        FeedSchedItem link = feedSchedItemService.addItemToFeedSched(
            dto.getFeedSchedId(),
            dto.getItemId(),
            dto.getAmount()
        );
        return FeedSchedItemMapper.toDTO(link);
    }

    @GetMapping()
    public List<FeedSchedItemDTO> getAllFeedSchedItems() {
        List<FeedSchedItem> links = feedSchedItemService.getAllFeedSchedItems();
        return links.stream().map(FeedSchedItemMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public FeedSchedItemDTO getFeedSchedItemById(@PathVariable Long id) {
        FeedSchedItem link = feedSchedItemService.getFeedSchedItemById(id);
        return FeedSchedItemMapper.toDTO(link);
    }

    @GetMapping("/feedSchedId/{feedSchedId}")
    public List<FeedSchedItemDTO> getItemsForFeedSched(@PathVariable Long feedSchedId) {
        List<FeedSchedItem> links = feedSchedItemService.getItemsForFeedSched(feedSchedId);
        return links.stream().map(FeedSchedItemMapper::toDTO).toList();
    }

    @GetMapping("/itemId/{itemId}")
    public List<FeedSchedItemDTO> getFeedSchedsForItem(@PathVariable Long itemId) {
        List<FeedSchedItem> links = feedSchedItemService.getFeedSchedsForItem(itemId);
        return links.stream().map(FeedSchedItemMapper::toDTO).toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeItemFromFeedSched(@PathVariable Long id) {
        FeedSchedItem link = feedSchedItemService.getFeedSchedItemById(id);
        feedSchedItemService.removeItemFromFeedSched(link.getFeedSched().getId(), link.getItem().getId());
        return ResponseEntity.ok("Link sikeresen törölve.");
    }
}
