package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for FeedSchedItemService behavior.
 */
@ExtendWith(MockitoExtension.class)
class FeedSchedItemServiceTest {

    @Mock
    private FeedSchedItemRepository feedSchedItemRepository;

    @Mock
    private FeedSchedRepository feedSchedRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private FeedSchedItemService feedSchedItemService;

    @Test
    void addItemToFeedSched_savesLinkAndSyncsStorage() {
        FeedSched feedSched = ServiceTestSupport.feedSched(1L, "Morning");
        Item item = ServiceTestSupport.item(2L, "Hay");
        FeedSchedItem savedLink = ServiceTestSupport.feedSchedItem(3L, feedSched, item, 2.5);

        when(feedSchedRepository.findById(1L)).thenReturn(Optional.of(feedSched));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(feedSchedItemRepository.existsByFeedSchedAndItem(feedSched, item)).thenReturn(false);
        when(feedSchedItemRepository.save(org.mockito.ArgumentMatchers.any(FeedSchedItem.class))).thenReturn(savedLink);

        FeedSchedItem result = feedSchedItemService.addItemToFeedSched(1L, 2L, 2.5);

        assertEquals(3L, result.getId());
        assertEquals(2.5, result.getAmount());
        verify(storageService).syncAmountInUseForItem(2L);
    }
}
