package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class HorseTreatmentServiceTest {

    @Mock
    private HorseTreatmentRepository horseTreatmentRepository;

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CalendarEventService calendarEventService;

    @InjectMocks
    private HorseTreatmentService horseTreatmentService;

    @Test
    void addTreatmentToHorse_asAdmin_savesLinkAndSyncsEvent() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", admin, ServiceTestSupport.stable(3L, "Main"));
        Treatment treatment = ServiceTestSupport.treatment(4L, "Checkup");
        HorseTreatment savedLink = ServiceTestSupport.horseTreatment(5L, horse, treatment);

        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(treatmentRepository.findById(4L)).thenReturn(Optional.of(treatment));
        when(horseTreatmentRepository.existsByTreatmentAndHorse(treatment, horse)).thenReturn(false);
        when(horseTreatmentRepository.save(org.mockito.ArgumentMatchers.any(HorseTreatment.class))).thenReturn(savedLink);

        HorseTreatment result = horseTreatmentService.addTreatmentToHorse(4L, 2L, auth);

        assertEquals(5L, result.getId());
        verify(calendarEventService).syncFromDomain(horse, EventType.TREATMENT, treatment.getDate(), 4L);
    }

    @Test
    void addTreatmentToHorse_throwsWhenTreatmentAlreadyLinked() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", admin, ServiceTestSupport.stable(3L, "Main"));
        Treatment treatment = ServiceTestSupport.treatment(4L, "Checkup");

        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(treatmentRepository.findById(4L)).thenReturn(Optional.of(treatment));
        when(horseTreatmentRepository.existsByTreatmentAndHorse(treatment, horse)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> horseTreatmentService.addTreatmentToHorse(4L, 2L, auth));

        assertEquals("A kezelés már hozzá van adva a lóhoz.", exception.getMessage());
        verifyNoInteractions(calendarEventService);
    }

    @Test
    void addTreatmentToHorse_throwsWhenOwnerDoesNotOwnHorse() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User currentUser = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(9L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", otherOwner, ServiceTestSupport.stable(3L, "Main"));

        when(userRepository.findByUsername("anna")).thenReturn(currentUser);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> horseTreatmentService.addTreatmentToHorse(4L, 2L, auth));

        assertEquals("Csak a saját lovaidhoz adhatsz vagy törölhetsz oltásokat.", exception.getMessage());
        verifyNoInteractions(treatmentRepository, horseTreatmentRepository, calendarEventService);
    }

    @Test
    void queryMethods_filterForOwnerAndAdmin() {
        Authentication admin = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse ownHorse = ServiceTestSupport.horse(3L, "Csillag", owner, ServiceTestSupport.stable(4L, "Main"));
        Horse otherHorse = ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main"));
        Treatment treatment = ServiceTestSupport.treatment(6L, "Checkup");
        HorseTreatment ownLink = ServiceTestSupport.horseTreatment(7L, ownHorse, treatment);
        HorseTreatment otherLink = ServiceTestSupport.horseTreatment(8L, otherHorse, treatment);

        when(horseTreatmentRepository.findAll()).thenReturn(List.of(ownLink, otherLink));
        when(horseTreatmentRepository.findById(7L)).thenReturn(Optional.of(ownLink));
        when(horseTreatmentRepository.findByHorse_Id(3L)).thenReturn(List.of(ownLink));
        when(horseTreatmentRepository.findByTreatment_Id(6L)).thenReturn(List.of(ownLink, otherLink));

        assertEquals(2, horseTreatmentService.getAllHorseTreatments(admin).size());
        assertEquals(1, horseTreatmentService.getAllHorseTreatments(ownerAuth).size());
        assertSame(ownLink, horseTreatmentService.getHorseTreatmentById(7L, admin));
        assertSame(ownLink, horseTreatmentService.getHorseTreatmentById(7L, null));
        assertEquals(List.of(ownLink), horseTreatmentService.getTreatmentsForHorse(3L, ownerAuth));
        assertEquals(1, horseTreatmentService.getHorsesByTreatment(6L, ownerAuth).size());
    }

    @Test
    void getHorseTreatmentById_throwsWhenOwnerRequestsDifferentHorse() {
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse otherHorse = ServiceTestSupport.horse(5L, "Villam", otherOwner, ServiceTestSupport.stable(4L, "Main"));
        Treatment treatment = ServiceTestSupport.treatment(6L, "Checkup");
        HorseTreatment otherLink = ServiceTestSupport.horseTreatment(8L, otherHorse, treatment);

        when(horseTreatmentRepository.findById(8L)).thenReturn(Optional.of(otherLink));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> horseTreatmentService.getHorseTreatmentById(8L, ownerAuth));

        assertEquals("Csak a saját lovaidhoz tartozó kezeléseket érheted el.", exception.getMessage());
    }

    @Test
    void removeTreatmentFromHorse_deletesLinkAndEvent() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));

        horseTreatmentService.removeTreatmentFromHorse(4L, 2L, auth);

        verify(horseTreatmentRepository).deleteByTreatment_IdAndHorse_Id(4L, 2L);
        verify(calendarEventService).deleteFromDomain(EventType.TREATMENT, 4L, 2L);
    }
}
