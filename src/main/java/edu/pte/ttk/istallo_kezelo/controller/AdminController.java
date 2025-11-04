package edu.pte.ttk.istallo_kezelo.controller;

import edu.pte.ttk.istallo_kezelo.dto.UserDTO;
import edu.pte.ttk.istallo_kezelo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Összes felhasználó listázása (csak admin láthatja)
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }


    // Meglévő user szerepének módosítása
    @PatchMapping("/update-role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        System.out.println("FRONTEND BODY: " + body);
        String newRole = body.get("userType");
        if (newRole == null || newRole.isBlank()) {
            return ResponseEntity.badRequest().body("Hiányzó role érték");
        }

        adminService.updateUserRole(id, newRole);
        return ResponseEntity.ok("Felhasználó típusa sikeresen frissítve.");
    }
}
