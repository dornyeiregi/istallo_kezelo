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

import java.util.List;


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
    public HorseDTO createHorse(@RequestBody HorseDTO dto) {
        User owner = userService.getUserById(dto.ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Stable stable = stableService.getStableById(dto.stableId)
                .orElseThrow(() -> new RuntimeException("Stable not found"));

        Horse horse = new Horse();
        horse.setHorseName(dto.horseName);
        horse.setDob(dto.dob);
        horse.setSex(dto.sex);
        horse.setOwner(owner);
        horse.setStable(stable);
        horse.setMicrochipNum(dto.microchipNum);
        horse.setPassportNum(dto.passportNum);
        horse.setAdditional(dto.additional);

        Horse savedHorse = horseService.saveHorse(horse);

        return toDTO(savedHorse);
    }

    // Összes ló lekérdezése
    @GetMapping
    public List<HorseDTO> getAllHorses() {
        return horseService.getAllHorses().stream()
        .map(this::toDTO).toList();
    }

    // Ló lekérdezése id alapján
    @GetMapping("/{id}")
    public HorseDTO getHorseById(@PathVariable Long id) {
        Horse horse = horseService.getHorseById(id)
            .orElseThrow(() -> new RuntimeException("Horse not found"));
        return toDTO(horse);
    }

    // Ló lekérdezése név alapján
    @GetMapping("/name")
    public HorseDTO getHorseByName (@RequestParam String horseName) {
        Horse horse = horseService.getHorseByName(horseName);
        if (horse == null) {
            throw new RuntimeException("Horse not found");
        }
        return toDTO(horse);
    }

    // Ló frissítése
    @PatchMapping("/{id}")
    public HorseDTO updateHorsePartially(@PathVariable Long id, @RequestBody HorseDTO dto){
        Horse existingHorse = horseService.getHorseById(id)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));

        if (dto.horseName != null) {existingHorse.setHorseName(dto.horseName);}
        if(dto.dob != null) {existingHorse.setDob(dto.dob);}
        if(dto.sex != null) {existingHorse.setSex(dto.sex);}
        if(dto.microchipNum != null) {existingHorse.setMicrochipNum(dto.microchipNum);}
        if(dto.passportNum != null) {existingHorse.setPassportNum(dto.passportNum);}
        if(dto.additional != null) {existingHorse.setAdditional(dto.additional);}

        if (dto.ownerId != null) {
            User newOwner = userService.getUserById(dto.ownerId)
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található."));
            existingHorse.setOwner(newOwner);
        }

        if (dto.stableId != null) {
            Stable newStable = stableService.getStableById(dto.stableId)
                .orElseThrow(() -> new RuntimeException("Istálló nem található."));
            existingHorse.setStable(newStable);
        }

        Horse savedHorse = horseService.saveHorse(existingHorse);

        return toDTO(savedHorse);
    }

    // Ló törlése
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHorse(@PathVariable Long id) {
        horseService.deleteHorseById(id);
        return ResponseEntity.ok("Ló sikeresen törölve.");
    }



    /**
     * Más service-ekhez tartozó lekérdezések
     */

    // Felhasználó összes lovának lekérdezése felhasználó id alapján
    @GetMapping("/owner/{ownerId}")
    public List<HorseDTO> getHorsesByOwner(@PathVariable Long ownerId) {
        if (userService.getUserById(ownerId) == null) {
            throw new RuntimeException("Felhasználó nem található.");
        } else {
            return horseService.getAllHorses().stream()
                .filter(horse -> horse.getOwner().getId().equals(ownerId))
                .map(this::toDTO)
                .toList();
        }
    }

    // Felhasználó összes lovának lekérdezése felhasználó neve alapján
    @GetMapping("/owner")
    public List<HorseDTO> getHorsesByOwnerName(@RequestParam String lName, @RequestParam String fName) {
        User owner = userService.getUserByFullName(lName, fName);
        if (owner == null) {
            throw new RuntimeException("Felhasználó nem található.");
        }
        return horseService.getAllHorses().stream()
            .filter(horse -> horse.getOwner().getId().equals(owner.getId()))
            .map(this::toDTO)
            .toList();
    }

    // Istállóban lévő összes ló lekérdezése istálló id alapján
    @GetMapping("/stable/{stableId}")
    public List<HorseDTO> getHorsesByStableId(@PathVariable Long stableId){
        if (stableService.getStableById(stableId) == null) {
            throw new RuntimeException("Istálló nem található.");
        } else {
            return horseService.getAllHorses().stream()
                .filter(horse -> horse.getStable().getStableId().equals(stableId))
                .map(this::toDTO)
                .toList();
        }
    }

    // Istállóban lévő összes ló lekérdezése istálló név alapján
    @GetMapping("/stable")
    public List<HorseDTO> getHorsesByStableName(@RequestParam String stableName) {
        Stable stable = stableService.getStableByName(stableName);
        if(stable == null){
            throw new RuntimeException("Istálló nem található.");
        }
        return horseService.getAllHorses().stream()
            .filter(horse -> horse.getStable().getStableName().equals(stable.getStableName()))
            .map(this::toDTO).toList();
    }



    private HorseDTO toDTO(Horse horse){
        HorseDTO dto = new HorseDTO();
        dto.horseName = horse.getHorseName();
        dto.dob = horse.getDob();
        dto.sex = horse.getSex();
        dto.ownerName = horse.getOwner().getUserLname() + " "
            + horse.getOwner().getUserFname();
        dto.ownerId = horse.getOwner().getId();
        dto.stableName = horse.getStable().getStableName();
        dto.stableId = horse.getStable().getStableId();
        dto.microchipNum = horse.getMicrochipNum();
        dto.passportNum = horse.getPassportNum();
        dto.additional = horse.getAdditional();
        return dto;
    }
}
