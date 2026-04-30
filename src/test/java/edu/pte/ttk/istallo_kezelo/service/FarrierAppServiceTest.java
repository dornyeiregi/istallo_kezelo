package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.controller.AuthController;
import edu.pte.ttk.istallo_kezelo.dto.FarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.dto.FarrierHorseDTO;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.FarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
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
class FarrierAppServiceTest {

    @Mock
    private FarrierAppRepository farrierAppRepository;

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthController authController;

    @Mock
    private CalendarEventService calendarEventService;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private FarrierAppService farrierAppService;

    @Test
    void createFarrierApp_withoutHorseIds_savesAppointment() {
        FarrierAppDTO dto = new FarrierAppDTO();
        dto.setAppointmentDate(LocalDate.of(2026, 5, 10));
        dto.setFarrierName("Bela");
        dto.setFarrierPhone("555");
        dto.setShoes(Boolean.TRUE);
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        when(farrierAppRepository.save(any(FarrierApp.class))).thenAnswer(invocation -> {
            FarrierApp app = invocation.getArgument(0);
            app.setId(10L);
            return app;
        });

        FarrierApp result = farrierAppService.createFarrierApp(dto, auth);

        assertEquals(10L, result.getId());
        assertEquals("Bela", result.getFarrierName());
        assertEquals(LocalDate.of(2026, 5, 10), result.getAppointmentDate());
    }

    @Test
    void createFarrierApp_withHorseDetailsAddsHorseAndDefaultsShoeCount() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", admin, ServiceTestSupport.stable(3L, "Main"));
        FarrierApp savedApp = ServiceTestSupport.farrierApp(10L, "Bela");
        savedApp.setAppointmentDate(LocalDate.of(2026, 5, 10));
        FarrierAppDTO dto = new FarrierAppDTO();
        dto.setAppointmentDate(LocalDate.of(2026, 5, 10));
        dto.setFarrierName("Bela");
        dto.setShoes(Boolean.TRUE);
        dto.setHorseDetails(List.of(new FarrierHorseDTO(2L, "Csillag", null, "note")));

        when(farrierAppRepository.save(any(FarrierApp.class))).thenAnswer(invocation -> {
            FarrierApp app = invocation.getArgument(0);
            if (app.getId() == null) {
                app.setId(10L);
            }
            return app;
        });
        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(farrierAppRepository.findById(10L)).thenReturn(Optional.of(savedApp));

        FarrierApp result = farrierAppService.createFarrierApp(dto, auth);

