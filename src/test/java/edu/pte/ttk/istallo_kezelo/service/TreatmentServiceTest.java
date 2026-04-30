package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.TreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseTreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.TreatmentRepository;
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
class TreatmentServiceTest {

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private HorseTreatmentRepository horseTreatmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private CalendarEventService calendarEventService;

    @Mock
    private SettingsService settingsService;

    @Mock
    private EventReminderService eventReminderService;

    @InjectMocks
    private TreatmentService treatmentService;

    @Test
    void saveTreatment_withHorseIds_savesLinksAndSyncsEvents() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", admin, ServiceTestSupport.stable(3L, "Main"));
        Treatment treatment = ServiceTestSupport.treatment(4L, "Checkup");

        when(treatmentRepository.save(treatment)).thenReturn(treatment);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(horseTreatmentRepository.save(org.mockito.ArgumentMatchers.any(HorseTreatment.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Treatment result = treatmentService.saveTreatment(treatment, List.of(2L), auth);

        assertEquals(1, result.getHorsesTreated().size());
        verify(calendarEventService).syncFromDomain(horse, EventType.TREATMENT, treatment.getDate(), 4L);
        verify(eventReminderService).sendRemindersNow();
    }

    @Test
    void saveTreatment_throwsWhenOwnerAssignsOtherUsersHorse() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(9L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", otherOwner, ServiceTestSupport.stable(3L, "Main"));
        Treatment treatment = ServiceTestSupport.treatment(4L, "Checkup");

        when(treatmentRepository.save(treatment)).thenReturn(treatment);
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> treatmentService.saveTreatment(treatment, List.of(2L), auth));

        assertEquals("Csak a saját lovaidhoz adhatsz hozzá kezelést.", exception.getMessage());
        verifyNoInteractions(horseTreatmentRepository, calendarEventService, eventReminderService);
    }

    @Test
    void queryMethods_applyVisibilityAndMappings() {
        Authentication admin = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse ownHorse = ServiceTestSupport.horse(3L, "Csillag", owner, ServiceTestSupport.stable(4L, "Main"));
        Horse otherHorse = ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main"));
        Treatment treatment = ServiceTestSupport.treatment(6L, "Checkup");
        HorseTreatment ownLink = ServiceTestSupport.horseTreatment(7L, ownHorse, treatment);
        HorseTreatment otherLink = ServiceTestSupport.horseTreatment(8L, otherHorse, treatment);
        treatment.getHorsesTreated().addAll(List.of(ownLink, otherLink));

        when(treatmentRepository.findAll()).thenReturn(List.of(treatment));
        when(treatmentRepository.findById(6L)).thenReturn(Optional.of(treatment));
        when(horseRepository.findById(3L)).thenReturn(Optional.of(ownHorse));
        when(horseRepository.findByHorseName("Csillag")).thenReturn(ownHorse);
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseTreatmentRepository.findByHorse_Id(3L)).thenReturn(List.of(ownLink));
        when(horseTreatmentRepository.findByHorse_horseName("Csillag")).thenReturn(List.of(ownLink));

        assertEquals(1, treatmentService.getAllTreatments(admin).size());
        assertEquals(1, treatmentService.getAllTreatments(ownerAuth).size());
        assertSame(treatment, treatmentService.getTreatmentById(6L, admin));
        assertEquals(List.of(treatment), treatmentService.getTreatmentsByHorseId(3L, ownerAuth));
        assertEquals(List.of(treatment), treatmentService.getTreatmentsByHorseName("Csillag", ownerAuth));
    }

    @Test
    void getTreatmentById_throwsWhenOwnerCannotAccessOtherOwnersTreatment() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User otherOwner = ServiceTestSupport.user(9L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", otherOwner, ServiceTestSupport.stable(3L, "Main"));
        Treatment treatment = ServiceTestSupport.treatment(4L, "Checkup");
        HorseTreatment link = ServiceTestSupport.horseTreatment(5L, horse, treatment);
        treatment.getHorsesTreated().add(link);

        when(treatmentRepository.findById(4L)).thenReturn(Optional.of(treatment));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> treatmentService.getTreatmentById(4L, auth));

