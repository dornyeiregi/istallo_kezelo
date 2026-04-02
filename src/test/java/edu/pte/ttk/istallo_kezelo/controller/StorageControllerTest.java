package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.StorageDTO;
import edu.pte.ttk.istallo_kezelo.model.Item;
import edu.pte.ttk.istallo_kezelo.model.Storage;
import edu.pte.ttk.istallo_kezelo.service.StorageConsumptionService;
import edu.pte.ttk.istallo_kezelo.service.StorageService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

/**
 * Test class for StorageController behavior.
 */
@ExtendWith(MockitoExtension.class)
class StorageControllerTest {

    @Mock
    private StorageService storageService;

    @Mock
    private StorageConsumptionService storageConsumptionService;

    @InjectMocks
    private StorageController storageController;

    @Test
    void addStorage_returnsMappedDto() {
        Item item = ControllerTestSupport.item(2L, "Hay");
        Storage storage = ControllerTestSupport.storage(1L, item, 20.0, 5.0);
        StorageDTO dto = new StorageDTO(null, null, 20.0, 2L, null, null, null, null);
        when(storageService.createStorage(dto)).thenReturn(storage);

        StorageDTO result = storageController.addStorage(dto);

        assertEquals(1L, result.getStorageId());
        assertEquals(2L, result.getItemId());
    }

    @Test
    void getAllStorages_returnsMappedDtos() {
        when(storageService.getAllStorages()).thenReturn(List.of(ControllerTestSupport.storage(1L, ControllerTestSupport.item(2L, "Hay"), 20.0, 5.0)));

        List<StorageDTO> result = storageController.getAllStorages();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getStorageId());
        verify(storageConsumptionService).reduceConsumablesDaily();
    }

    @Test
    void getStorageAlerts_filtersWarnings() {
        Item item = ControllerTestSupport.item(2L, "Hay");
        Storage storage = ControllerTestSupport.storage(1L, item, 10.0, 1.0);
        when(storageService.getAllStorages()).thenReturn(List.of(storage));

        List<StorageDTO> result = storageController.getStorageAlerts();

        assertEquals(1, result.size());
        assertEquals("YELLOW", result.get(0).getWarningLevel());
        verify(storageConsumptionService).reduceConsumablesDaily();
    }

    @Test
    void updateStorage_returnsMappedDto() {
        Storage updated = ControllerTestSupport.storage(1L, ControllerTestSupport.item(2L, "Hay"), 25.0, 5.0);
        StorageDTO dto = new StorageDTO();
        when(storageService.updateStorage(1L, dto)).thenReturn(updated);

        var response = storageController.updateStorage(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(25.0, response.getBody().getAmountStored());
    }

    @Test
    void deleteStorage_returnsOk() {
        var response = storageController.deleteStorage(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tároló sikeresen törölve.", response.getBody());
        verify(storageService).deleteStorage(1L);
    }

    @Test
    void syncStorages_returnsOk() {
        var response = storageController.syncStorages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tárolók sikeresen szinkronizálva.", response.getBody());
        verify(storageService).syncAllAmountsInUse();
    }
}