        assertEquals(10L, result.getId());
        verify(calendarEventService).createEvent(2L, EventType.FARRIERAPP, LocalDate.of(2026, 5, 10), 10L, null);
    }

    @Test
    void createFarrierApp_throwsWhenOwnerAssignsOtherUsersHorse() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(9L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", otherOwner, ServiceTestSupport.stable(3L, "Main"));
        FarrierAppDTO dto = new FarrierAppDTO();
        dto.setAppointmentDate(LocalDate.of(2026, 5, 10));
        dto.setFarrierName("Bela");
        dto.setHorseIds(List.of(2L));

        when(farrierAppRepository.save(any(FarrierApp.class))).thenAnswer(invocation -> {
            FarrierApp app = invocation.getArgument(0);
            app.setId(10L);
            return app;
        });
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> farrierAppService.createFarrierApp(dto, auth));

        assertEquals("Csak a saját lovaidhoz adhatsz hozzá patkolást.", exception.getMessage());
        verifyNoInteractions(calendarEventService);
    }

    @Test
    void createFarrierApp_throwsWhenInvalidShoeCountProvided() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", admin, ServiceTestSupport.stable(3L, "Main"));
        FarrierApp savedApp = ServiceTestSupport.farrierApp(10L, "Bela");
        FarrierAppDTO dto = new FarrierAppDTO();
        dto.setAppointmentDate(LocalDate.of(2026, 5, 10));
        dto.setFarrierName("Bela");
        dto.setShoes(Boolean.TRUE);
        dto.setHorseDetails(List.of(new FarrierHorseDTO(2L, "Csillag", 3, "note")));

        when(farrierAppRepository.save(any(FarrierApp.class))).thenAnswer(invocation -> {
            FarrierApp app = invocation.getArgument(0);
            if (app.getId() == null) {
                app.setId(10L);
            }
            return app;
        });
        when(farrierAppRepository.findById(10L)).thenReturn(Optional.of(savedApp));
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(userRepository.findByUsername("admin")).thenReturn(admin);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> farrierAppService.createFarrierApp(dto, auth));

        assertEquals("Érvénytelen patkó darabszám. Lehetséges értékek: 0, 2, 4.", exception.getMessage());
        verifyNoInteractions(calendarEventService);
    }

    @Test
    void queryMethods_applyVisibilityAndFilters() {
        Authentication admin = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse ownHorse = ServiceTestSupport.horse(3L, "Csillag", owner, ServiceTestSupport.stable(4L, "Main"));
        Horse otherHorse = ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main"));
        FarrierApp ownApp = ServiceTestSupport.farrierApp(6L, "Bela");
        FarrierApp otherApp = ServiceTestSupport.farrierApp(7L, "Jozsi");
        ownApp.getHorses_done().add(ServiceTestSupport.horseFarrierApp(8L, ownHorse, ownApp));
        otherApp.getHorses_done().add(ServiceTestSupport.horseFarrierApp(9L, otherHorse, otherApp));

        when(farrierAppRepository.findAll()).thenReturn(List.of(ownApp, otherApp));
        when(farrierAppRepository.findByAppointmentDate(LocalDate.of(2026, 3, 1))).thenReturn(List.of(ownApp));
        when(farrierAppRepository.findByFarrierName("Bela")).thenReturn(List.of(ownApp));
        when(farrierAppRepository.findByHorsesDone_Horse_HorseName("Csillag")).thenReturn(List.of(ownApp));
        when(farrierAppRepository.findById(6L)).thenReturn(Optional.of(ownApp));

        assertEquals(2, farrierAppService.getAllFarrierApps(admin).size());
        assertEquals(1, farrierAppService.getAllFarrierApps(ownerAuth).size());
        assertEquals(1, farrierAppService.getFarrierAppsByDate(LocalDate.of(2026, 3, 1), ownerAuth).size());
        assertEquals(1, farrierAppService.getFarrierAppsByFarrierName("Bela", ownerAuth).size());
        assertEquals(1, farrierAppService.getFarrierAppsByHorseName("Csillag", ownerAuth).size());
        assertEquals(1, farrierAppService.getFarrierAppByHorseId(3L, ownerAuth).size());
        assertSame(ownApp, farrierAppService.getFarrierAppById(6L, admin));
    }

    @Test
    void getFarrierAppById_throwsWhenOwnerCannotAccessOtherOwnersAppointment() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User otherOwner = ServiceTestSupport.user(9L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", otherOwner, ServiceTestSupport.stable(3L, "Main"));
        FarrierApp app = ServiceTestSupport.farrierApp(10L, "Bela");
        HorseFarrierApp link = ServiceTestSupport.horseFarrierApp(5L, horse, app);
        app.getHorses_done().add(link);

        when(farrierAppRepository.findById(10L)).thenReturn(Optional.of(app));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> farrierAppService.getFarrierAppById(10L, auth));

        assertEquals("Nincs jogosultságod ehhez a patkolás megetkintéséhez.", exception.getMessage());
        verify(settingsService).assertEmployeeAccess(auth, SettingsService.EMPLOYEE_VIEW_FARRIER_APPS);
    }

    @Test
    void updateFarrierApp_updatesFieldsAndRecreatesEvents() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        FarrierApp existing = ServiceTestSupport.farrierApp(10L, "Bela");
        FarrierAppDTO dto = new FarrierAppDTO();
        dto.setFarrierName("Updated");
        dto.setAppointmentDate(LocalDate.of(2026, 6, 1));
        dto.setHorseIds(List.of(2L));
        dto.setShoes(Boolean.FALSE);

        when(farrierAppRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(farrierAppRepository.save(existing)).thenReturn(existing);

        farrierAppService.updateFarrierApp(10L, dto, auth);

        assertEquals("Updated", existing.getFarrierName());
        verify(calendarEventService).deleteFromDomain(EventType.FARRIERAPP, 10L);
        verify(calendarEventService).createEvent(2L, EventType.FARRIERAPP, LocalDate.of(2026, 6, 1), 10L, null);
    }

    @Test
    void updateFarrierApp_withHorseDetailsStoresDetailFields() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        FarrierApp existing = ServiceTestSupport.farrierApp(10L, "Bela");
        FarrierAppDTO dto = new FarrierAppDTO();
        dto.setHorseDetails(List.of(new FarrierHorseDTO(2L, "Csillag", 2, "front only")));
        dto.setShoes(Boolean.TRUE);

        when(farrierAppRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(farrierAppRepository.save(existing)).thenReturn(existing);

        farrierAppService.updateFarrierApp(10L, dto, auth);

        assertEquals(1, existing.getHorses_done().size());
        assertEquals(2, existing.getHorses_done().get(0).getShoeCount());
        assertEquals("front only", existing.getHorses_done().get(0).getNote());
    }

    @Test
    void addHorseToFarrierApp_overloadUsesDefaults() {
        FarrierApp existing = ServiceTestSupport.farrierApp(10L, "Bela");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));

        when(farrierAppRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(farrierAppRepository.save(existing)).thenReturn(existing);

        farrierAppService.addHorseToFarrierApp(10L, 2L);

        verify(calendarEventService).createEvent(2L, EventType.FARRIERAPP, existing.getAppointmentDate(), 10L, null);
    }

    @Test
    void deleteFarrierApp_deletesRecordAndEvent() {
        farrierAppService.deleteFarrierApp(10L);

        verify(farrierAppRepository).deleteById(10L);
        verify(calendarEventService).deleteFromDomain(EventType.FARRIERAPP, 10L);
    }
}
