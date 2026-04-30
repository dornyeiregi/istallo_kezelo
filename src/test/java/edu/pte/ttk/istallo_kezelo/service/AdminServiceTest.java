package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.SignupRequestDTO;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllUsers_mapsAllUsers() {
        User user = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        when(userRepository.findAll()).thenReturn(List.of(user));

        assertEquals(1, adminService.getAllUsers().size());
    }

    @Test
    void createUser_encodesPasswordAndSavesUser() {
        SignupRequestDTO request = new SignupRequestDTO(
            "anna",
            "Nagy",
            "Anna",
            "anna@example.com",
            "123",
            UserType.OWNER,
            "secret"
        );
        when(userRepository.existsByUsername("anna")).thenReturn(false);
        when(userRepository.existsByEmail("anna@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = adminService.createUser(request);

        assertEquals("anna", result.getUsername());
        assertEquals("encoded-secret", result.getPassword());
        assertSame(UserType.OWNER, result.getUserType());
    }

    @Test
    void createUser_throwsWhenUsernameTaken() {
        SignupRequestDTO request = new SignupRequestDTO("anna", "Nagy", "Anna", "anna@example.com", "123", UserType.OWNER, "secret");
        when(userRepository.existsByUsername("anna")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> adminService.createUser(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void updateUserRole_updatesWhenRoleChanges() {
        User user = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("admin", "n/a")
        );
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = adminService.updateUserRole(1L, "employee");

        assertSame(UserType.EMPLOYEE, result.getUserType());
    }

    @Test
    void updateUserRole_doesNotSaveWhenRoleUnchanged() {
        User user = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("admin", "n/a")
        );
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        User result = adminService.updateUserRole(1L, "owner");

        assertSame(UserType.OWNER, result.getUserType());
        verify(userRepository, never()).save(user);
    }

    @Test
    void deleteUser_throwsWhenDeletingSelf() {
        User current = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("admin", "n/a")
        );
        when(userRepository.findByUsername("admin")).thenReturn(current);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> adminService.deleteUser(1L));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void deleteUser_deletesWhenTargetIsDifferentUser() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("admin", "n/a")
        );
        when(userRepository.findByUsername("admin")).thenReturn(ServiceTestSupport.user(1L, "admin", UserType.ADMIN));

        adminService.deleteUser(2L);

        verify(userRepository).deleteById(2L);
    }
}
