package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemDTO;
import edu.pte.ttk.istallo_kezelo.mapper.FeedSchedItemMapper;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.service.FeedSchedItemService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/feedSchedItems")
public class FeedSchedItemController {

    private final FeedSchedItemService feedSchedItemService;

    public FeedSchedItemController(FeedSchedItemService feedSchedItemService) {
        this.feedSchedItemService = feedSchedItemService;
    }

    @GetMapping()
    public List<FeedSchedItemDTO> getAllFeedSchedItems() {
        List<FeedSchedItem> links = feedSchedItemService.getAllFeedSchedItems();
        return links.stream().map(FeedSchedItemMapper::toDTO).toList();
    }
}
