package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.ShotDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.service.ShotService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class ShotControllerTest {

    @Mock
    private ShotService shotService;

    @InjectMocks
    private ShotController shotController;

    @Test
    void createShot_returnsMappedDto() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        ShotDTO dto = new ShotDTO(null, "Tetano", 6, "MONTHS", LocalDate.of(2026, 1, 15), List.of(1L));
        Shot shot = ControllerTestSupport.shot(2L, "Tetano");
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(3L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(4L, "A"));
        HorseShot link = ControllerTestSupport.horseShot(horse, shot);
        shot.getHorses_treated().add(link);

        when(shotService.saveShot(any(Shot.class), eq(List.of(1L)), eq(auth))).thenReturn(shot);

        ShotDTO result = shotController.createShot(dto, auth);

        assertEquals(2L, result.getShotId());
        assertEquals(List.of(1L), result.getHorseIds());
        ArgumentCaptor<Shot> captor = ArgumentCaptor.forClass(Shot.class);
        verify(shotService).saveShot(captor.capture(), eq(List.of(1L)), eq(auth));
        assertEquals("Tetano", captor.getValue().getShotName());
    }

    @Test
    void getAllShots_returnsMappedDtos() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        when(shotService.getAllShots(auth)).thenReturn(List.of(ControllerTestSupport.shot(2L, "Tetano")));

        List<ShotDTO> result = shotController.getAllShots(auth);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getShotId());
    }

    @Test
    void getShotById_returnsMappedDto() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        Shot shot = ControllerTestSupport.shot(2L, "Tetano");
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(3L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(4L, "A"));
        shot.getHorses_treated().add(ControllerTestSupport.horseShot(horse, shot));
        when(shotService.getShotById(2L, auth)).thenReturn(shot);

        ShotDTO result = shotController.getShotById(2L, auth);

        assertEquals("Tetano", result.getShotName());
    }

    @Test
    void getAllShotsOfHorseById_returnsMappedDtos() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        Shot shot = ControllerTestSupport.shot(2L, "Tetano");
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(3L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(4L, "A"));
        shot.getHorses_treated().add(ControllerTestSupport.horseShot(horse, shot));
        when(shotService.getShotsByHorseId(1L, auth)).thenReturn(List.of(shot));

        List<ShotDTO> result = shotController.getAllShotsOfHorseById(1L, auth);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getShotId());
    }

    @Test
    void updateShot_returnsOk() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        ShotDTO dto = new ShotDTO();

        var response = shotController.updateShot(2L, dto, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Oltás sikeresen frissítve.", response.getBody());
        verify(shotService).updateShot(2L, dto, auth);
    }

    @Test
    void deleteShot_returnsOk() {
        Authentication auth = ControllerTestSupport.auth("admin", "ROLE_ADMIN");

        var response = shotController.deleteShot(2L, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Oltás sikeresen törölve.", response.getBody());
        verify(shotService).deleteShotById(2L, auth);
    }
}
