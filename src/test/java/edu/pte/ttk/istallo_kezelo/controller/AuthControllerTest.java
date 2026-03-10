package edu.pte.ttk.istallo_kezelo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.pte.ttk.istallo_kezelo.dto.ChangePasswordRequest;
import edu.pte.ttk.istallo_kezelo.dto.SignupRequestDTO;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import edu.pte.ttk.istallo_kezelo.security.JwtUtil;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    void authenticateUser_returnsTokenAndUser() {
        User request = new User();
        request.setUsername("anna");
        request.setPassword("secret");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername("anna")
            .password("encoded")
            .authorities("ROLE_OWNER")
            .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        User dbUser = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");
        when(userRepository.findByUsername("anna")).thenReturn(dbUser);

        var response = authController.authenticateUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().get("token"));
        Object userPayload = response.getBody().get("user");
        assertInstanceOf(Map.class, userPayload);
        assertEquals("anna", ((Map<?, ?>) userPayload).get("username"));
        assertEquals("OWNER", ((Map<?, ?>) userPayload).get("userType"));
    }

    @Test
    void registerUser_returnsCreated() {
        SignupRequestDTO request = new SignupRequestDTO("anna", "Nagy", "Anna", "anna@example.com", "123", null, "secret");
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername("anna")
            .password("encoded")
            .authorities("ROLE_OWNER")
            .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );

        when(userRepository.existsByUsername("anna")).thenReturn(false);
        when(userRepository.existsByEmail("anna@example.com")).thenReturn(false);
        when(encoder.encode("secret")).thenReturn("encoded-secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        var response = authController.registerUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().get("token"));
        assertEquals("anna@example.com", ((Map<?, ?>) response.getBody().get("user")).get("email"));
        assertEquals("OWNER", ((Map<?, ?>) response.getBody().get("user")).get("userType"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded-secret", userCaptor.getValue().getPassword());
        assertEquals(UserType.OWNER, userCaptor.getValue().getUserType());
    }

    @Test
    void registerUser_returnsConflictWhenUsernameTaken() {
        SignupRequestDTO request = new SignupRequestDTO("anna", "Nagy", "Anna", "anna@example.com", "123", UserType.OWNER, "secret");
        when(userRepository.existsByUsername("anna")).thenReturn(true);

        var response = authController.registerUser(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("A felhasználónév már foglalt.", response.getBody().get("message"));
    }

    @Test
    void changePassword_returnsUnauthorizedWithoutAuthentication() {
        ChangePasswordRequest request = new ChangePasswordRequest("old", "new");

        var response = authController.changePassword(request, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Nincs hitelesítve.", response.getBody().get("message"));
    }

    @Test
    void changePassword_updatesPasswordWhenCurrentMatches() {
        ChangePasswordRequest request = new ChangePasswordRequest("old", "new");
        Authentication authentication = ControllerTestSupport.auth("anna", "ROLE_OWNER");
        User user = ControllerTestSupport.user(1L, "anna", "Nagy", "Anna");
        user.setPassword("encoded-old");

        when(userRepository.findByUsername("anna")).thenReturn(user);
        when(encoder.matches("old", "encoded-old")).thenReturn(true);
        when(encoder.encode("new")).thenReturn("encoded-new");

        var response = authController.changePassword(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Jelszó sikeresen frissítve.", response.getBody().get("message"));
        assertEquals("encoded-new", user.getPassword());
        verify(userRepository).save(user);
    }
}
