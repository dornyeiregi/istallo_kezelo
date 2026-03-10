package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.Horse;
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
import java.util.List;
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
    void getAllHorses_forOwnerReturnsOwnedHorses() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        Horse horse = ServiceTestSupport.horse(2L, "Csillag", owner, ServiceTestSupport.stable(3L, "Main"));

        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(horseRepository.findByOwner(owner)).thenReturn(List.of(horse));

        List<Horse> result = horseService.getAllHorses(auth);

        assertEquals(1, result.size());
        assertEquals("Csillag", result.get(0).getHorseName());
    }
}
