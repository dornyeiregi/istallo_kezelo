package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.TreatmentDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.service.TreatmentService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class TreatmentControllerTest {

    @Mock
    private TreatmentService treatmentService;

    @InjectMocks
    private TreatmentController treatmentController;

    @Test
    void createTreatment_returnsMappedDto() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        TreatmentDTO dto = new TreatmentDTO(null, "Sebkezeles", "Leiras", LocalDate.of(2026, 2, 1), List.of(1L));
        Treatment treatment = ControllerTestSupport.treatment(2L, "Sebkezeles");
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(3L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(4L, "A"));
        HorseTreatment link = ControllerTestSupport.horseTreatment(horse, treatment);
        treatment.getHorsesTreated().add(link);

        when(treatmentService.saveTreatment(any(Treatment.class), eq(List.of(1L)), eq(auth))).thenReturn(treatment);

        TreatmentDTO result = treatmentController.createTreatment(dto, auth);

        assertEquals(2L, result.getTreatmentId());
        assertEquals(List.of(1L), result.getHorseIds());
        ArgumentCaptor<Treatment> captor = ArgumentCaptor.forClass(Treatment.class);
        verify(treatmentService).saveTreatment(captor.capture(), eq(List.of(1L)), eq(auth));
        assertEquals("Leiras", captor.getValue().getDescription());
    }

    @Test
    void getAllTreatments_returnsMappedDtos() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        when(treatmentService.getAllTreatments(auth)).thenReturn(List.of(ControllerTestSupport.treatment(2L, "Sebkezeles")));

        List<TreatmentDTO> result = treatmentController.getAllTreatments(auth);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getTreatmentId());
    }

    @Test
    void getTreatmentById_returnsMappedDto() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        Treatment treatment = ControllerTestSupport.treatment(2L, "Sebkezeles");
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(3L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(4L, "A"));
        treatment.getHorsesTreated().add(ControllerTestSupport.horseTreatment(horse, treatment));
        when(treatmentService.getTreatmentById(2L, auth)).thenReturn(treatment);

        TreatmentDTO result = treatmentController.getTreatmentById(2L, auth);

        assertEquals("Sebkezeles", result.getTreatmentName());
    }

    @Test
    void getAllTreatmentsOfHorseById_returnsMappedDtos() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        Treatment treatment = ControllerTestSupport.treatment(2L, "Sebkezeles");
        Horse horse = ControllerTestSupport.horse(1L, "Csillag", ControllerTestSupport.user(3L, "anna", "Nagy", "Anna"), ControllerTestSupport.stable(4L, "A"));
        treatment.getHorsesTreated().add(ControllerTestSupport.horseTreatment(horse, treatment));
        when(treatmentService.getTreatmentsByHorseId(1L, auth)).thenReturn(List.of(treatment));

        List<TreatmentDTO> result = treatmentController.getAllTreatmentsOfHorseById(1L, auth);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getTreatmentId());
    }

    @Test
    void updateTreatment_returnsOk() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        TreatmentDTO dto = new TreatmentDTO();

        var response = treatmentController.updateTreatment(2L, dto, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Kezelés sikeresen frissítve.", response.getBody());
        verify(treatmentService).updateTreatment(2L, dto, auth);
    }

    @Test
    void deleteTreatment_returnsOk() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");

        var response = treatmentController.deleteTreatment(2L, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Kezelés sikeresen törölve.", response.getBody());
        verify(treatmentService).deleteTreatmentById(2L, auth);
    }
}
