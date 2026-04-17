package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

/**
 * Test class for HorseShotService behavior.
 */
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
}
