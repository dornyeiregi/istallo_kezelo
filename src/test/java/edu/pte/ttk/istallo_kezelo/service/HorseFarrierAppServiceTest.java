package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

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

    @Mock
    private EventReminderService eventReminderService;

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
        verify(eventReminderService).sendRemindersNow();
    }

    @Test
    void addHorseToFarrierApp_throwsWhenInvalidShoeCount() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", admin, ServiceTestSupport.stable(3L, "Main"));
        FarrierApp farrierApp = ServiceTestSupport.farrierApp(4L, "Bela");

        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(farrierAppRepository.findById(4L)).thenReturn(Optional.of(farrierApp));
        when(horseFarrierAppRepository.existsByFarrierAppAndHorse(farrierApp, horse)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> horseFarrierAppService.addHorseToFarrierApp(4L, 2L, 3, "note", auth));

        assertEquals("Érvénytelen patkó darabszám. Lehetséges értékek: 0, 2, 4.", exception.getMessage());
    }

    @Test
    void queryMethods_filterForOwnerAndAdmin() {
        Authentication admin = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse ownHorse = ServiceTestSupport.horse(3L, "Csillag", owner, ServiceTestSupport.stable(4L, "Main"));
        Horse otherHorse = ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main"));
        FarrierApp farrierApp = ServiceTestSupport.farrierApp(6L, "Bela");
        HorseFarrierApp ownLink = ServiceTestSupport.horseFarrierApp(7L, ownHorse, farrierApp);
        HorseFarrierApp otherLink = ServiceTestSupport.horseFarrierApp(8L, otherHorse, farrierApp);

        when(horseFarrierAppRepository.findAll()).thenReturn(List.of(ownLink, otherLink));
        when(horseFarrierAppRepository.findByFarrierApp_Id(6L)).thenReturn(List.of(ownLink, otherLink));
        when(horseFarrierAppRepository.findByHorseId(3L)).thenReturn(List.of(ownLink));
        when(horseFarrierAppRepository.findById(7L)).thenReturn(Optional.of(ownLink));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findById(3L)).thenReturn(Optional.of(ownHorse));

        assertEquals(2, horseFarrierAppService.getAllHorseFarrierApps(admin).size());
        assertEquals(1, horseFarrierAppService.getAllHorseFarrierApps(ownerAuth).size());
        assertEquals(1, horseFarrierAppService.getHorsesForFarrierApp(6L, ownerAuth).size());
        assertEquals(List.of(ownLink), horseFarrierAppService.getFarrierAppsForHorse(3L, ownerAuth));
        assertSame(ownLink, horseFarrierAppService.getHorseFarrierAppById(7L, admin));
        assertSame(ownLink, horseFarrierAppService.getHorseFarrierAppById(7L, null));
    }

    @Test
    void getHorseFarrierAppById_throwsWhenOwnerRequestsDifferentHorse() {
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse otherHorse = ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main"));
        FarrierApp farrierApp = ServiceTestSupport.farrierApp(6L, "Bela");
        HorseFarrierApp otherLink = ServiceTestSupport.horseFarrierApp(8L, otherHorse, farrierApp);

        when(horseFarrierAppRepository.findById(8L)).thenReturn(Optional.of(otherLink));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> horseFarrierAppService.getHorseFarrierAppById(8L, ownerAuth));

        assertEquals("Csak a saját lovadhoz tartozó patkolást érheted el.", exception.getMessage());
    }

    @Test
    void removeHorseFromFarrierApp_deletesLinkAndEvent() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));

        horseFarrierAppService.removeHorseFromFarrierApp(4L, 2L, auth);

        verify(horseFarrierAppRepository).deleteByFarrierApp_IdAndHorse_Id(4L, 2L);
        verify(calendarEventService).deleteFromDomain(EventType.FARRIERAPP, 4L, 2L);
    }

    @Test
    void addHorseToFarrierApp_throwsWhenOwnerDoesNotOwnHorse() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User currentUser = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(9L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", otherOwner, ServiceTestSupport.stable(3L, "Main"));

        when(userRepository.findByUsername("anna")).thenReturn(currentUser);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> horseFarrierAppService.addHorseToFarrierApp(4L, 2L, auth));

        assertEquals("Csak saját lovaidhoz adhatsz vagy törölhetsz patkolást.", exception.getMessage());
        verifyNoInteractions(farrierAppRepository, horseFarrierAppRepository, calendarEventService, eventReminderService);
    }
}
