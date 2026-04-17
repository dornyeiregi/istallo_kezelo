package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import edu.pte.ttk.istallo_kezelo.dto.SignupRequestDTO;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test class for AdminService behavior.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

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
}
