package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.StableRepository;
import edu.pte.ttk.istallo_kezelo.repository.StorageRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private StorageRepository storageRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private FeedSchedItemRepository feedSchedItemRepository;

    @Mock
    private HorseFeedSchedRepository horseFeedSchedRepository;

    @Mock
    private StableRepository stableRepository;

    @InjectMocks
    private StorageService storageService;

    @Test
    void syncAmountInUseForItem_calculatesAndSavesTotal() {
        Item item = ServiceTestSupport.item(1L, "Hay");
        Storage storage = ServiceTestSupport.storage(2L, item, 20.0, 0.0);
        FeedSched firstFeedSched = ServiceTestSupport.feedSched(3L, "Morning");
        FeedSched secondFeedSched = ServiceTestSupport.feedSched(4L, "Evening");
        FeedSchedItem firstLink = ServiceTestSupport.feedSchedItem(5L, firstFeedSched, item, 1.5);
        FeedSchedItem secondLink = ServiceTestSupport.feedSchedItem(6L, secondFeedSched, item, 2.0);

        when(storageRepository.findByItem_Id(1L)).thenReturn(storage);
        when(feedSchedItemRepository.findByItem_Id(1L)).thenReturn(List.of(firstLink, secondLink));
        when(horseFeedSchedRepository.countActiveByFeedSchedId(3L)).thenReturn(2);
        when(horseFeedSchedRepository.countActiveByFeedSchedId(4L)).thenReturn(3);
        when(storageRepository.save(storage)).thenReturn(storage);

        storageService.syncAmountInUseForItem(1L);

        assertEquals(9.0, storage.getAmountInUse());
    }
}
