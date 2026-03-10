package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
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
    }
}
