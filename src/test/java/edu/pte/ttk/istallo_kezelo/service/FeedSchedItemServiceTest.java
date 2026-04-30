package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void addItemToFeedSched_throwsWhenDuplicateLinkExists() {
        FeedSched feedSched = ServiceTestSupport.feedSched(1L, "Morning");
        Item item = ServiceTestSupport.item(2L, "Hay");

        when(feedSchedRepository.findById(1L)).thenReturn(Optional.of(feedSched));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(feedSchedItemRepository.existsByFeedSchedAndItem(feedSched, item)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> feedSchedItemService.addItemToFeedSched(1L, 2L, 2.5));

        assertEquals("Tétel már hozzá van rendelve az etetési naplóhoz.", exception.getMessage());
        verifyNoInteractions(storageService);
    }

    @Test
    void addItemToFeedSched_throwsWhenAmountMissing() {
        FeedSched feedSched = ServiceTestSupport.feedSched(1L, "Morning");
        Item item = ServiceTestSupport.item(2L, "Hay");

        when(feedSchedRepository.findById(1L)).thenReturn(Optional.of(feedSched));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(feedSchedItemRepository.existsByFeedSchedAndItem(feedSched, item)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> feedSchedItemService.addItemToFeedSched(1L, 2L, null));

        assertEquals("Mennyiség kötelező.", exception.getMessage());
        verifyNoInteractions(storageService);
    }

    @Test
    void getters_delegateToRepository() {
        FeedSched feedSched = ServiceTestSupport.feedSched(1L, "Morning");
        Item item = ServiceTestSupport.item(2L, "Hay");
        FeedSchedItem link = ServiceTestSupport.feedSchedItem(3L, feedSched, item, 2.5);

        when(feedSchedItemRepository.findAll()).thenReturn(List.of(link));
        when(feedSchedItemRepository.findByFeedSched_Id(1L)).thenReturn(List.of(link));
        when(feedSchedItemRepository.findByItem_Id(2L)).thenReturn(List.of(link));
        when(feedSchedItemRepository.findById(3L)).thenReturn(Optional.of(link));

        assertEquals(List.of(link), feedSchedItemService.getAllFeedSchedItems());
        assertEquals(List.of(link), feedSchedItemService.getItemsForFeedSched(1L));
        assertEquals(List.of(link), feedSchedItemService.getFeedSchedsForItem(2L));
        assertSame(link, feedSchedItemService.getFeedSchedItemById(3L));
    }

    @Test
    void removeItemFromFeedSched_deletesLinkAndSyncsStorage() {
        feedSchedItemService.removeItemFromFeedSched(1L, 2L);

        verify(feedSchedItemRepository).deleteByFeedSched_IdAndItem_Id(1L, 2L);
        verify(storageService).syncAmountInUseForItem(2L);
    }
}
