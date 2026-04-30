package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import io.jsonwebtoken.security.SecurityException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void adminMethods_delegateToRepository() {
        Authentication adminAuth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User admin = ServiceTestSupport.user(9L, "admin", UserType.ADMIN);
        User owner = ServiceTestSupport.user(1L, "anna", UserType.OWNER);

        when(userRepository.save(owner)).thenReturn(owner);
        when(userRepository.findAll()).thenReturn(List.of(owner));
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(owner));
        when(userRepository.findByUsername("anna")).thenReturn(owner);
        when(userRepository.findByUserLnameAndUserFname("Last", "First")).thenReturn(owner);
        when(userRepository.findByOwnedHorsesHorseName("Csillag")).thenReturn(owner);

        assertSame(owner, userService.saveUser(owner));
        assertEquals(List.of(owner), userService.getAllUsers());
        assertEquals(java.util.Optional.of(owner), userService.getUserById(1L, adminAuth));
        assertSame(owner, userService.getUserByUsername("anna", adminAuth));
        assertSame(owner, userService.getUserByFullName("Last", "First"));
        assertSame(owner, userService.getUserByHorseName("Csillag"));

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void updateUser_forOwnProfileEncodesPassword() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User currentUser = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User updatedUser = ServiceTestSupport.user(1L, "anna.updated", UserType.OWNER);
        updatedUser.setPassword("new-secret");
        updatedUser.setEmail("new@example.com");

        when(userRepository.findByUsername("anna")).thenReturn(currentUser);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(currentUser));
        when(passwordEncoder.encode("new-secret")).thenReturn("encoded-secret");
        when(userRepository.save(currentUser)).thenReturn(currentUser);

        User result = userService.updateUser(1L, updatedUser, auth);

        assertEquals("anna.updated", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("encoded-secret", result.getPassword());
    }

    @Test
    void getUserById_returnsOwnUserForNonAdmin() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User currentUser = ServiceTestSupport.user(1L, "anna", UserType.OWNER);

        when(userRepository.findByUsername("anna")).thenReturn(currentUser);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(currentUser));

        assertEquals(java.util.Optional.of(currentUser), userService.getUserById(1L, auth));
    }

    @Test
    void getUserByUsername_returnsOwnUser() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User currentUser = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        when(userRepository.findByUsername("anna")).thenReturn(currentUser);

        assertSame(currentUser, userService.getUserByUsername("anna", auth));
    }

    @Test
    void getUserByUsername_throwsWhenNonAdminRequestsAnotherUser() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");

        SecurityException exception = assertThrows(SecurityException.class,
            () -> userService.getUserByUsername("bela", auth));

        assertEquals("Nincs jogosultsága.", exception.getMessage());
    }

    @Test
    void getUserById_throwsWhenNonAdminRequestsAnotherUser() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User currentUser = ServiceTestSupport.user(1L, "anna", UserType.OWNER);

        when(userRepository.findByUsername("anna")).thenReturn(currentUser);

        SecurityException exception = assertThrows(SecurityException.class,
            () -> userService.getUserById(2L, auth));

        assertEquals("Nincs jogosultsága.", exception.getMessage());
    }

    @Test
    void updateUser_throwsWhenEmailAlreadyUsedByAnotherUser() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User currentUser = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User updatedUser = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User existingByEmail = ServiceTestSupport.user(2L, "bela", UserType.OWNER);
        updatedUser.setEmail("used@example.com");
        existingByEmail.setEmail("used@example.com");

        when(userRepository.findByUsername("anna")).thenReturn(currentUser);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(currentUser));
        when(userRepository.findByEmail("used@example.com")).thenReturn(existingByEmail);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.updateUser(1L, updatedUser, auth));

        assertEquals("Ez az email cím már használatban van.", exception.getMessage());
    }

    @Test
    void updateUser_throwsWhenNonAdminModifiesAnotherProfile() {
        Authentication auth = ServiceTestSupport.auth("anna", "ROLE_OWNER");
        User currentUser = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User updatedUser = ServiceTestSupport.user(2L, "bela", UserType.OWNER);

        when(userRepository.findByUsername("anna")).thenReturn(currentUser);

        SecurityException exception = assertThrows(SecurityException.class,
            () -> userService.updateUser(2L, updatedUser, auth));

        assertEquals("Csak a saját profilodat módosíthatod!", exception.getMessage());
    }

    @Test
    void updateUser_asAdminCanChangeRoleWithoutEncodingPassword() {
        Authentication auth = ServiceTestSupport.auth("admin", "ROLE_ADMIN");
        User currentUser = ServiceTestSupport.user(9L, "admin", UserType.ADMIN);
        User target = ServiceTestSupport.user(1L, "anna", UserType.OWNER);
        User updatedUser = ServiceTestSupport.user(1L, "anna", UserType.EMPLOYEE);
        updatedUser.setPassword(null);

        when(userRepository.findByUsername("admin")).thenReturn(currentUser);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(target));
        when(userRepository.save(target)).thenReturn(target);

        User result = userService.updateUser(1L, updatedUser, auth);

        assertSame(UserType.EMPLOYEE, result.getUserType());
    }
}
