package edu.pte.ttk.istallo_kezelo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import io.jsonwebtoken.security.SecurityException;
import edu.pte.ttk.istallo_kezelo.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Új felhasználó mentése
    @PreAuthorize("hasAnyRole('ADMIN')")
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Összes felhasználó lekérdezése
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Felhasználó lekérdezése id alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public Optional<User> getUserById(Long id, Authentication auth) {
        if (isAdmin(auth)) {
            return userRepository.findById(id);
        }

        User currentUser = userRepository.findByUsername(auth.getName());
        if (!currentUser.getId().equals(id)) {
            throw new SecurityException("Nincs jogosultsága.");
        }
        return userRepository.findById(id);
    }

    // Felhasználó lekérdezése felhasználónév alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public User getUserByUsername(String username, Authentication auth){
        if (isAdmin(auth) || auth.getName().equals(username)) {
            return userRepository.findByUsername(username);
        }
        throw new SecurityException("Nincs jogosultsága.");
    }

    // Felhasználó lekérdezése név alapján
    @PreAuthorize("hasAnyRole('ADMIN')")
    public User getUserByFullName(String lName, String fName){
        return userRepository.findByUserLnameAndUserFname(lName, fName);
    }

    // Felhasználó lekérdezése ló neve alapján
    @PreAuthorize("hasAnyRole('ADMIN')")
    public User getUserByHorseName(String horsename){
        return userRepository.findByOwnedHorsesHorseName(horsename);
    }

    // Felhasználó frissítése
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public User updateUser(Long id, User updatedUser, Authentication auth) {
        User currentUser = userRepository.findByUsername(auth.getName());
        boolean isAdmin = isAdmin(auth);

        if (!isAdmin && !currentUser.getId().equals(id)) {
            throw new SecurityException("Csak a saját profilodat módosíthatod!");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Felhasználó nem található."));

        user.setUsername(updatedUser.getUsername());
        user.setUserLname(updatedUser.getUserLname());
        user.setUserFname(updatedUser.getUserFname());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            if (currentUser.getId().equals(id)) {
                String encoded = passwordEncoder.encode(updatedUser.getPassword());
                user.setPassword(encoded);
            } else {
                throw new SecurityException("Más felhasználó jelszavát nem tudod frissíteni.");
            }
        }
        if (isAdmin && updatedUser.getUserType() != null) {
            user.setUserType(updatedUser.getUserType());
        }

        return userRepository.save(user);
    }

    // Felhasználó törlése
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Helper
    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
