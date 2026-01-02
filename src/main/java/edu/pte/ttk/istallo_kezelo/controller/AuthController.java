package edu.pte.ttk.istallo_kezelo.controller;

import edu.pte.ttk.istallo_kezelo.dto.SignupRequestDTO;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import edu.pte.ttk.istallo_kezelo.security.JwtUtil;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtils;
    
    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);

        User dbUser = userRepository.findByUsername(userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", Map.of(
            "username", dbUser.getUsername(),
            "userType", dbUser.getUserType().name()
        ));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody SignupRequestDTO request) {
        System.out.println(request);
        if (request.getUsername() == null || request.getUsername().isBlank()
            || request.getPassword() == null || request.getPassword().isBlank()
            || request.getEmail() == null || request.getEmail().isBlank()
            || request.getLName() == null || request.getLName().isBlank()
            || request.getFName() == null || request.getFName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Hiányzó kötelező mezők a regisztrációhoz."));
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "A felhasználónév már foglalt."));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Az e-mail cím már használatban van."));
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setUserLname(request.getLName());
        newUser.setUserFname(request.getFName());
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhone());
        newUser.setUserType(request.getUserType() != null ? request.getUserType() : UserType.OWNER);
        newUser.setPassword(encoder.encode(request.getPassword()));

        userRepository.save(newUser);

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", userDetails.getUsername());
        userPayload.put("userType", newUser.getUserType().name());
        userPayload.put("email", newUser.getEmail());
        userPayload.put("phone", newUser.getPhone());
        response.put("user", userPayload);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
