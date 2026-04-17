package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import edu.pte.ttk.istallo_kezelo.dto.EmployeeAccessSettingsDTO;
import edu.pte.ttk.istallo_kezelo.model.AppSetting;
import edu.pte.ttk.istallo_kezelo.repository.AppSettingRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

/**
 * Test class for SettingsService behavior.
 */
@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    @Mock
    private AppSettingRepository appSettingRepository;

    @InjectMocks
    private SettingsService settingsService;

    @Test
    void assertEmployeeAccess_deniesWhenSettingFalse() {
        Authentication auth = ServiceTestSupport.auth("emp", "ROLE_EMPLOYEE");
        when(appSettingRepository.findById(SettingsService.EMPLOYEE_VIEW_SHOTS))
            .thenReturn(Optional.of(new AppSetting(SettingsService.EMPLOYEE_VIEW_SHOTS, Boolean.FALSE)));

        assertThrows(AccessDeniedException.class,
            () -> settingsService.assertEmployeeAccess(auth, SettingsService.EMPLOYEE_VIEW_SHOTS));
    }

    @Test
    void updateEmployeeAccessSettings_persistsAndReturnsUpdatedValues() {
        EmployeeAccessSettingsDTO dto = new EmployeeAccessSettingsDTO(Boolean.TRUE, null, null);

        when(appSettingRepository.findById(SettingsService.EMPLOYEE_VIEW_SHOTS))
            .thenReturn(Optional.empty(), Optional.of(new AppSetting(SettingsService.EMPLOYEE_VIEW_SHOTS, Boolean.TRUE)));
        when(appSettingRepository.findById(SettingsService.EMPLOYEE_VIEW_TREATMENTS))
            .thenReturn(Optional.empty());
        when(appSettingRepository.findById(SettingsService.EMPLOYEE_VIEW_FARRIER_APPS))
            .thenReturn(Optional.empty());
        when(appSettingRepository.save(any(AppSetting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeAccessSettingsDTO result = settingsService.updateEmployeeAccessSettings(dto);

        assertTrue(result.getViewShots());
        assertFalse(result.getViewTreatments());
        assertFalse(result.getViewFarrierApps());
    }
}
