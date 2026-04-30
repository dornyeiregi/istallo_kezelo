package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.StableDTO;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.service.StableService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

/**
 * Test class for StableController behavior.
 */
@ExtendWith(MockitoExtension.class)
class StableControllerTest {

    @Mock
    private StableService stableService;

    @InjectMocks
    private StableController stableController;

    @Test
    void createStable_returnsMappedDto() {
        when(stableService.saveStable(any(Stable.class))).thenReturn(ControllerTestSupport.stable(1L, "A"));

        StableDTO result = stableController.createStable(new StableDTO(null, "A", null, null, List.of()));

        assertEquals(1L, result.getStableId());
        ArgumentCaptor<Stable> captor = ArgumentCaptor.forClass(Stable.class);
        verify(stableService).saveStable(captor.capture());
        assertEquals("A", captor.getValue().getStableName());
    }

    @Test
    void getAllStables_returnsMappedDtos() {
        when(stableService.getAllStables()).thenReturn(List.of(ControllerTestSupport.stable(1L, "A")));

        List<StableDTO> result = stableController.getAllStables();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getStableId());
    }

    @Test
    void updateStablePartially_returnsUpdatedStable() {
        Stable stable = ControllerTestSupport.stable(1L, "A");
        when(stableService.getStableById(1L)).thenReturn(Optional.of(stable));
        when(stableService.saveStable(stable)).thenReturn(stable);

        StableDTO result = stableController.updateStablePartially(1L, new StableDTO(null, "B", null, null, null));

        assertEquals("B", result.getStableName());
    }

    @Test
    void updateStablePartially_throwsWhenStableMissing() {
        when(stableService.getStableById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> stableController.updateStablePartially(1L, new StableDTO()));

        assertEquals("Istálló nem található.", exception.getMessage());
    }

    @Test
    void deleteStable_returnsOk() {
        var response = stableController.deleteStable(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Istálló sikeresen törölve.", response.getBody());
        verify(stableService).deleteStableById(1L);
    }
}
