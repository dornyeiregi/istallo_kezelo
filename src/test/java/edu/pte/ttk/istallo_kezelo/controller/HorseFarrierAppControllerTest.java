package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.HorseFarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.service.HorseFarrierAppService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

/**
 * Test class for HorseFarrierAppController behavior.
 */
@ExtendWith(MockitoExtension.class)
class HorseFarrierAppControllerTest {

    @Mock
    private HorseFarrierAppService horseFarrierAppService;

    @InjectMocks
    private HorseFarrierAppController horseFarrierAppController;

    @Test
    void addHorseToFarrierApp_returnsMappedDto() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        Stable stable = ControllerTestSupport.stable(2L, "A");
        Horse horse = ControllerTestSupport.horse(3L, "Csillag", owner, stable);
        FarrierApp farrierApp = ControllerTestSupport.farrierApp(4L, "Kovacs Bela");
        HorseFarrierApp link = ControllerTestSupport.horseFarrierApp(horse, farrierApp);
        HorseFarrierAppDTO dto = new HorseFarrierAppDTO(3L, 4L, null, null, null, null, null);

        when(horseFarrierAppService.addHorseToFarrierApp(4L, 3L, auth)).thenReturn(link);

        HorseFarrierAppDTO result = horseFarrierAppController.addHorseToFarrierApp(dto, auth);

        assertEquals(3L, result.getHorseId());
        assertEquals(4L, result.getFarrierAppId());
    }

    @Test
    void getAllHorseFarrierApps_returnsMappedDtos() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        Horse horse = ControllerTestSupport.horse(3L, "Csillag", owner, ControllerTestSupport.stable(2L, "A"));
        FarrierApp farrierApp = ControllerTestSupport.farrierApp(4L, "Kovacs Bela");
        HorseFarrierApp link = ControllerTestSupport.horseFarrierApp(horse, farrierApp);

        when(horseFarrierAppService.getAllHorseFarrierApps(auth)).thenReturn(List.of(link));

        List<HorseFarrierAppDTO> result = horseFarrierAppController.getAllHorseFarrierApps(auth);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getHorseId());
    }

    @Test
    void getHorseFarrierAppById_returnsMappedDto() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        Horse horse = ControllerTestSupport.horse(3L, "Csillag", owner, ControllerTestSupport.stable(2L, "A"));
        FarrierApp farrierApp = ControllerTestSupport.farrierApp(4L, "Kovacs Bela");
        HorseFarrierApp link = ControllerTestSupport.horseFarrierApp(horse, farrierApp);

        when(horseFarrierAppService.getHorseFarrierAppById(9L, auth)).thenReturn(link);

        HorseFarrierAppDTO result = horseFarrierAppController.getHorseFarrierAppById(9L, auth);

        assertEquals(4L, result.getFarrierAppId());
    }

    @Test
    void getFarrierAppsForHorse_returnsMappedDtos() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        Horse horse = ControllerTestSupport.horse(3L, "Csillag", owner, ControllerTestSupport.stable(2L, "A"));
        FarrierApp farrierApp = ControllerTestSupport.farrierApp(4L, "Kovacs Bela");
        HorseFarrierApp link = ControllerTestSupport.horseFarrierApp(horse, farrierApp);

        when(horseFarrierAppService.getFarrierAppsForHorse(3L, auth)).thenReturn(List.of(link));

        List<HorseFarrierAppDTO> result = horseFarrierAppController.getFarrierAppsForHorse(3L, auth);

        assertEquals(1, result.size());
        assertEquals("Csillag", result.get(0).getHorseName());
    }
}