        assertEquals("Nincs jogosultságod ehhez a kezeléshez.", exception.getMessage());
        verify(settingsService).assertEmployeeAccess(auth, SettingsService.EMPLOYEE_VIEW_TREATMENTS);
    }

    @Test
    void updateTreatment_updatesFieldsAndHorseLinks() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse currentHorse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        Horse newHorse = ServiceTestSupport.horse(4L, "Villam", owner, ServiceTestSupport.stable(3L, "Main"));
        Treatment treatment = ServiceTestSupport.treatment(5L, "Checkup");
        HorseTreatment currentLink = ServiceTestSupport.horseTreatment(6L, currentHorse, treatment);
        treatment.getHorsesTreated().add(currentLink);
        TreatmentDTO dto = new TreatmentDTO();
        dto.setTreatmentName("Updated");
        dto.setDescription("updated");
        dto.setDate(LocalDate.of(2026, 5, 1));
        dto.setFrequencyUnit("DAYS");
        dto.setFrequencyValue(10);
        dto.setHorseIds(List.of(4L));

        when(treatmentRepository.findById(5L)).thenReturn(Optional.of(treatment));
        when(horseRepository.findById(4L)).thenReturn(Optional.of(newHorse));
        when(treatmentRepository.save(treatment)).thenReturn(treatment);

        Treatment result = treatmentService.updateTreatment(5L, dto, auth);

        assertEquals("Updated", result.getTreatmentName());
        verify(horseTreatmentRepository).delete(currentLink);
        verify(calendarEventService).deleteFromDomain(EventType.TREATMENT, 5L, 2L);
        verify(calendarEventService).syncFromDomain(newHorse, EventType.TREATMENT, LocalDate.of(2026, 5, 1), 5L);
    }

    @Test
    void updateTreatment_throwsWhenOwnerTriesToModifyOtherOwnersTreatment() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User otherOwner = ServiceTestSupport.user(9L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", otherOwner, ServiceTestSupport.stable(3L, "Main"));
        Treatment treatment = ServiceTestSupport.treatment(4L, "Checkup");
        HorseTreatment link = ServiceTestSupport.horseTreatment(5L, horse, treatment);
        treatment.getHorsesTreated().add(link);
        TreatmentDTO dto = new TreatmentDTO();
        dto.setTreatmentName("Updated");

        when(treatmentRepository.findById(4L)).thenReturn(Optional.of(treatment));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> treatmentService.updateTreatment(4L, dto, auth));

        assertEquals("Csak a saját lovakhoz tartozó kezeléseket módosíthatod.", exception.getMessage());
        verifyNoInteractions(calendarEventService);
    }

    @Test
    void deleteTreatmentById_enforcesOwnershipAndDeletesEvent() {
        Authentication admin = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse ownHorse = ServiceTestSupport.horse(3L, "Csillag", owner, ServiceTestSupport.stable(4L, "Main"));
        Horse otherHorse = ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main"));
        Treatment ownTreatment = ServiceTestSupport.treatment(6L, "Checkup");
        ownTreatment.getHorsesTreated().add(ServiceTestSupport.horseTreatment(7L, ownHorse, ownTreatment));
        Treatment otherTreatment = ServiceTestSupport.treatment(8L, "Other");
        otherTreatment.getHorsesTreated().add(ServiceTestSupport.horseTreatment(9L, otherHorse, otherTreatment));

        when(treatmentRepository.findById(6L)).thenReturn(Optional.of(ownTreatment));
        when(treatmentRepository.findById(8L)).thenReturn(Optional.of(otherTreatment));

        treatmentService.deleteTreatmentById(6L, admin);

        verify(treatmentRepository).deleteById(6L);
        verify(calendarEventService).deleteFromDomain(EventType.TREATMENT, 6L);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> treatmentService.deleteTreatmentById(8L, ownerAuth));

        assertEquals("Csak a saját lovakhoz tartozó kezeléseket törölheted.", exception.getMessage());
    }
}
