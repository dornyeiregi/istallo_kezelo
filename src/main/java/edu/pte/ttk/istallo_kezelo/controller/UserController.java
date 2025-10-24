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
        user.setUsername(dto.getUsername());
        user.setUserLname(dto.getUserLname());
        user.setUserFname(dto.getUserFname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setUserType(dto.getUserType());

        User savedUser = userService.saveUser(user);

        return toDTO(savedUser);
    }
    
    // Összes felhasználó lekérdezése       
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream().map(this::toDTO).toList();
    }
    

    // Felhasználó lekérdezése id alapján
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id){
        User user = userService.getUserById(id)
            .orElseThrow(() -> new RuntimeException("Felhasználó nem található."));
        return toDTO(user);
    }

    
    // Felhasználó lekérdezése felhasználónév alapján
    @GetMapping("/byUsername/{username}")
    public UserDTO getUserByUsername(@PathVariable String username){
        User user = userService.getUserByUsername(username);
        if (user == null){
            throw new RuntimeException("Felhasználó nem található.");
        }
        return toDTO(user);
    }

    // Felhasználó lekérdezése név alapján
    @GetMapping("/byFullName/{lName}/{fName}")
    public UserDTO getUserByFullName(@PathVariable String lName, @PathVariable String fName) {
        User user = userService.getUserByFullName(lName, fName);
        if (user == null) {
            throw new RuntimeException("Felhasználó nem található.");
        }
        return toDTO(user);
    }
    
    // Felhasználó lekérdezése ló neve alapján
    @GetMapping("/byHorseName/{horseName}")
    public UserDTO getUserbyHorseName(@PathVariable String horseName){
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

        if (dto.getUserType() != null) {existingUser.setUserType(dto.getUserType());};
        if (dto.getUserLname() != null) { existingUser.setUserLname(dto.getUserLname());};
        if (dto.getUserFname() != null) {existingUser.setUserFname(dto.getUserFname());};
        if (dto.getEmail() != null) { existingUser.setEmail(dto.getEmail());}
        if (dto.getPhone() != null) { existingUser.setPhone(dto.getPhone());};
        if (dto.getUsername() != null) {existingUser.setUsername(dto.getUsername());};

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
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setUserLname(user.getUserLname());
        dto.setUserFname(user.getUserFname());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setUserType(user.getUserType());
        return dto;
    }

}
