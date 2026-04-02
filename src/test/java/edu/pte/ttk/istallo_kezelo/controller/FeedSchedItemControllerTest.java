package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.service.FeedSchedItemService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for FeedSchedItemController behavior.
 */
@ExtendWith(MockitoExtension.class)
class FeedSchedItemControllerTest {

    @Mock
    private FeedSchedItemService feedSchedItemService;

    @InjectMocks
    private FeedSchedItemController feedSchedItemController;

    @Test
    void getAllFeedSchedItems_returnsMappedDtos() {
        FeedSched feedSched = ControllerTestSupport.feedSched(1L, "Morning");
        Item item = ControllerTestSupport.item(2L, "Hay");
        var link = ControllerTestSupport.feedSchedItem(feedSched, item, 3.5);
        when(feedSchedItemService.getAllFeedSchedItems()).thenReturn(List.of(link));

        List<FeedSchedItemDTO> result = feedSchedItemController.getAllFeedSchedItems();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getFeedSchedId());
        assertEquals(2L, result.get(0).getItemId());
    }
}
