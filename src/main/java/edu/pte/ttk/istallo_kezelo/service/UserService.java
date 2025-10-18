package edu.pte.ttk.istallo_kezelo.service;

import org.springframework.stereotype.Service;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import edu.pte.ttk.istallo_kezelo.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository; 
    }

    // Új felhasználó mentése
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Összes felhasználó lekérdezése
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Felhasználó lekérdezése id alapján
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Felhasználó lekérdezése felhasználónév alapján
    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    // Felhasználó lekérdezése név alapján
    public User getUserByFullName(String lName, String fName){
        return userRepository.findByUserLnameAndUserFname(lName, fName);
    }

    // Felhasználó lekérdezése ló neve alapján
    public User getUserByHorseName(String horsename){
        return userRepository.findByOwnedHorsesHorseName(horsename);
    }

    // Felhasználó frissítése
    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setUsername(updatedUser.getUsername());
            user.setUserLname(updatedUser.getUserLname());
            user.setUserFname(updatedUser.getUserFname());
            user.setEmail(updatedUser.getEmail());
            user.setPhone(updatedUser.getPhone());
            user.setUserType(updatedUser.getUserType());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("Felhasználó nem található.");
        }
    }

    // Felhasználó törlése
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
