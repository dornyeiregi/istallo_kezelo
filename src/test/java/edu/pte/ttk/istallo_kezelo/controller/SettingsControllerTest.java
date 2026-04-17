package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.EmployeeAccessSettingsDTO;
import edu.pte.ttk.istallo_kezelo.service.SettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for SettingsController behavior.
 */
@ExtendWith(MockitoExtension.class)
class SettingsControllerTest {

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private SettingsController settingsController;

    @Test
    void getEmployeeAccessSettings_returnsDto() {
        EmployeeAccessSettingsDTO dto = new EmployeeAccessSettingsDTO(true, false, true);
        when(settingsService.getEmployeeAccessSettings()).thenReturn(dto);

        EmployeeAccessSettingsDTO result = settingsController.getEmployeeAccessSettings();

        assertEquals(dto, result);
    }

    @Test
    void updateEmployeeAccessSettings_returnsUpdatedDto() {
        EmployeeAccessSettingsDTO request = new EmployeeAccessSettingsDTO(true, null, false);
        EmployeeAccessSettingsDTO response = new EmployeeAccessSettingsDTO(true, false, false);
        when(settingsService.updateEmployeeAccessSettings(request)).thenReturn(response);

        EmployeeAccessSettingsDTO result = settingsController.updateEmployeeAccessSettings(request);

        assertEquals(response, result);
        verify(settingsService).updateEmployeeAccessSettings(request);
    }
}
