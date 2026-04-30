package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.FeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemAmountDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedChangeRequest;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.service.FeedSchedService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

/**
 * Test class for FeedSchedController behavior.
 */
@ExtendWith(MockitoExtension.class)
class FeedSchedControllerTest {

    @Mock
    private FeedSchedService feedSchedService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FeedSchedController feedSchedController;

    @Test
    void createFeedSched_returnsMappedDto() {
        FeedSched feedSched = ControllerTestSupport.feedSched(1L, "Morning");
        Item item = ControllerTestSupport.item(5L, "Hay");
        feedSched.getFeedSchedItems().add(ControllerTestSupport.feedSchedItem(feedSched, item, 3.5));

        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setDescription("Morning");
        when(authentication.getAuthorities()).thenReturn(List.of());
        when(feedSchedService.createFeedSched(dto)).thenReturn(feedSched);

        ResponseEntity<String> response = feedSchedController.createFeedSched(dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Etetési napló sikeresen létrehozva.", response.getBody());
        verify(feedSchedService).createFeedSched(dto);
    }

    @Test
    void createFeedSched_asOwner_returnsAccepted() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setDescription("Morning");

        ResponseEntity<String> response = feedSchedController.createFeedSched(dto, auth);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Kérés elküldve. Jóváhagyás után lép életbe.", response.getBody());
        verify(feedSchedService).createFeedSchedRequest(dto, auth);
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
        when(feedSchedService.updateFeedSched(1L, dto, authentication)).thenReturn(true);

        var response = feedSchedController.updateFeedSched(1L, dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Etetési napló sikeresen frissítve.", response.getBody());
        verify(feedSchedService).updateFeedSched(1L, dto, authentication);
    }

    @Test
    void updateFeedSched_returnsAcceptedWhenRequestCreated() {
        FeedSchedDTO dto = new FeedSchedDTO();
        when(feedSchedService.updateFeedSched(1L, dto, authentication)).thenReturn(false);

        var response = feedSchedController.updateFeedSched(1L, dto, authentication);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Kérés elküldve. Jóváhagyás után lép életbe.", response.getBody());
        verify(feedSchedService).updateFeedSched(1L, dto, authentication);
    }

    @Test
    void deleteFeedSched_returnsOk() {
        var response = feedSchedController.deleteFeedSched(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Etetési napló sikeresen törölve.", response.getBody());
        verify(feedSchedService).deleteFeedSched(1L);
    }

    @Test
    void getChangeRequests_returnsMappedDtos() {
        FeedSched feedSched = ControllerTestSupport.feedSched(1L, "Morning");
        var requester = ControllerTestSupport.user(2L, "anna", "Nagy", "Anna");
        FeedSchedChangeRequest request = new FeedSchedChangeRequest();
        request.setId(9L);
        request.setFeedSched(feedSched);
        request.setRequestedBy(requester);
        request.setRequestedAt(LocalDateTime.now());
        request.setRequestedHorseIds("1,2");
        request.setRequestedItemIds("5");
        request.setRequestedItemAmounts("5:2.5");

        when(feedSchedService.getAllChangeRequests()).thenReturn(List.of(request));
        when(feedSchedService.parseIds("1,2")).thenReturn(List.of(1L, 2L));
        when(feedSchedService.parseIds("5")).thenReturn(List.of(5L));
        when(feedSchedService.parseItemAmounts("5:2.5"))
            .thenReturn(List.of(new FeedSchedItemAmountDTO(5L, 2.5)));

        var result = feedSchedController.getChangeRequests();

        assertEquals(1, result.size());
        assertEquals(9L, result.get(0).getId());
        assertEquals(List.of(1L, 2L), result.get(0).getHorseIds());
    }

    @Test
    void getMyChangeRequests_returnsMappedDtos() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        FeedSched feedSched = ControllerTestSupport.feedSched(1L, "Morning");
        var requester = ControllerTestSupport.user(2L, "anna", "Nagy", "Anna");
        FeedSchedChangeRequest request = new FeedSchedChangeRequest();
        request.setId(10L);
        request.setFeedSched(feedSched);
        request.setRequestedBy(requester);
        request.setRequestedAt(LocalDateTime.now());

        when(feedSchedService.getMyChangeRequests(auth)).thenReturn(List.of(request));
        when(feedSchedService.parseIds(null)).thenReturn(List.of());
        when(feedSchedService.parseItemAmounts(null)).thenReturn(List.of());

        var result = feedSchedController.getMyChangeRequests(auth);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void approveChangeRequest_returnsOk() {
        FeedSched updatedFeedSched = ControllerTestSupport.feedSched(7L, "Updated");
        when(feedSchedService.approveChangeRequest(7L)).thenReturn(updatedFeedSched);

        var response = feedSchedController.approveChangeRequest(7L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Kérés jóváhagyva.", response.getBody());
        verify(feedSchedService).approveChangeRequest(7L);
    }

    @Test
    void rejectChangeRequest_returnsOk() {
        doNothing().when(feedSchedService).rejectChangeRequest(7L);

        var response = feedSchedController.rejectChangeRequest(7L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Kérés elutasítva.", response.getBody());
        verify(feedSchedService).rejectChangeRequest(7L);
    }
}
