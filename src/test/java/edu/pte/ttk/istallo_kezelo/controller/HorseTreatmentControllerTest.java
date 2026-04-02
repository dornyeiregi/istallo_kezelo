package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.HorseTreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.service.HorseTreatmentService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

/**
 * Test class for HorseTreatmentController behavior.
 */
@ExtendWith(MockitoExtension.class)
class HorseTreatmentControllerTest {

    @Mock
    private HorseTreatmentService horseTreatmentService;

    @InjectMocks
    private HorseTreatmentController horseTreatmentController;

    @Test
    void addTreatmentToHorse_returnsMappedDto() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(2L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(3L, "A"));
        Treatment treatment = ControllerTestSupport.treatment(4L, "Sebkezeles");
        HorseTreatment link = ControllerTestSupport.horseTreatment(horse, treatment);
        HorseTreatmentDTO dto = new HorseTreatmentDTO(1L, 4L, null, null, null);

        when(horseTreatmentService.addTreatmentToHorse(4L, 1L, auth)).thenReturn(link);

        HorseTreatmentDTO result = horseTreatmentController.addTreatmentToHorse(dto, auth);

        assertEquals(1L, result.getHorseId());
        assertEquals(4L, result.getTreatmentId());
    }

    @Test
    void getAllHorseTreatments_returnsMappedDtos() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(2L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(3L, "A"));
        Treatment treatment = ControllerTestSupport.treatment(4L, "Sebkezeles");
        HorseTreatment link = ControllerTestSupport.horseTreatment(horse, treatment);
        when(horseTreatmentService.getAllHorseTreatments(auth)).thenReturn(List.of(link));

        List<HorseTreatmentDTO> result = horseTreatmentController.getAllHorseTreatments(auth);

        assertEquals(1, result.size());
        assertEquals("Sebkezeles", result.get(0).getTreatmentName());
    }
}
