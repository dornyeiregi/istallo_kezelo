package edu.pte.ttk.istallo_kezelo.controller;

import edu.pte.ttk.istallo_kezelo.dto.SignupRequestDTO;
import edu.pte.ttk.istallo_kezelo.dto.UserDTO;
import edu.pte.ttk.istallo_kezelo.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) { this.adminService = adminService; }

    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody SignupRequestDTO dto) {
        adminService.createUser(dto);
        return ResponseEntity.ok("Felhasználó sikeresen létrehozva.");
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/update-role/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        System.out.println("FRONTEND BODY: " + body);
        String newRole = body.get("userType");
        if (newRole == null || newRole.isBlank()) {
            return ResponseEntity.badRequest().body("Hiányzó role érték");
        }
        adminService.updateUserRole(id, newRole);
        return ResponseEntity.ok("Felhasználó típusa sikeresen frissítve.");
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("Felhasználó sikeresen törölve.");
    }
}
