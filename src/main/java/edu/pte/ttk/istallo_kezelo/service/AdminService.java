package edu.pte.ttk.istallo_kezelo.service;

import edu.pte.ttk.istallo_kezelo.dto.SignupRequestDTO;
import edu.pte.ttk.istallo_kezelo.dto.UserDTO;
import edu.pte.ttk.istallo_kezelo.mapper.UserMapper;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Adminisztrátori felhasználókezelési műveletek szolgáltatása.
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Létrehozza a szolgáltatást a szükséges függőségekkel.
     *
     * @param userRepository felhasználó repository
     * @param passwordEncoder jelszó hash-elő
     */
    public AdminService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Visszaadja az összes felhasználót DTO-ként.
     *
     * @return felhasználók listája
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserMapper::toDTO).toList();
    }

    /**
     * Módosítja a felhasználó típusát.
     *
     * @param userId  felhasználó azonosító
     * @param newRole új szerepkód
     * @return frissített felhasználó
     */
    public User updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Felhasználó nem található"));
        try {
            UserType newType = UserType.valueOf(newRole.trim().toUpperCase());
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            if (user.getUsername().equals(currentUsername)
                    && user.getUserType() == UserType.ADMIN
                    && newType != UserType.ADMIN) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Saját magadat nem fokozhatod le.");
            }
            if (user.getUserType() != newType) {
                user.setUserType(newType);
                userRepository.save(user);
            }
            return user;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Érvénytelen role típus");
        }
    }

    /**
     * Törli a felhasználót az azonosító alapján.
     *
     * @param id felhasználó azonosító
     */
    public void deleteUser(Long id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername);
        if (currentUser != null && currentUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Saját magadat nem törölheted.");
        }
        userRepository.deleteById(id);
    }

    /**
     * Létrehoz egy új felhasználót a regisztrációs adatokból.
     *
     * @param dto regisztrációs adatok
     * @return mentett felhasználó
     */
    public User createUser(SignupRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Felhasználónév már foglalt");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail már használatban van");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setUserLname(dto.getLName());
        user.setUserFname(dto.getFName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setUserType(dto.getUserType());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

}
