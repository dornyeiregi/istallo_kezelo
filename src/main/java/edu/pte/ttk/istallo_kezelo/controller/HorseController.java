package edu.pte.ttk.istallo_kezelo.controller;

import edu.pte.ttk.istallo_kezelo.dto.HorseDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.service.HorseService;
import edu.pte.ttk.istallo_kezelo.service.StableService;
import edu.pte.ttk.istallo_kezelo.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/horses")
public class HorseController {

    private final HorseService horseService;
    private final UserService userService;
    private final StableService stableService;

    public HorseController(HorseService horseService, UserService userService, StableService stableService) {
        this.horseService = horseService;
        this.userService = userService;
        this.stableService = stableService;
    }

    // Új ló létrehozása
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public HorseDTO createHorse(@RequestBody HorseDTO dto, Authentication auth) {
        User owner = userService.getUserById(dto.getOwnerId(), auth)
            .orElseThrow(() -> new RuntimeException("Felhasználó nem található."));

        Stable stable = null;
        if (dto.getStableName() != null && !dto.getStableName().isBlank()) {
            stable = stableService.getStableByName(dto.getStableName());
            if (stable == null) {
                throw new RuntimeException("Istálló nem található név alapján: " + dto.getStableName());
            }
        } else {
            throw new RuntimeException("Az istálló mező kitöltése kötelező.");
        }

        Horse horse = new Horse();
        horse.setHorseName(dto.getHorseName());
        horse.setDob(dto.getDob());
        horse.setSex(dto.getSex());
        horse.setOwner(owner);
        horse.setStable(stable);
        horse.setMicrochipNum(dto.getMicrochipNum());
        horse.setPassportNum(dto.getPassportNum());
        horse.setAdditional(dto.getAdditional());

        Horse savedHorse = horseService.saveHorse(horse);
        return toDTO(savedHorse);
    }

    // Összes ló lekérdezése
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
    public List<HorseDTO> getAllHorses(Authentication auth) {
        return horseService.getAllHorses(auth).stream().map(this::toDTO).toList();
    }

