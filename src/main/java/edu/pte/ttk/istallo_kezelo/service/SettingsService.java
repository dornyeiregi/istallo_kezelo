package edu.pte.ttk.istallo_kezelo.service;

import edu.pte.ttk.istallo_kezelo.dto.EmployeeAccessSettingsDTO;
import edu.pte.ttk.istallo_kezelo.model.AppSetting;
import edu.pte.ttk.istallo_kezelo.repository.AppSettingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Alkalmazás-szolgáltatás hozzáférési beállításokhoz.
 */
@Service
public class SettingsService {

    public static final String EMPLOYEE_VIEW_SHOTS = "EMPLOYEE_VIEW_SHOTS";
    public static final String EMPLOYEE_VIEW_TREATMENTS = "EMPLOYEE_VIEW_TREATMENTS";
    public static final String EMPLOYEE_VIEW_FARRIER_APPS = "EMPLOYEE_VIEW_FARRIER_APPS";

    private final AppSettingRepository appSettingRepository;

    public SettingsService(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    @Transactional(readOnly = true)
    public EmployeeAccessSettingsDTO getEmployeeAccessSettings() {
        return new EmployeeAccessSettingsDTO(
            getBoolSetting(EMPLOYEE_VIEW_SHOTS, Boolean.FALSE),
            getBoolSetting(EMPLOYEE_VIEW_TREATMENTS, Boolean.FALSE),
            getBoolSetting(EMPLOYEE_VIEW_FARRIER_APPS, Boolean.FALSE)
        );
    }

    @Transactional
    public EmployeeAccessSettingsDTO updateEmployeeAccessSettings(EmployeeAccessSettingsDTO dto) {
        if (dto.getViewShots() != null) {
            setBoolSetting(EMPLOYEE_VIEW_SHOTS, dto.getViewShots());
        }
        if (dto.getViewTreatments() != null) {
            setBoolSetting(EMPLOYEE_VIEW_TREATMENTS, dto.getViewTreatments());
        }
        if (dto.getViewFarrierApps() != null) {
            setBoolSetting(EMPLOYEE_VIEW_FARRIER_APPS, dto.getViewFarrierApps());
        }
        return getEmployeeAccessSettings();
    }

    public void assertEmployeeAccess(Authentication auth, String settingKey) {
        if (!isEmployee(auth)) return;
        Boolean allowed = getBoolSetting(settingKey, Boolean.FALSE);
        if (!Boolean.TRUE.equals(allowed)) {
            throw new AccessDeniedException("Az alkalmazottak nem férnek hozzá ehhez a modulhoz.");
        }
    }

    public boolean isEmployee(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
    }

    private Boolean getBoolSetting(String key, Boolean defaultValue) {
        return appSettingRepository.findById(key)
            .map(AppSetting::getBoolValue)
            .orElse(defaultValue);
    }

    private void setBoolSetting(String key, Boolean value) {
        AppSetting setting = appSettingRepository.findById(key)
            .orElseGet(() -> new AppSetting(key, value));
        setting.setBoolValue(value);
        appSettingRepository.save(setting);
    }
}
