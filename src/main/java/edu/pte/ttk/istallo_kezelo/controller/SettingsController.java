package edu.pte.ttk.istallo_kezelo.controller;

import edu.pte.ttk.istallo_kezelo.dto.EmployeeAccessSettingsDTO;
import edu.pte.ttk.istallo_kezelo.service.SettingsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/employee-access")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public EmployeeAccessSettingsDTO getEmployeeAccessSettings() {
        return settingsService.getEmployeeAccessSettings();
    }

    @PatchMapping("/employee-access")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public EmployeeAccessSettingsDTO updateEmployeeAccessSettings(@RequestBody EmployeeAccessSettingsDTO dto) {
        return settingsService.updateEmployeeAccessSettings(dto);
    }
}
