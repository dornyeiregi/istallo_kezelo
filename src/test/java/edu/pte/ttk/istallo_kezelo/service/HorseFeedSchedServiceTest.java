package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for HorseFeedSchedService behavior.
 */
@ExtendWith(MockitoExtension.class)
class HorseFeedSchedServiceTest {

    @Mock
    private FeedSchedRepository feedSchedRepository;

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private HorseFeedSchedRepository horseFeedSchedRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private HorseFeedSchedService horseFeedSchedService;

    @Test
    void addHorseToFeedSched_savesLinkAndSyncsStorage() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        FeedSched feedSched = ServiceTestSupport.feedSched(4L, "Morning");
        Item item = ServiceTestSupport.item(5L, "Hay");
        FeedSchedItem itemLink = ServiceTestSupport.feedSchedItem(6L, feedSched, item, 1.5);
        feedSched.getFeedSchedItems().add(itemLink);
        HorseFeedSched savedLink = ServiceTestSupport.horseFeedSched(7L, horse, feedSched);

        when(feedSchedRepository.findById(4L)).thenReturn(Optional.of(feedSched));
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(horseFeedSchedRepository.existsByFeedSchedAndHorse(feedSched, horse)).thenReturn(false);
        when(horseFeedSchedRepository.save(org.mockito.ArgumentMatchers.any(HorseFeedSched.class))).thenReturn(savedLink);

        HorseFeedSched result = horseFeedSchedService.addHorseToFeedSched(4L, 2L);

        assertEquals(7L, result.getId());
        verify(storageService).syncAmountInUseForItem(5L);
    }
}
