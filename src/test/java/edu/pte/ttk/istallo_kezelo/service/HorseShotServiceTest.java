package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.ShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class HorseShotServiceTest {

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private ShotRepository shotRepository;

    @Mock
    private HorseShotRepository horseShotRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CalendarEventService calendarEventService;

    @InjectMocks
    private HorseShotService horseShotService;

    @Test
    void addShotToHorse_asAdmin_savesLinkAndSyncsEvent() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", admin, ServiceTestSupport.stable(3L, "Main"));
        Shot shot = ServiceTestSupport.shot(4L, "Tetanosz");
        HorseShot savedLink = ServiceTestSupport.horseShot(5L, horse, shot);

        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(shotRepository.findById(4L)).thenReturn(Optional.of(shot));
        when(horseShotRepository.existsByShotAndHorse(shot, horse)).thenReturn(false);
        when(horseShotRepository.save(org.mockito.ArgumentMatchers.any(HorseShot.class))).thenReturn(savedLink);

        HorseShot result = horseShotService.addShotToHorse(4L, 2L, auth);

        assertEquals(5L, result.getId());
        verify(calendarEventService).syncFromDomain(horse, EventType.SHOT, shot.getDate(), 4L);
    }

    @Test
    void addShotToHorse_throwsWhenShotAlreadyLinked() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", admin, ServiceTestSupport.stable(3L, "Main"));
        Shot shot = ServiceTestSupport.shot(4L, "Tetanosz");

        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(shotRepository.findById(4L)).thenReturn(Optional.of(shot));
        when(horseShotRepository.existsByShotAndHorse(shot, horse)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> horseShotService.addShotToHorse(4L, 2L, auth));

        assertEquals("Az oltás már hozzá van csatolva a lóhoz.", exception.getMessage());
        verifyNoInteractions(calendarEventService);
    }

    @Test
    void addShotToHorse_throwsWhenOwnerDoesNotOwnHorse() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User currentUser = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(9L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", otherOwner, ServiceTestSupport.stable(3L, "Main"));

        when(userRepository.findByUsername("anna")).thenReturn(currentUser);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> horseShotService.addShotToHorse(4L, 2L, auth));

        assertEquals("Csak a saját lovaidhoz adhatsz vagy törölhetsz oltásokat.", exception.getMessage());
        verifyNoInteractions(shotRepository, horseShotRepository, calendarEventService);
    }

    @Test
    void queryMethods_filterForOwnerAndAdmin() {
        Authentication admin = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse ownHorse = ServiceTestSupport.horse(3L, "Csillag", owner, ServiceTestSupport.stable(4L, "Main"));
        Horse otherHorse = ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main"));
        Shot shot = ServiceTestSupport.shot(6L, "Tetanosz");
        HorseShot ownLink = ServiceTestSupport.horseShot(7L, ownHorse, shot);
        HorseShot otherLink = ServiceTestSupport.horseShot(8L, otherHorse, shot);

        when(horseShotRepository.findAll()).thenReturn(List.of(ownLink, otherLink));
        when(horseShotRepository.findById(7L)).thenReturn(Optional.of(ownLink));
        when(horseShotRepository.findByShot_Id(6L)).thenReturn(List.of(ownLink, otherLink));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findById(3L)).thenReturn(Optional.of(ownHorse));
        when(horseShotRepository.findByHorse_Id(3L)).thenReturn(List.of(ownLink));

        assertEquals(2, horseShotService.getAllHorseShots(admin).size());
        assertEquals(1, horseShotService.getAllHorseShots(ownerAuth).size());
        assertSame(ownLink, horseShotService.getHorseShotById(7L, admin));
        assertSame(ownLink, horseShotService.getHorseShotById(7L, null));
        assertEquals(1, horseShotService.getHorseForShot(6L, ownerAuth).size());
        assertEquals(List.of(ownLink), horseShotService.getShotsForHorse(3L, ownerAuth));
    }

    @Test
    void getHorseShotById_throwsWhenOwnerRequestsDifferentHorse() {
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse otherHorse = ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main"));
        Shot shot = ServiceTestSupport.shot(6L, "Tetanosz");
        HorseShot otherLink = ServiceTestSupport.horseShot(8L, otherHorse, shot);

        when(horseShotRepository.findById(8L)).thenReturn(Optional.of(otherLink));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> horseShotService.getHorseShotById(8L, ownerAuth));

        assertEquals("Csak a saját lovaidhoz tartozó oltásokat érheted el.", exception.getMessage());
    }

    @Test
    void removeShotFromHorse_deletesLinkAndEvent() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));

        horseShotService.removeShotFromHorse(4L, 2L, auth);

        verify(horseShotRepository).deleteByShot_IdAndHorse_Id(4L, 2L);
        verify(calendarEventService).deleteFromDomain(EventType.SHOT, 4L, 2L);
    }
}
