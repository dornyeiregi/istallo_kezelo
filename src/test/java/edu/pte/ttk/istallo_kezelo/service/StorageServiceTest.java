package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.StorageDTO;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.FeedSchedItem;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.model.StableItem;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.ItemRepository;
import edu.pte.ttk.istallo_kezelo.repository.StableRepository;
import edu.pte.ttk.istallo_kezelo.repository.StorageRepository;
import java.util.List;
import java.util.Optional;
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

    @Mock
    private StorageAlertService storageAlertService;

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

    @Test
    void syncAmountInUseForItem_returnsWhenStorageMissing() {
        when(storageRepository.findByItem_Id(1L)).thenReturn(null);

        storageService.syncAmountInUseForItem(1L);

        verify(storageRepository).findByItem_Id(1L);
    }

    @Test
    void createStorage_throwsWhenAmountStoredNegative() {
        Item item = ServiceTestSupport.item(1L, "Hay");
        StorageDTO dto = new StorageDTO();
        dto.setItemId(1L);
        dto.setAmountStored(-1.0);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> storageService.createStorage(dto));

        assertEquals("A tárolt mennyiség nem lehet negatív.", exception.getMessage());
    }

    @Test
    void updateStorage_throwsWhenAmountStoredNegative() {
        Item item = ServiceTestSupport.item(1L, "Hay");
        Storage storage = ServiceTestSupport.storage(2L, item, 20.0, 0.0);
        StorageDTO dto = new StorageDTO();
        dto.setAmountStored(-5.0);

        when(storageRepository.findById(2L)).thenReturn(Optional.of(storage));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> storageService.updateStorage(2L, dto));

        assertEquals("A tárolt mennyiség nem lehet negatív.", exception.getMessage());
        verifyNoInteractions(storageAlertService);
    }

    @Test
    void updateStorage_notifiesAboutSavedStorage() {
        Item item = ServiceTestSupport.item(1L, "Hay");
        Storage storage = ServiceTestSupport.storage(2L, item, 20.0, 0.0);
        StorageDTO dto = new StorageDTO();
        dto.setAmountStored(12.0);

        when(storageRepository.findById(2L)).thenReturn(Optional.of(storage));
        when(storageRepository.save(storage)).thenReturn(storage);

        Storage result = storageService.updateStorage(2L, dto);

        assertEquals(12.0, result.getAmountStored());
        verify(storageAlertService).notifyLowStock(List.of(storage));
    }

    @Test
    void createAndBasicGetters_delegateToRepository() {
        Item item = ServiceTestSupport.item(1L, "Hay");
        StorageDTO dto = new StorageDTO();
        dto.setItemId(1L);
        dto.setAmountStored(15.0);
        Storage storage = ServiceTestSupport.storage(2L, item, 15.0, 0.0);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(storageRepository.save(org.mockito.ArgumentMatchers.any(Storage.class))).thenReturn(storage);
        when(storageRepository.findAll()).thenReturn(List.of(storage));
        when(storageRepository.findById(2L)).thenReturn(Optional.of(storage));
        when(storageRepository.findByItem_Id(1L)).thenReturn(storage);

        Storage created = storageService.createStorage(dto);

        assertEquals(15.0, created.getAmountStored());
        assertEquals(List.of(storage), storageService.getAllStorages());
        assertEquals(storage, storageService.getStorageById(2L));
        assertEquals(storage, storageService.getStorageByItemId(1L));

        storageService.deleteStorage(2L);

        verify(storageRepository).deleteById(2L);
    }

    @Test
    void syncAllAmountsInUse_handlesFeedAndBeddingItems() {
        Item feed = ServiceTestSupport.item(1L, "Hay");
        Item bedding = ServiceTestSupport.item(2L, "Straw");
        bedding.setItemType(ItemType.BEDDING);
        Storage feedStorage = ServiceTestSupport.storage(3L, feed, 20.0, 0.0);
        Storage beddingStorage = ServiceTestSupport.storage(4L, bedding, 50.0, 0.0);
        FeedSched feedSched = ServiceTestSupport.feedSched(5L, "Morning");
        FeedSchedItem feedLink = ServiceTestSupport.feedSchedItem(6L, feedSched, feed, 2.5);
        Stable stable = ServiceTestSupport.stable(7L, "Main");
        StableItem stableItem = new StableItem();
        stableItem.setItem(bedding);
        stableItem.setUsageKg(6.0);
        stable.getStableItems().add(stableItem);

        when(storageRepository.findAll()).thenReturn(List.of(feedStorage, beddingStorage));
        when(feedSchedItemRepository.findByItem_Id(1L)).thenReturn(List.of(feedLink));
        when(horseFeedSchedRepository.countActiveByFeedSchedId(5L)).thenReturn(2);
        when(stableRepository.findAll()).thenReturn(List.of(stable));

        storageService.syncAllAmountsInUse();

        assertEquals(5.0, feedStorage.getAmountInUse());
        assertEquals(6.0, beddingStorage.getAmountInUse());
        verify(storageRepository).saveAll(List.of(feedStorage, beddingStorage));
    }
}
