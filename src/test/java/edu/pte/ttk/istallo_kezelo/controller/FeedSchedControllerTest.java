package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.FeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemAmountDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.service.FeedSchedService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class FeedSchedControllerTest {

    @Mock
    private FeedSchedService feedSchedService;

    @InjectMocks
    private FeedSchedController feedSchedController;

    @Test
    void createFeedSched_returnsMappedDto() {
        FeedSched feedSched = ControllerTestSupport.feedSched(1L, "Morning");
        Item item = ControllerTestSupport.item(5L, "Hay");
        feedSched.getFeedSchedItems().add(ControllerTestSupport.feedSchedItem(feedSched, item, 3.5));

        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setDescription("Morning");
        when(feedSchedService.createFeedSched(dto)).thenReturn(feedSched);

        FeedSchedDTO result = feedSchedController.createFeedSched(dto).getBody();

        assertEquals(1L, result.getFeedSchedId());
        assertEquals("Morning", result.getDescription());
        assertEquals(List.of(5L), result.getItemIds());
        assertEquals(List.of(new FeedSchedItemAmountDTO(5L, 3.5)), result.getItems());
    }

    @Test
    void getAllFeedScheds_returnsMappedDtos() {
        FeedSched feedSched = ControllerTestSupport.feedSched(1L, "Morning");
        when(feedSchedService.getAllFeedScheds()).thenReturn(List.of(feedSched));

        List<FeedSchedDTO> result = feedSchedController.getAllFeedScheds();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getFeedSchedId());
    }

    @Test
    void getFeedSchedById_throwsWhenMissing() {
        when(feedSchedService.getFeedSchedById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> feedSchedController.getFeedSchedById(1L));
    }

    @Test
    void getFeedSchedsByHorseId_returnsMappedDtos() {
        FeedSched feedSched = ControllerTestSupport.feedSched(1L, "Morning");
        when(feedSchedService.getFeedSchedByHorseId(7L)).thenReturn(List.of(feedSched));

        List<FeedSchedDTO> result = feedSchedController.getFeedSchedsByHorseId(7L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getFeedSchedId());
    }

    @Test
    void updateFeedSched_returnsOk() {
        FeedSchedDTO dto = new FeedSchedDTO();

        var response = feedSchedController.updateFeedSched(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Etetési napló sikeresen frissítve.", response.getBody());
        verify(feedSchedService).updateFeedSched(1L, dto);
    }

    @Test
    void deleteFeedSched_returnsOk() {
        var response = feedSchedController.deleteFeedSched(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Etetési napló sikeresen törölve.", response.getBody());
        verify(feedSchedService).deleteFeedSched(1L);
    }
}
