package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.SignupRequestDTO;
import edu.pte.ttk.istallo_kezelo.dto.UserDTO;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.service.AdminService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void createUser_returnsOk() {
        SignupRequestDTO dto = new SignupRequestDTO("anna", "Nagy", "Anna", "anna@example.com", "123", UserType.OWNER, "secret");

        var response = adminController.createUser(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Felhasználó sikeresen létrehozva.", response.getBody());
        verify(adminService).createUser(dto);
    }

    @Test
    void getAllUsers_returnsUsers() {
        UserDTO user = new UserDTO("anna", "Nagy", "Anna", "anna@example.com", "123", UserType.OWNER, 1L);
        when(adminService.getAllUsers()).thenReturn(List.of(user));

        var response = adminController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(user), response.getBody());
    }

    @Test
    void updateUserRole_returnsBadRequestWhenRoleMissing() {
        var response = adminController.updateUserRole(1L, Map.of());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Hiányzó role érték", response.getBody());
    }

    @Test
    void updateUserRole_returnsOkWhenRolePresent() {
        var response = adminController.updateUserRole(2L, Map.of("userType", "ADMIN"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Felhasználó típusa sikeresen frissítve.", response.getBody());
        verify(adminService).updateUserRole(2L, "ADMIN");
    }

    @Test
    void deleteUser_returnsOk() {
        var response = adminController.deleteUser(3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Felhasználó sikeresen törölve.", response.getBody());
        verify(adminService).deleteUser(3L);
    }
}
