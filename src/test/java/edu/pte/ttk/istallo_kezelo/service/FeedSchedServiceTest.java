package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedChangeRequestRepository;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for FeedSchedService behavior.
 */
@ExtendWith(MockitoExtension.class)
class FeedSchedServiceTest {

    @Mock
    private FeedSchedRepository feedSchedRepository;

    @Mock
    private FeedSchedChangeRequestRepository feedSchedChangeRequestRepository;

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private FeedSchedService feedSchedService;

    @Test
    void createFeedSched_withoutLinks_savesFeedSched() {
        FeedSchedDTO dto = new FeedSchedDTO();
        dto.setFeedEvening(true);
        dto.setDescription("Evening ration");
        when(feedSchedRepository.save(any(FeedSched.class))).thenAnswer(invocation -> {
            FeedSched feedSched = invocation.getArgument(0);
            feedSched.setId(7L);
            return feedSched;
        });

        FeedSched result = feedSchedService.createFeedSched(dto);

        assertEquals(7L, result.getId());
        assertEquals(true, result.isFeedEvening());
        assertEquals("Evening ration", result.getDescription());
    }
}
