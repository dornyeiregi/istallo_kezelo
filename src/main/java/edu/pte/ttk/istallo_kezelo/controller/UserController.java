package edu.pte.ttk.istallo_kezelo.controller;

import org.springframework.web.bind.annotation.*;

import edu.pte.ttk.istallo_kezelo.dto.UserDTO;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.service.UserService;
import org.springframework.http.ResponseEntity;

import java.util.List;




@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    
    // Új felhasználó létrehozása
    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO dto) {
        User user = new User();
        user.setUsername(dto.username);
        user.setUserLname(dto.userLname);
        user.setUserFname(dto.userFname);
        user.setEmail(dto.email);
        user.setPhone(dto.phone);
        user.setUserType(dto.userType);

        User savedUser = userService.saveUser(user);

        return toDTO(savedUser);
    }
    
    // Összes felhasználó lekérdezése       
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream()
        .map(this::toDTO).toList();
    }
    

    // Felhasználó lekérdezése id alapján
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id){
        User user = userService.getUserById(id)
            .orElseThrow(() -> new RuntimeException("Felhasználó nem található."));
        return toDTO(user);
    }

    
    // Felhasználó lekérdezése felhasználónév alapján
    @GetMapping("/username")
    public UserDTO getUserByUsername(@RequestParam String username){
        User user = userService.getUserByUsername(username);
        if (user == null){
            throw new RuntimeException("Felhasználó nem található.");
        }
        return toDTO(user);
    }

    // Felhasználó lekérdezése név alapján
    @GetMapping("/fullName")
    public UserDTO getUserByFullName(@RequestParam String lName, String fName) {
        User user = userService.getUserByFullName(lName, fName);
        if (user == null) {
            throw new RuntimeException("Felhasználó nem található.");
        }
        return toDTO(user);
    }
    
    // Felhasználó lekérdezése ló neve alapján
    @GetMapping("/horseName")
    public UserDTO getUserbyHorseName(@RequestParam String horseName){
        User user = userService.getUserByHorseName(horseName);
        if (user == null) {
            throw new RuntimeException("Felhasználó nem található.");
        }
        return toDTO(user);
    }
    
    /*
    // Ló hozzáadása a felhasználóhoz
    @PostMapping("/{userId}/addHorse")
    public HorseDTO addHorseToUser(@PathVariable Long userId, @RequestBody HorseDTO dto){
        User owner = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Felhasználó nem található."));

        Horse horse = new Horse();
        horse.setHorseName(dto.horseName);
        horse.setDob(dto.dob);
        horse.setSex(dto.sex);
        horse.setPassportNum(dto.passportNum);
        horse.setMicrochipNum(dto.microchipNum);
        horse.setAdditional(dto.additional);
        horse.setOwner(owner);

        if (dto.stableId != null) {
            Stable stable = stableService.getStableById(dto.stableId)
                .orElseThrow(() -> new RuntimeException("Istálló nem található."));
            horse.setStable(stable);
        }

        Horse savedHorse = horseService.saveHorse(horse);
        
        HorseDTO addedHorse = new HorseDTO();
        addedHorse.horseName = savedHorse.getHorseName();
        addedHorse.dob = savedHorse.getDob();
        addedHorse.sex = savedHorse.getSex();
        addedHorse.passportNum = savedHorse.getPassportNum();
        addedHorse.microchipNum = savedHorse.getMicrochipNum();
        addedHorse.additional = savedHorse.getAdditional();
        addedHorse.ownerId = savedHorse.getOwner().getId();
        addedHorse.ownerName = savedHorse.getOwner().getUserLname() + " "
            + savedHorse.getOwner().getUserFname();
        if (savedHorse.getStable() != null) {
            addedHorse.stableId = savedHorse.getStable().getStableId();
            addedHorse.stableName = savedHorse.getStable().getStableName();
        }
        
        return addedHorse;
    }
        */

    // Felhasználó frissítése
    @PatchMapping("/{id}")
    public UserDTO updateUserPartially(@PathVariable Long id, @RequestBody UserDTO dto){
        User existingUser = userService.getUserById(id)
            .orElseThrow(() -> new RuntimeException("Felhasználó nem található."));

        if (dto.userType != null) {existingUser.setUserType(dto.userType);};
        if (dto.userLname != null) { existingUser.setUserLname(dto.userLname);};
        if (dto.userFname != null) {existingUser.setUserFname(dto.userFname);};
        if (dto.email != null) { existingUser.setEmail(dto.email);}
        if (dto.phone != null) { existingUser.setPhone(dto.phone);};
        if (dto.username != null) {existingUser.setUsername(dto.username);};

        User savedUser = userService.saveUser(existingUser);
        return toDTO(savedUser);
    }

    // Felhasználó törlése
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Felhasználó sikeresen törölve.");
    }


    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.userLname = user.getUserLname();
        dto.userFname = user.getUserFname();
        dto.email = user.getEmail();
        dto.phone = user.getPhone();
        dto.userType = user.getUserType();
        return dto;
    }

}
