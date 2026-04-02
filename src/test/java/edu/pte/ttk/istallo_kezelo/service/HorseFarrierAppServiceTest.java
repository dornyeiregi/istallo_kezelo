package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.FarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

/**
 * Test class for HorseFarrierAppService behavior.
 */
@ExtendWith(MockitoExtension.class)
class HorseFarrierAppServiceTest {

    @Mock
    private FarrierAppRepository farrierAppRepository;

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private HorseFarrierAppRepository horseFarrierAppRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CalendarEventService calendarEventService;

    @InjectMocks
    private HorseFarrierAppService horseFarrierAppService;

    @Test
    void addHorseToFarrierApp_asAdmin_savesLinkAndSyncsEvent() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", admin, ServiceTestSupport.stable(3L, "Main"));
        FarrierApp farrierApp = ServiceTestSupport.farrierApp(4L, "Bela");
        HorseFarrierApp savedLink = ServiceTestSupport.horseFarrierApp(5L, horse, farrierApp);

        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(farrierAppRepository.findById(4L)).thenReturn(Optional.of(farrierApp));
        when(horseFarrierAppRepository.existsByFarrierAppAndHorse(farrierApp, horse)).thenReturn(false);
        when(horseFarrierAppRepository.save(org.mockito.ArgumentMatchers.any(HorseFarrierApp.class))).thenReturn(savedLink);

        HorseFarrierApp result = horseFarrierAppService.addHorseToFarrierApp(4L, 2L, auth);

        assertEquals(5L, result.getId());
        verify(calendarEventService).syncFromDomain(horse, EventType.FARRIERAPP, farrierApp.getAppointmentDate(), 4L);
    }
}
