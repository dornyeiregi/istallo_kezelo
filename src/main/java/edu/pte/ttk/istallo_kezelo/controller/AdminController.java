package edu.pte.ttk.istallo_kezelo.controller;

import edu.pte.ttk.istallo_kezelo.dto.SignupRequestDTO;
import edu.pte.ttk.istallo_kezelo.dto.UserDTO;
import edu.pte.ttk.istallo_kezelo.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Adminisztrátori felhasználókezelési végpontok vezérlője.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * Létrehozza a vezérlőt a szükséges szolgáltatással.
     *
     * @param adminService admin felhasználókezelési szolgáltatás
     */
    public AdminController(AdminService adminService) { this.adminService = adminService; }

    /**
     * Új felhasználót hoz létre admin jogosultsággal.
     *
     * @param dto regisztrációs adatok
     * @return státusz üzenet
     */
    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody SignupRequestDTO dto) {
        adminService.createUser(dto);
        return ResponseEntity.ok("Felhasználó sikeresen létrehozva.");
    }

    /**
     * Visszaadja az összes felhasználót.
     *
     * @return felhasználók listája
     */
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Frissíti a felhasználó típusát.
     *
     * @param id   felhasználó azonosító
     * @param body kérés törzs (userType mezővel)
     * @return státusz üzenet
     */
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

    /**
     * Törli a megadott felhasználót.
     *
     * @param id felhasználó azonosító
     * @return státusz üzenet
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("Felhasználó sikeresen törölve.");
    }
}
