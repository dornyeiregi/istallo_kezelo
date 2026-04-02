package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.controller.AuthController;
import edu.pte.ttk.istallo_kezelo.dto.FarrierAppDTO;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.repository.FarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

/**
 * Test class for FarrierAppService behavior.
 */
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
}
