package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.HorseShotDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.service.HorseShotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class HorseShotControllerTest {

    @Mock
    private HorseShotService horseShotService;

    @InjectMocks
    private HorseShotController horseShotController;

    @Test
    void addShotToHorse_returnsMappedDto() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(2L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(3L, "A"));
        Shot shot = ControllerTestSupport.shot(4L, "Tetano");
        HorseShot link = ControllerTestSupport.horseShot(horse, shot);
        HorseShotDTO dto = new HorseShotDTO(1L, 4L, null, null, null, null, null);

        when(horseShotService.addShotToHorse(4L, 1L, auth)).thenReturn(link);

        HorseShotDTO result = horseShotController.addShotToHorse(dto, auth);

        assertEquals(1L, result.getHorseId());
        assertEquals(4L, result.getShotId());
        assertEquals("Tetano", result.getShotName());
    }
}
