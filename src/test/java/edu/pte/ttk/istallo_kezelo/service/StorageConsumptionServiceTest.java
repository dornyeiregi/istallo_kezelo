package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemCategory;
import edu.pte.ttk.istallo_kezelo.model.enums.ItemType;
import edu.pte.ttk.istallo_kezelo.repository.StorageRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for StorageConsumptionService behavior.
 */
@ExtendWith(MockitoExtension.class)
class StorageConsumptionServiceTest {

    @Mock
    private StorageRepository storageRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private StorageConsumptionService storageConsumptionService;

    @Test
    void reduceConsumablesDaily_updatesAmountsAndDate() {
        Item item = ServiceTestSupport.item(1L, "Hay");
        Storage storage = ServiceTestSupport.storage(2L, item, 10.0, 3.0);
        storage.setLastReducedDate(LocalDate.now().minusDays(2));

        when(storageRepository.findByItem_ItemCategory(ItemCategory.CONSUMABLE)).thenReturn(List.of(storage));
        when(storageRepository.findByItem_ItemType(ItemType.BEDDING)).thenReturn(List.of());

        storageConsumptionService.reduceConsumablesDaily();

        verify(storageService).syncAllAmountsInUse();
        assertEquals(4.0, storage.getAmountStored());
        assertNotNull(storage.getLastReducedDate());
    }
}
