package edu.pte.ttk.istallo_kezelo.service;

import edu.pte.ttk.istallo_kezelo.dto.UserDTO;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.UserType;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::toDTO).toList();
    }

    public User updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Felhasználó nem található"));

        try {
            UserType newType = UserType.valueOf(newRole.trim().toUpperCase());

            // Bejelentkezett felhasználó lekérése
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

            // biztonsági szabály: admin ne tudja saját magát lefokozni
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

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setUserType(user.getUserType());
        return dto;
    }
}
