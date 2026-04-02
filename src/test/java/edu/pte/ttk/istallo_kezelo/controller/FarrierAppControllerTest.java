package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.FarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.service.FarrierAppService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

/**
 * Test class for FarrierAppController behavior.
 */
@ExtendWith(MockitoExtension.class)
class FarrierAppControllerTest {

    @Mock
    private FarrierAppService farrierAppService;

    @InjectMocks
    private FarrierAppController farrierAppController;

    @Test
    void createFarrierApp_returnsMappedDto() {
        User owner = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        Stable stable = ControllerTestSupport.stable(2L, "A");
        Horse horse = ControllerTestSupport.horse(3L, "Csillag", owner, stable);
        FarrierApp farrierApp = ControllerTestSupport.farrierApp(4L, "Kovacs Bela");
        HorseFarrierApp link = ControllerTestSupport.horseFarrierApp(horse, farrierApp);
        farrierApp.getHorses_done().add(link);

        FarrierAppDTO dto = new FarrierAppDTO(
                null,
                "Kovacs Bela",
                "555",
                LocalDate.of(2026, 3, 1),
                6,
                "HONAP",
                true,
                List.of(3L),
                null
        );
        when(farrierAppService.createFarrierApp(dto, ControllerTestSupport.auth("anna", "ROLE_OWNER"))).thenReturn(farrierApp);

        FarrierAppDTO result = farrierAppController.createFarrierApp(dto, ControllerTestSupport.auth("anna", "ROLE_OWNER")).getBody();

        assertEquals(4L, result.getFarrierAppId());
        assertEquals(List.of(3L), result.getHorseIds());
    }

    @Test
    void getAllFarrierApps_returnsMappedDtos() {
        User owner = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        Stable stable = ControllerTestSupport.stable(2L, "A");
        Horse horse = ControllerTestSupport.horse(3L, "Csillag", owner, stable);
        FarrierApp farrierApp = ControllerTestSupport.farrierApp(4L, "Kovacs Bela");
        farrierApp.getHorses_done().add(ControllerTestSupport.horseFarrierApp(horse, farrierApp));

        when(farrierAppService.getAllFarrierApps(ControllerTestSupport.auth("anna", "ROLE_OWNER"))).thenReturn(List.of(farrierApp));

        List<FarrierAppDTO> result = farrierAppController.getAllFarrierApps(ControllerTestSupport.auth("anna", "ROLE_OWNER"));

        assertEquals(1, result.size());
        assertEquals(4L, result.get(0).getFarrierAppId());
    }

    @Test
    void getFarrierAppById_throwsWhenServiceReturnsNull() {
        when(farrierAppService.getFarrierAppById(1L, ControllerTestSupport.auth("anna", "ROLE_OWNER"))).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
            farrierAppController.getFarrierAppById(1L, ControllerTestSupport.auth("anna", "ROLE_OWNER"))
        );
    }

    @Test
    void getFarrierAppsByHorseId_returnsMappedDtos() {
        User owner = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        Stable stable = ControllerTestSupport.stable(2L, "A");
        Horse horse = ControllerTestSupport.horse(3L, "Csillag", owner, stable);
        FarrierApp farrierApp = ControllerTestSupport.farrierApp(4L, "Kovacs Bela");
        farrierApp.getHorses_done().add(ControllerTestSupport.horseFarrierApp(horse, farrierApp));

        when(farrierAppService.getFarrierAppByHorseId(3L, ControllerTestSupport.auth("anna", "ROLE_OWNER"))).thenReturn(List.of(farrierApp));

        List<FarrierAppDTO> result = farrierAppController.getFarrierAppsByHorseId(3L, ControllerTestSupport.auth("anna", "ROLE_OWNER"));

        assertEquals(1, result.size());
        assertEquals(4L, result.get(0).getFarrierAppId());
    }

    @Test
    void updateFarrierApp_returnsOk() {
        FarrierAppDTO dto = new FarrierAppDTO();

        var response = farrierAppController.updateFarrierApp(1L, dto, ControllerTestSupport.auth("anna", "ROLE_OWNER"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Patkolás sikeresen frissítve.", response.getBody());
        verify(farrierAppService).updateFarrierApp(1L, dto, ControllerTestSupport.auth("anna", "ROLE_OWNER"));
    }

    @Test
    void deleteFarrierApp_returnsOk() {
        var response = farrierAppController.deleteFarrierApp(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Patkolás sikeresen törölve.", response.getBody());
        verify(farrierAppService).deleteFarrierApp(1L);
    }
}
