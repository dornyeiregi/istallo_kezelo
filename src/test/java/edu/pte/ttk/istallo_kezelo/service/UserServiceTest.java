package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
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
}
