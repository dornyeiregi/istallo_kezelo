package edu.pte.ttk.istallo_kezelo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Test class for CustomUserDetailsService behavior.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_returnsSpringSecurityUser() {
        User user = ServiceTestSupport.user(1L, "admin", UserType.ADMIN);
        user.setPassword("encoded-secret");
        when(userRepository.findByUsername("admin")).thenReturn(user);

        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        assertEquals("admin", result.getUsername());
        assertEquals("encoded-secret", result.getPassword());
        assertEquals("ROLE_ADMIN", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_throwsWhenUserMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("missing"));
    }
}
