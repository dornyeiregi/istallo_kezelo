package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.FarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseTreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.ShotRepository;
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
class HorseServiceTest {

    @Mock
    private HorseRepository horseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HorseShotRepository horseShotRepository;

    @Mock
    private ShotRepository shotRepository;

    @Mock
    private HorseFarrierAppRepository horseFarrierAppRepository;

    @Mock
    private FarrierAppRepository farrierAppRepository;

    @Mock
    private HorseFeedSchedRepository horseFeedSchedRepository;

    @Mock
    private FeedSchedRepository feedSchedRepository;

    @Mock
    private HorseTreatmentRepository horseTreatmentRepository;

    @Mock
    private TreatmentRepository treatmentRepository;

    @InjectMocks
    private HorseService horseService;

    @Test
    void saveHorse_delegatesToRepository() {
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", ServiceTestSupport.user(1L, "anna", UserType.OWNER), ServiceTestSupport.stable(3L, "Main"));
        when(horseRepository.save(horse)).thenReturn(horse);

        assertSame(horse, horseService.saveHorse(horse));
    }

    @Test
    void getAllHorses_forOwnerReturnsOwnedHorses() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));

        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findByOwnerAndIsActiveTrueOrOwnerAndIsActiveIsNull(owner, owner)).thenReturn(List.of(horse));

        List<Horse> result = horseService.getAllHorses(auth);

        assertEquals(1, result.size());
        assertEquals("Csillag", result.get(0).getHorseName());
    }

    @Test
    void getAllHorses_forAdminFiltersPending() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        Horse active = ServiceTestSupport.horse(2L, "Csillag", admin, ServiceTestSupport.stable(3L, "Main"));
        Horse pending = ServiceTestSupport.horse(4L, "Villam", admin, ServiceTestSupport.stable(3L, "Main"));
        pending.setIsPending(Boolean.TRUE);

        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(horseRepository.findByIsActiveTrueOrIsActiveIsNull()).thenReturn(List.of(active, pending));

        List<Horse> result = horseService.getAllHorses(auth);

        assertEquals(List.of(active), result);
    }

    @Test
    void getHorseById_respectsAccessRules() {
        Authentication adminAuth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(3L, "Csillag", otherOwner, ServiceTestSupport.stable(4L, "Main"));

        when(horseRepository.findById(3L)).thenReturn(Optional.of(horse));

        assertEquals(Optional.of(horse), horseService.getHorseById(3L, adminAuth));
        assertTrue(horseService.getHorseById(3L, ownerAuth).isEmpty());
    }

    @Test
    void getHorseByName_handlesSuccessAndDeniedAccess() {
        Authentication adminAuth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        Authentication ownerAuth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User otherOwner = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(3L, "Csillag", otherOwner, ServiceTestSupport.stable(4L, "Main"));

        when(horseRepository.findByHorseName("Csillag")).thenReturn(horse);
        when(horseRepository.findByHorseName("Unknown")).thenReturn(null);

        assertSame(horse, horseService.getHorseByName("Csillag", adminAuth));
        assertEquals("Ló nem található.", assertThrows(RuntimeException.class,
            () -> horseService.getHorseByName("Unknown", adminAuth)).getMessage());
        assertEquals("Nincs jogosultságod megtekinteni ezt a lovat.", assertThrows(RuntimeException.class,
            () -> horseService.getHorseByName("Csillag", ownerAuth)).getMessage());
    }

    @Test
    void updateHorse_updatesAllFieldsForOwner() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Stable stable = ServiceTestSupport.stable(3L, "Main");
        Stable newStable = ServiceTestSupport.stable(4L, "New");
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, stable);
        Horse updated = ServiceTestSupport.horse(2L, "Villam", owner, newStable);
        updated.setDob(LocalDate.of(2019, 2, 2));
        updated.setAdditional("updated");

        when(horseRepository.findByHorseName("Csillag")).thenReturn(horse);
        when(horseRepository.save(horse)).thenReturn(horse);

        Horse result = horseService.updateHorse("Csillag", updated, auth);

        assertEquals("Villam", result.getHorseName());
        assertEquals(newStable, result.getStable());
        assertEquals("updated", result.getAdditional());
    }

    @Test
    void deleteHorseById_removesOrphanedLinkedEntities() {
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", ServiceTestSupport.user(1L, "anna", UserType.OWNER), ServiceTestSupport.stable(3L, "Main"));
        Shot shot = ServiceTestSupport.shot(4L, "Tetanosz");
        Treatment treatment = ServiceTestSupport.treatment(5L, "Checkup");
        FarrierApp farrierApp = ServiceTestSupport.farrierApp(6L, "Bela");
        FeedSched feedSched = ServiceTestSupport.feedSched(7L, "Morning");
        HorseShot shotLink = ServiceTestSupport.horseShot(8L, horse, shot);
        HorseTreatment treatmentLink = ServiceTestSupport.horseTreatment(9L, horse, treatment);
        HorseFarrierApp farrierLink = ServiceTestSupport.horseFarrierApp(10L, horse, farrierApp);
        HorseFeedSched feedLink = ServiceTestSupport.horseFeedSched(11L, horse, feedSched);

        when(horseRepository.findById(2L)).thenReturn(Optional.of(horse));
        when(horseShotRepository.findByHorse_Id(2L)).thenReturn(List.of(shotLink));
        when(horseShotRepository.countByShot_Id(4L)).thenReturn(0);
        when(horseTreatmentRepository.findByHorse_Id(2L)).thenReturn(List.of(treatmentLink));
        when(horseTreatmentRepository.countByTreatment_Id(5L)).thenReturn(0);
        when(horseFarrierAppRepository.findByHorseId(2L)).thenReturn(List.of(farrierLink));
        when(horseFarrierAppRepository.countByFarrierApp_Id(6L)).thenReturn(0);
        when(horseFeedSchedRepository.findByHorseId(2L)).thenReturn(List.of(feedLink));
        when(horseFeedSchedRepository.countByFeedSchedId(7L)).thenReturn(0);

        horseService.deleteHorseById(2L);

        verify(horseShotRepository).deleteAll(List.of(shotLink));
        verify(shotRepository).deleteById(4L);
        verify(horseTreatmentRepository).deleteAll(List.of(treatmentLink));
        verify(treatmentRepository).deleteById(5L);
        verify(horseFarrierAppRepository).deleteAll(List.of(farrierLink));
        verify(farrierAppRepository).deleteById(6L);
        verify(horseFeedSchedRepository).deleteAll(List.of(feedLink));
        verify(feedSchedRepository).deleteById(7L);
        verify(horseRepository).delete(horse);
    }

    @Test
    void activateAndDeactivateHorse_updateFlags() {
        Horse horse = ServiceTestSupport.horse(3L, "Csillag", ServiceTestSupport.user(1L, "anna", UserType.OWNER), null);
        when(horseRepository.findById(3L)).thenReturn(Optional.of(horse));
        when(horseRepository.save(horse)).thenReturn(horse);

        Horse deactivated = horseService.deactivateHorseById(3L);
        Horse activated = horseService.activateHorseById(3L);

        assertEquals(Boolean.TRUE, activated.getIsActive());
        assertEquals(Boolean.FALSE, activated.getIsPending());
        assertEquals(activated, deactivated);
    }

    @Test
    void pendingAndInactiveQueries_delegateToRepository() {
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        Horse inactive = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));
        inactive.setIsPending(Boolean.FALSE);
        Horse pending = ServiceTestSupport.horse(4L, "Villam", owner, ServiceTestSupport.stable(3L, "Main"));
        pending.setIsPending(Boolean.TRUE);

        when(horseRepository.findByIsActiveFalse()).thenReturn(List.of(inactive));
        when(horseRepository.findByIsPendingTrue()).thenReturn(List.of(pending));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findByOwnerAndIsPendingTrue(owner)).thenReturn(List.of(pending));

        assertEquals(List.of(inactive), horseService.getInactiveHorses());
        assertEquals(List.of(pending), horseService.getPendingHorses());
        assertEquals(List.of(pending), horseService.getPendingHorsesForOwner(auth));
    }

    @Test
    void approveHorseRequest_throwsWhenStableMissing() {
        Horse horse = ServiceTestSupport.horse(3L, "Csillag", ServiceTestSupport.user(1L, "anna", UserType.OWNER), null);

        when(horseRepository.findById(3L)).thenReturn(java.util.Optional.of(horse));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> horseService.approveHorseRequest(3L, null));

        assertEquals("Istálló megadása kötelező.", exception.getMessage());
    }

    @Test
    void approveHorseRequest_setsHorseActiveAndStable() {
        Stable stable = ServiceTestSupport.stable(4L, "Main");
        Horse horse = ServiceTestSupport.horse(3L, "Csillag", ServiceTestSupport.user(1L, "anna", UserType.OWNER), null);
        horse.setIsPending(Boolean.TRUE);
        horse.setIsActive(Boolean.FALSE);

        when(horseRepository.findById(3L)).thenReturn(java.util.Optional.of(horse));
        when(horseRepository.save(horse)).thenReturn(horse);

        Horse result = horseService.approveHorseRequest(3L, stable);

        assertEquals(stable, result.getStable());
        assertEquals(Boolean.TRUE, result.getIsActive());
        assertEquals(Boolean.FALSE, result.getIsPending());
    }
}
