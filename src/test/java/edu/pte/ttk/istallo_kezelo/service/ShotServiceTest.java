package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.ShotDTO;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class ShotServiceTest {

    @Mock
    private ShotRepository shotRepository;

    @Mock
    private HorseShotRepository horseShotRepository;

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CalendarEventService calendarEventService;

    @Mock
    private SettingsService settingsService;

    @Mock
    private EventReminderService eventReminderService;

    @InjectMocks
    private ShotService shotService;

    @Test
    void saveShot_withHorseIds_savesLinksAndCreatesEvents() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", admin, ServiceTestSupport.stable(3L, "Main"));
        Shot shot = ServiceTestSupport.shot(4L, "Tetanosz");

        when(shotRepository.save(shot)).thenReturn(shot);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(horseShotRepository.save(org.mockito.ArgumentMatchers.any(HorseShot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shot result = shotService.saveShot(shot, List.of(2L), auth);

        assertEquals(1, result.getHorses_treated().size());
        verify(calendarEventService).createEvent(2L, EventType.SHOT, shot.getDate(), 4L, null);
        verify(eventReminderService).sendRemindersNow();
    }

    @Test
    void saveShot_throwsWhenOwnerAssignsOtherUsersHorse() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(9L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", otherOwner, ServiceTestSupport.stable(3L, "Main"));
        Shot shot = ServiceTestSupport.shot(4L, "Tetanosz");

        when(shotRepository.save(shot)).thenReturn(shot);
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> shotService.saveShot(shot, List.of(2L), auth));

        assertEquals("Csak a saját lovaidhoz adhatsz hozzá oltást.", exception.getMessage());
        verifyNoInteractions(horseShotRepository, calendarEventService, eventReminderService);
    }

    @Test
    void queryMethods_applyVisibilityAndMappings() {
        Authentication admin = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse ownHorse = ServiceTestSupport.horse(3L, "Csillag", owner, ServiceTestSupport.stable(4L, "Main"));
        Horse otherHorse = ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main"));
        Shot ownShot = ServiceTestSupport.shot(6L, "Tetanosz");
        Shot otherShot = ServiceTestSupport.shot(7L, "Influenza");
        ownShot.getHorses_treated().add(ServiceTestSupport.horseShot(8L, ownHorse, ownShot));
        otherShot.getHorses_treated().add(ServiceTestSupport.horseShot(9L, otherHorse, otherShot));

        when(shotRepository.findAll()).thenReturn(List.of(ownShot, otherShot));
        when(shotRepository.findById(6L)).thenReturn(Optional.of(ownShot));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findById(3L)).thenReturn(Optional.of(ownHorse));
        when(horseRepository.findByHorseName("Csillag")).thenReturn(ownHorse);
        when(horseShotRepository.findByHorse_Id(3L)).thenReturn(List.of(ownShot.getHorses_treated().get(0)));
        when(horseShotRepository.findByHorse_horseName("Csillag")).thenReturn(List.of(ownShot.getHorses_treated().get(0)));

        assertEquals(2, shotService.getAllShots(admin).size());
        assertEquals(1, shotService.getAllShots(ownerAuth).size());
        assertSame(ownShot, shotService.getShotById(6L, admin));
        assertEquals(List.of(ownShot), shotService.getShotsByHorseId(3L, ownerAuth));
        assertEquals(List.of(ownShot), shotService.getShotsByHorseName("Csillag", ownerAuth));
    }

    @Test
    void getShotById_throwsWhenOwnerCannotAccessOtherOwnersShot() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User otherOwner = ServiceTestSupport.user(9L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", otherOwner, ServiceTestSupport.stable(3L, "Main"));
        Shot shot = ServiceTestSupport.shot(4L, "Tetanosz");
        HorseShot link = ServiceTestSupport.horseShot(5L, horse, shot);
        shot.getHorses_treated().add(link);

        when(shotRepository.findById(4L)).thenReturn(Optional.of(shot));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> shotService.getShotById(4L, auth));

        assertEquals("Nincs jogosultságod ehhez az oltáshoz.", exception.getMessage());
        verify(settingsService).assertEmployeeAccess(auth, SettingsService.EMPLOYEE_VIEW_SHOTS);
    }

    @Test
    void updateShot_asAdminUpdatesCoreFieldsAndHorseLinks() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse currentHorse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        Horse newHorse = ServiceTestSupport.horse(4L, "Villam", owner, ServiceTestSupport.stable(3L, "Main"));
        Shot shot = ServiceTestSupport.shot(5L, "Tetanosz");
        HorseShot currentLink = ServiceTestSupport.horseShot(6L, currentHorse, shot);
        shot.getHorses_treated().add(currentLink);
        ShotDTO dto = new ShotDTO();
        dto.setShotName("Updated");
        dto.setDate(LocalDate.of(2026, 6, 1));
        dto.setFrequencyUnit("DAYS");
        dto.setFrequencyValue(10);
        dto.setHorseIds(List.of(4L));

        when(shotRepository.findById(5L)).thenReturn(Optional.of(shot));
        when(horseRepository.findById(4L)).thenReturn(Optional.of(newHorse));
        when(shotRepository.save(shot)).thenReturn(shot);

        Shot result = shotService.updateShot(5L, dto, auth);

        assertEquals("Updated", result.getShotName());
        verify(horseShotRepository).delete(currentLink);
        verify(calendarEventService).deleteFromDomain(EventType.SHOT, 5L, 2L);
        verify(calendarEventService).syncFromDomain(newHorse, EventType.SHOT, LocalDate.of(2026, 6, 1), 5L);
    }

    @Test
    void updateShot_forOwnerWithSharedShotCreatesReplacementShot() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse ownHorse = ServiceTestSupport.horse(3L, "Csillag", owner, ServiceTestSupport.stable(4L, "Main"));
        Horse otherHorse = ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main"));
        Shot original = ServiceTestSupport.shot(6L, "Tetanosz");
        HorseShot ownLink = ServiceTestSupport.horseShot(7L, ownHorse, original);
        original.getHorses_treated().add(ownLink);
        original.getHorses_treated().add(ServiceTestSupport.horseShot(8L, otherHorse, original));
        ShotDTO dto = new ShotDTO();
        dto.setShotName("Updated");
        dto.setHorseIds(List.of(3L));

        when(shotRepository.findById(6L)).thenReturn(Optional.of(original));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findById(3L)).thenReturn(Optional.of(ownHorse));
        when(shotRepository.save(org.mockito.ArgumentMatchers.any(Shot.class))).thenAnswer(invocation -> {
            Shot saved = invocation.getArgument(0);
            if (saved.getId() == null) {
                saved.setId(10L);
            }
            return saved;
        });
        when(horseShotRepository.save(org.mockito.ArgumentMatchers.any(HorseShot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shot result = shotService.updateShot(6L, dto, auth);

        assertEquals(10L, result.getId());
        assertEquals("Updated", result.getShotName());
        verify(horseShotRepository).delete(ownLink);
        verify(calendarEventService).deleteFromDomain(EventType.SHOT, 6L, 3L);
        verify(calendarEventService).syncFromDomain(ownHorse, EventType.SHOT, original.getDate(), 10L);
    }

    @Test
    void updateShot_throwsWhenOwnerTriesToModifyOtherOwnersShot() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User otherOwner = ServiceTestSupport.user(9L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", otherOwner, ServiceTestSupport.stable(3L, "Main"));
        Shot shot = ServiceTestSupport.shot(4L, "Tetanosz");
        HorseShot link = ServiceTestSupport.horseShot(5L, horse, shot);
        shot.getHorses_treated().add(link);
        ShotDTO dto = new ShotDTO();
        dto.setShotName("Updated");

        when(shotRepository.findById(4L)).thenReturn(Optional.of(shot));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> shotService.updateShot(4L, dto, auth));

        assertEquals("Csak saját lovakhoz tartozó oltásokat módosíthatsz.", exception.getMessage());
        verifyNoInteractions(calendarEventService);
    }

    @Test
    void deleteShotById_enforcesOwnershipAndDeletesCalendarEntries() {
        Authentication admin = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Shot ownShot = ServiceTestSupport.shot(6L, "Tetanosz");
        ownShot.getHorses_treated().add(ServiceTestSupport.horseShot(7L, ServiceTestSupport.horse(3L, "Csillag", owner, ServiceTestSupport.stable(4L, "Main")), ownShot));
        Shot otherShot = ServiceTestSupport.shot(8L, "Other");
        otherShot.getHorses_treated().add(ServiceTestSupport.horseShot(9L, ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main")), otherShot));

        when(shotRepository.findById(6L)).thenReturn(Optional.of(ownShot));
        when(shotRepository.findById(8L)).thenReturn(Optional.of(otherShot));

        shotService.deleteShotById(6L, admin);

        verify(shotRepository).deleteById(6L);
        verify(calendarEventService).deleteFromDomain(EventType.SHOT, 6L);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> shotService.deleteShotById(8L, ownerAuth));

        assertEquals("Csak a saját lovakhoz tartozó oltásokat törölheted.", exception.getMessage());
    }
}
