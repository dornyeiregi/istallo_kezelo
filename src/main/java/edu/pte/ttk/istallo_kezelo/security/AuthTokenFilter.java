package edu.pte.ttk.istallo_kezelo.security;

import edu.pte.ttk.istallo_kezelo.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getServletPath();

        if (path.startsWith("/api/auth/") || path.startsWith("/api/test/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                // Felhasználónév és szerepkörök kinyerése a tokenből
                String username = jwtUtils.getUsernameFromToken(jwt);
                List<String> roles = jwtUtils.getRolesFromToken(jwt);

                System.out.println("JWT érvényes: " + username + " (" + roles + ")");

                // Felhasználó betöltése az adatbázisból
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // A tokenben lévő szerepkörök Spring Security formátumra alakítása
                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Authentication objektum beállítása a SecurityContext-be
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("Felhasználó hitelesítve: " + username + ", jogosultságok: " + authorities);
            } else {
                System.out.println("Érvénytelen vagy hiányzó JWT token.");
            }

        } catch (Exception e) {
            System.out.println("Hiba a felhasználó hitelesítése közben: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // JWT token kinyerése az Authorization fejlécből
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
