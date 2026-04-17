package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.UserDTO;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

/**
 * Test class for UserController behavior.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_returnsMappedDto() {
        User savedUser = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        when(userService.saveUser(any(User.class))).thenReturn(savedUser);
        UserDTO dto = new UserDTO("anna", "Nagy", "Anna", "anna@example.com", "123", UserType.OWNER, null);

        UserDTO result = userController.createUser(dto);

        assertEquals(1L, result.getUserId());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).saveUser(captor.capture());
        assertEquals("anna@example.com", captor.getValue().getEmail());
    }

    @Test
    void getAllUsers_returnsMappedDtos() {
        when(userService.getAllUsers()).thenReturn(List.of(ControllerTestSupport.user(1L, "anna", "Nagy", "Anna")));

        List<UserDTO> result = userController.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    void getUserById_returnsMappedDto() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        User user = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        when(userService.getUserById(1L, auth)).thenReturn(Optional.of(user));

        UserDTO result = userController.getUserById(1L, auth);

        assertEquals("anna", result.getUsername());
    }

    @Test
    void getUserByUsername_returnsMappedDto() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        User user = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        when(userService.getUserByUsername("anna", auth)).thenReturn(user);

        UserDTO result = userController.getUserByUsername("anna", auth);

        assertEquals(1L, result.getUserId());
    }

    @Test
    void updateUserPartially_returnsUpdatedUser() {
        Authentication auth = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        User user = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        when(userService.getUserById(1L, auth)).thenReturn(Optional.of(user));
        when(userService.updateUser(1L, user, auth)).thenReturn(user);

        UserDTO dto = new UserDTO();
        dto.setEmail("new@example.com");
        dto.setPhone("999");

        UserDTO result = userController.updateUserPartially(1L, dto, auth);

        assertEquals("new@example.com", result.getEmail());
        assertEquals("999", result.getPhone());
    }
}