    // Ló lekérdezése id alapján
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_OWNER') or hasAuthority('ROLE_EMPLOYEE')")
    public HorseDTO getHorseById(@PathVariable Long id, Authentication auth) {
        Horse horse = horseService.getHorseById(id, auth)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));

        if (hasRole(auth, "OWNER") && !horse.getOwner().getUsername().equals(auth.getName())) {
            throw new RuntimeException("Nincs jogosultsága más lovait megtekinteni.");
        }

        return toDTO(horse);
    }

    // Ló lekérdezése név alapján
    @GetMapping("/byName/{horseName}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_OWNER') or hasAuthority('ROLE_EMPLOYEE')")
    public HorseDTO getHorseByName (@PathVariable String horseName, Authentication auth) {
        Horse horse = horseService.getHorseByName(horseName, auth);
        if (horse == null) {
            throw new RuntimeException("Ló nem található.");
        }
        return toDTO(horse);
    }

    // Ló frissítése
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_OWNER')")
    public HorseDTO updateHorsePartially(@PathVariable Long id, @RequestBody HorseDTO dto, Authentication auth){
        Horse existingHorse = horseService.getHorseById(id, auth)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));

        if (hasRole(auth, "OWNER") && !existingHorse.getOwner().getUsername().equals(auth.getName())) {
            throw new RuntimeException("Csak a saját lovakat lehet szerkeszteni.");
        }
        if (dto.getHorseName() != null) {existingHorse.setHorseName(dto.getHorseName());}
        if(dto.getDob() != null) {existingHorse.setDob(dto.getDob());}
        if(dto.getSex() != null) {existingHorse.setSex(dto.getSex());}
        if(dto.getMicrochipNum() != null) {existingHorse.setMicrochipNum(dto.getMicrochipNum());}
        if(dto.getPassportNum() != null) {existingHorse.setPassportNum(dto.getPassportNum());}
        if(dto.getAdditional() != null) {existingHorse.setAdditional(dto.getAdditional());}

        if (dto.getOwnerId() != null) {
            User newOwner = userService.getUserById(dto.getOwnerId(), auth)
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található."));
            existingHorse.setOwner(newOwner);
        }

        if (dto.getStableId() != null) {
            Stable newStable = stableService.getStableById(dto.getStableId())
                .orElseThrow(() -> new RuntimeException("Istálló nem található."));
            existingHorse.setStable(newStable);
        }

        Horse savedHorse = horseService.saveHorse(existingHorse);

        return toDTO(savedHorse);
    }

    // Ló törlése
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteHorse(@PathVariable Long id) {
        horseService.deleteHorseById(id);
        return ResponseEntity.ok(Map.of("message", "Ló sikeresen törölve."));
    }



    /**
     * Más service-ekhez tartozó lekérdezések
     */


    // Felhasználó saját lovainak lekérése
    @GetMapping("/mine")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_OWNER')")
    public List<HorseDTO> getMyHorses(Authentication auth) {
        return horseService.getAllHorses(auth).stream()
            .filter(h -> h.getOwner().getUsername().equals(auth.getName()))
            .map(this::toDTO).toList();
    }
    

    // Felhasználó összes lovának lekérdezése felhasználó id alapján
    @GetMapping("/byOwnerId/{ownerId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<HorseDTO> getHorsesByOwner(@PathVariable Long ownerId, Authentication auth) {
        if (userService.getUserById(ownerId, auth) == null) {
            throw new RuntimeException("Felhasználó nem található.");
        } else {
            return horseService.getAllHorses(auth).stream()
                .filter(horse -> horse.getOwner().getId().equals(ownerId))
                .map(this::toDTO).toList();
        }
    }

    // Felhasználó összes lovának lekérdezése felhasználó neve alapján
    @GetMapping("/byOwnerName/{lName}/{fName}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<HorseDTO> getHorsesByOwnerName(@PathVariable String lName, @PathVariable String fName, Authentication auth) {
        User owner = userService.getUserByFullName(lName, fName);
        if (owner == null) {
            throw new RuntimeException("Felhasználó nem található.");
        }
        return horseService.getAllHorses(auth).stream()
            .filter(horse -> horse.getOwner().getId().equals(owner.getId()))
            .map(this::toDTO).toList();
    }

    // Felhasználó összes lovának lekérdezése felhasználónév alapján
    @GetMapping("/byUsername/{username}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<HorseDTO> getHorsesByUsername(@PathVariable String username, Authentication auth) {
        User owner = userService.getUserByUsername(username, auth);
        if (owner == null) {
            throw new RuntimeException("Felhasználó nem található.");
        }
        return horseService.getAllHorses(auth).stream()
            .filter(horse -> horse.getOwner().getUsername().equals(username))
            .map(this::toDTO).toList();
    }
    

    // Istállóban lévő összes ló lekérdezése istálló id alapján
    @GetMapping("/byStableId/{stableId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<HorseDTO> getHorsesByStableId(@PathVariable Long stableId, Authentication auth){
        if (stableService.getStableById(stableId) == null) {
            throw new RuntimeException("Istálló nem található.");
        } else {
            return horseService.getAllHorses(auth).stream()
                .filter(horse -> horse.getStable().getStableId().equals(stableId))
                .map(this::toDTO).toList();
        }
    }

    // Istállóban lévő összes ló lekérdezése istálló név alapján
    @GetMapping("/byStableName/{stableName}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<HorseDTO> getHorsesByStableName(@PathVariable String stableName, Authentication auth) {
        Stable stable = stableService.getStableByName(stableName);
        if(stable == null){
            throw new RuntimeException("Istálló nem található.");
        }
        return horseService.getAllHorses(auth).stream()
            .filter(horse -> horse.getStable().getStableName().equals(stable.getStableName()))
            .map(this::toDTO).toList();
    }



    private HorseDTO toDTO(Horse horse){
        HorseDTO dto = new HorseDTO();
        dto.setHorseId(horse.getId());
        dto.setHorseName(horse.getHorseName());
        dto.setDob(horse.getDob());
        dto.setSex(horse.getSex());
        dto.setOwnerName(horse.getOwner().getUserLname() + " "
            + horse.getOwner().getUserFname());
        dto.setOwnerId(horse.getOwner().getId());
        dto.setStableName(horse.getStable().getStableName());
        dto.setStableId(horse.getStable().getStableId());
        dto.setMicrochipNum(horse.getMicrochipNum());
        dto.setPassportNum(horse.getPassportNum());
        dto.setAdditional(horse.getAdditional());
        return dto;
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

}
