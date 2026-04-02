package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import edu.pte.ttk.istallo_kezelo.service.StorageAlertService;

/**
 * REST kontroller teszt e-mail küldéshez.
 */
@RestController
@RequestMapping("/api/mail")
public class MailController {

    private final StorageAlertService storageAlertService;

    public MailController(StorageAlertService storageAlertService) {
        this.storageAlertService = storageAlertService;
    }

    @GetMapping("/test")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> sendTest(@RequestParam(value = "to", required = false) String to) {
        storageAlertService.sendTestMail(to);
        return ResponseEntity.ok("Teszt e-mail elküldve.");
    }
}
