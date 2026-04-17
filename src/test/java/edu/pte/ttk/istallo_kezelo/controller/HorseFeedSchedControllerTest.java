package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.HorseFeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.service.HorseFeedSchedService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for HorseFeedSchedController behavior.
 */
@ExtendWith(MockitoExtension.class)
class HorseFeedSchedControllerTest {

    @Mock
    private HorseFeedSchedService horseFeedSchedService;

    @InjectMocks
    private HorseFeedSchedController horseFeedSchedController;

    @Test
    void addHorseToFeedSched_returnsMappedDto() {
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(2L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(3L, "A"));
        FeedSched feedSched = ControllerTestSupport.feedSched(4L, "Morning");
        HorseFeedSched link = ControllerTestSupport.horseFeedSched(horse, feedSched);
        HorseFeedSchedDTO dto = new HorseFeedSchedDTO(1L, 4L, null, null, null);

        when(horseFeedSchedService.addHorseToFeedSched(4L, 1L)).thenReturn(link);

        HorseFeedSchedDTO result = horseFeedSchedController.addHorseToFeedSched(dto);

        assertEquals(1L, result.getHorseId());
        assertEquals(4L, result.getFeedSchedId());
    }

    @Test
    void getAllHorseFeedScheds_returnsMappedDtos() {
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(2L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(3L, "A"));
        FeedSched feedSched = ControllerTestSupport.feedSched(4L, "Morning");
        HorseFeedSched link = ControllerTestSupport.horseFeedSched(horse, feedSched);
        when(horseFeedSchedService.getAllHorseFeedScheds()).thenReturn(List.of(link));

        List<HorseFeedSchedDTO> result = horseFeedSchedController.getAllHorseFeedScheds();

        assertEquals(1, result.size());
        assertEquals("Morning", result.get(0).getFeedDescription());
    }

    @Test
    void getHorseFeedSchedById_returnsMappedDto() {
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(2L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(3L, "A"));
        FeedSched feedSched = ControllerTestSupport.feedSched(4L, "Morning");
        HorseFeedSched link = ControllerTestSupport.horseFeedSched(horse, feedSched);
        when(horseFeedSchedService.getHorseFeedSchedById(9L)).thenReturn(link);

        HorseFeedSchedDTO result = horseFeedSchedController.getHorseFeedSchedById(9L);

        assertEquals(4L, result.getFeedSchedId());
    }

    @Test
    void getFeedSchedsForHorse_returnsMappedDtos() {
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(2L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(3L, "A"));
        FeedSched feedSched = ControllerTestSupport.feedSched(4L, "Morning");
        HorseFeedSched link = ControllerTestSupport.horseFeedSched(horse, feedSched);
        when(horseFeedSchedService.getFeedSchedsForHorse(1L)).thenReturn(List.of(link));

        List<HorseFeedSchedDTO> result = horseFeedSchedController.getFeedSchedsForHorse(1L);

        assertEquals(1, result.size());
        assertEquals("Csillag", result.get(0).getHorseName());
    }

    @Test
    void removeAllFeedSchedsForHorse_returnsOk() {
        var response = horseFeedSchedController.removeAllFeedSchedsForHorse(5L);

        assertEquals("Etetési ütemtervek eltávolítva.", response.getBody());
        verify(horseFeedSchedService).removeAllFeedSchedsForHorse(5L);
    }
}
