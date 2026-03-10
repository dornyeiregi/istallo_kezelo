package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.FeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.enums.FeedTime;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedSchedServiceTest {

    @Mock
    private FeedSchedRepository feedSchedRepository;

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private FeedSchedService feedSchedService;

    @Test
    void createFeedSched_withoutLinks_savesFeedSched() {
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setFeedTime(FeedTime.EVENING);
        dto.setDescription("Evening ration");
        when(feedSchedRepository.save(any(FeedSched.class))).thenAnswer(invocation -> {
            FeedSched feedSched = invocation.getArgument(0);
            feedSched.setId(7L);
            return feedSched;
        });

        FeedSched result = feedSchedService.createFeedSched(dto);

        assertEquals(7L, result.getId());
        assertEquals(FeedTime.EVENING, result.getFeedTime());
        assertEquals("Evening ration", result.getDescription());
    }
}
