package edu.pte.ttk.istallo_kezelo.controller;

import edu.pte.ttk.istallo_kezelo.dto.HorseDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.Sex;
import edu.pte.ttk.istallo_kezelo.service.HorseService;
import edu.pte.ttk.istallo_kezelo.service.StableService;
import edu.pte.ttk.istallo_kezelo.service.UserService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
        User owner = buildOwner(10L, "tulaj1", "Nagy", "Anna");
        Stable stable = buildStable(20L, "Foistallo");

        HorseDTO dto = new HorseDTO();
        dto.setHorseName("Csillag");
        dto.setDob(LocalDate.of(2018, 5, 20));
        dto.setSex(Sex.F);
        dto.setOwnerId(owner.getId());
        dto.setStableName(stable.getStableName());
        dto.setMicrochipNum("HU-123");
        dto.setPassportNum("HU-999");
        dto.setAdditional("Nyugodt");

        when(userService.getUserById(eq(owner.getId()), any())).thenReturn(Optional.of(owner));
        when(stableService.getStableByName(eq(stable.getStableName()))).thenReturn(stable);
        when(horseService.saveHorse(any(Horse.class))).thenAnswer(invocation -> {
            Horse horse = invocation.getArgument(0);
            ReflectionTestUtils.setField(horse, "id", 55L);
            return horse;
        });

        HorseDTO result = horseController.createHorse(dto, auth("admin", "ROLE_ADMIN"));

        assertEquals(55L, result.getId());
        assertEquals("Csillag", result.getHorseName());
        assertEquals("Nagy Anna", result.getOwnerName());
        assertEquals("Foistallo", result.getStableName());
        assertEquals(Sex.F, result.getSex());
    }

    @Test
    void getHorseById_returnsHorseDtoForOwner() {
        Horse horse = buildHorse(101L, "Szel", "tulaj1", "Kovacs", "Eva", "Foistallo");

        when(horseService.getHorseById(eq(101L), any())).thenReturn(Optional.of(horse));

        HorseDTO result = horseController.getHorseById(101L, auth("tulaj1", "ROLE_OWNER"));

        assertEquals(101L, result.getId());
        assertEquals("Szel", result.getHorseName());
        assertEquals("Kovacs Eva", result.getOwnerName());
        assertEquals("Foistallo", result.getStableName());
    }

    @Test
    void getHorseById_ownerMismatch_throwsRuntimeException() {
        Horse horse = buildHorse(202L, "Villam", "tulaj1", "Kiss", "Adam", "Teszt istallo");

        when(horseService.getHorseById(eq(202L), any())).thenReturn(Optional.of(horse));

        assertThrows(RuntimeException.class, () ->
            horseController.getHorseById(202L, auth("tulaj2", "ROLE_OWNER"))
        );
    }

    private static Authentication auth(String username, String... roles) {
        java.util.List<SimpleGrantedAuthority> authorities = java.util.Arrays.stream(roles)
            .map(SimpleGrantedAuthority::new)
            .toList();
        return new UsernamePasswordAuthenticationToken(username, "n/a", authorities);
    }

    private static User buildOwner(Long id, String username, String lName, String fName) {
        User owner = new User();
        owner.setId(id);
        owner.setUsername(username);
        owner.setUserLname(lName);
        owner.setUserFname(fName);
        return owner;
    }

    private static Stable buildStable(Long id, String name) {
        Stable stable = new Stable();
        ReflectionTestUtils.setField(stable, "id", id);
        stable.setStableName(name);
        return stable;
    }

    private static Horse buildHorse(Long id, String horseName, String username, String lName, String fName, String stableName) {
        User owner = buildOwner(30L, username, lName, fName);
        Stable stable = buildStable(40L, stableName);
        Horse horse = new Horse();
        ReflectionTestUtils.setField(horse, "id", id);
        horse.setHorseName(horseName);
        horse.setSex(Sex.M);
        horse.setOwner(owner);
        horse.setStable(stable);
        return horse;
    }
}
