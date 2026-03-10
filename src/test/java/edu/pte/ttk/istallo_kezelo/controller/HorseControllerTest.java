package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.HorseDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.Sex;
import edu.pte.ttk.istallo_kezelo.service.HorseService;
import edu.pte.ttk.istallo_kezelo.service.StableService;
import edu.pte.ttk.istallo_kezelo.service.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class HorseControllerTest {

    @Mock
    private HorseService horseService;

    @Mock
    private UserService userService;

    @Mock
    private StableService stableService;

    @InjectMocks
    private HorseController horseController;

    @Test
    void createHorse_returnsHorseDto() {
        Authentication auth = ControllerTestSupport.auth("admin", "ROLE_ADMIN");
        User owner = ControllerTestSupport.user(10L, "tulaj1", "Nagy", "Anna");
        Stable stable = ControllerTestSupport.stable(20L, "Foistallo");

        HorseDTO dto = new HorseDTO();
        dto.setHorseName("Csillag");
        dto.setDob(LocalDate.of(2018, 5, 20));
        dto.setSex(Sex.F);
        dto.setOwnerId(owner.getId());
        dto.setStableName(stable.getStableName());
        dto.setMicrochipNum("HU-123");
        dto.setPassportNum("HU-999");
        dto.setAdditional("Nyugodt");

        when(userService.getUserById(owner.getId(), auth)).thenReturn(Optional.of(owner));
        when(stableService.getStableByName(stable.getStableName())).thenReturn(stable);
        when(horseService.saveHorse(any(Horse.class))).thenAnswer(invocation -> {
            Horse horse = invocation.getArgument(0);
            ReflectionTestUtils.setField(horse, "id", 55L);
            return horse;
        });

        HorseDTO result = horseController.createHorse(dto, auth);

        assertEquals(55L, result.getId());
        assertEquals("Csillag", result.getHorseName());
        assertEquals("Nagy Anna", result.getOwnerName());
        assertEquals("Foistallo", result.getStableName());

        ArgumentCaptor<Horse> captor = ArgumentCaptor.forClass(Horse.class);
        verify(horseService).saveHorse(captor.capture());
        assertEquals("HU-123", captor.getValue().getMicrochipNum());
    }

    @Test
    void getAllHorses_returnsMappedDtos() {
        Authentication auth = ControllerTestSupport.auth("employee", "ROLE_EMPLOYEE");
        Horse horse = ControllerTestSupport.horse(101L, "Szel", ControllerTestSupport.user(30L, "tulaj1", "Kovacs", "Eva"), ControllerTestSupport.stable(40L, "Foistallo"));
        when(horseService.getAllHorses(auth)).thenReturn(List.of(horse));

        List<HorseDTO> result = horseController.getAllHorses(auth);

        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).getId());
    }

    @Test
    void getHorseById_returnsHorseDtoForOwner() {
        Authentication auth = ControllerTestSupport.auth("tulaj1", "ROLE_OWNER");
        Horse horse = ControllerTestSupport.horse(101L, "Szel", ControllerTestSupport.user(30L, "tulaj1", "Kovacs", "Eva"), ControllerTestSupport.stable(40L, "Foistallo"));
        when(horseService.getHorseById(101L, auth)).thenReturn(Optional.of(horse));

        HorseDTO result = horseController.getHorseById(101L, auth);

        assertEquals(101L, result.getId());
        assertEquals("Szel", result.getHorseName());
        assertEquals("Kovacs Eva", result.getOwnerName());
    }

    @Test
    void getHorseById_ownerMismatch_throwsRuntimeException() {
        Authentication auth = ControllerTestSupport.auth("tulaj2", "ROLE_OWNER");
        Horse horse = ControllerTestSupport.horse(202L, "Villam", ControllerTestSupport.user(30L, "tulaj1", "Kiss", "Adam"), ControllerTestSupport.stable(40L, "Teszt istallo"));
        when(horseService.getHorseById(202L, auth)).thenReturn(Optional.of(horse));

        assertThrows(RuntimeException.class, () -> horseController.getHorseById(202L, auth));
    }

    @Test
    void updateHorsePartially_returnsUpdatedHorse() {
        Authentication auth = ControllerTestSupport.auth("admin", "ROLE_ADMIN");
        User owner = ControllerTestSupport.user(10L, "tulaj1", "Nagy", "Anna");
        Stable stable = ControllerTestSupport.stable(20L, "Foistallo");
        Horse existingHorse = ControllerTestSupport.horse(55L, "Csillag", owner, stable);
        User newOwner = ControllerTestSupport.user(11L, "tulaj2", "Kiss", "Eva");
        Stable newStable = ControllerTestSupport.stable(21L, "Masik");
        HorseDTO dto = new HorseDTO();
        dto.setHorseName("Uj nev");
        dto.setOwnerId(11L);
        dto.setStableId(21L);

        when(horseService.getHorseById(55L, auth)).thenReturn(Optional.of(existingHorse));
        when(userService.getUserById(11L, auth)).thenReturn(Optional.of(newOwner));
        when(stableService.getStableById(21L)).thenReturn(Optional.of(newStable));
        when(horseService.saveHorse(existingHorse)).thenReturn(existingHorse);

        HorseDTO result = horseController.updateHorsePartially(55L, dto, auth);

        assertEquals("Uj nev", result.getHorseName());
        assertEquals(11L, result.getOwnerId());
        assertEquals(21L, result.getStableId());
    }

    @Test
    void deleteHorse_returnsOk() {
        var response = horseController.deleteHorse(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ló sikeresen törölve.", response.getBody().get("message"));
        verify(horseService).deleteHorseById(1L);
    }

    @Test
    void getMyHorses_filtersByAuthenticatedUser() {
        Authentication auth = ControllerTestSupport.auth("tulaj1", "ROLE_OWNER");
        Horse mine = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(10L, "tulaj1", "Nagy", "Anna"), ControllerTestSupport.stable(20L, "A"));
        Horse others = ControllerTestSupport.horse(2L, "Villam", ControllerTestSupport.user(11L, "tulaj2", "Kiss", "Eva"), ControllerTestSupport.stable(20L, "A"));
        when(horseService.getAllHorses(auth)).thenReturn(List.of(mine, others));

        List<HorseDTO> result = horseController.getMyHorses(auth);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}
