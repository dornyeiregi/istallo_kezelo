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
        User owner = userService.getUserById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található."));

        Stable stable = stableService.getStableById(dto.getStableId())
                .orElseThrow(() -> new RuntimeException("Istálló nem található."));

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
    @GetMapping
    public List<HorseDTO> getAllHorses() {
        return horseService.getAllHorses().stream().map(this::toDTO).toList();
    }

    // Ló lekérdezése id alapján
    @GetMapping("/{id}")
    public HorseDTO getHorseById(@PathVariable Long id) {
        Horse horse = horseService.getHorseById(id)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));
        return toDTO(horse);
    }

    // Ló lekérdezése név alapján
    @GetMapping("/byName/{horseName}")
    public HorseDTO getHorseByName (@PathVariable String horseName) {
        Horse horse = horseService.getHorseByName(horseName);
        if (horse == null) {
            throw new RuntimeException("Ló nem található.");
        }
        return toDTO(horse);
    }

    // Ló frissítése
    @PatchMapping("/{id}")
    public HorseDTO updateHorsePartially(@PathVariable Long id, @RequestBody HorseDTO dto){
        Horse existingHorse = horseService.getHorseById(id)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));

        if (dto.getHorseName() != null) {existingHorse.setHorseName(dto.getHorseName());}
        if(dto.getDob() != null) {existingHorse.setDob(dto.getDob());}
        if(dto.getSex() != null) {existingHorse.setSex(dto.getSex());}
        if(dto.getMicrochipNum() != null) {existingHorse.setMicrochipNum(dto.getMicrochipNum());}
        if(dto.getPassportNum() != null) {existingHorse.setPassportNum(dto.getPassportNum());}
        if(dto.getAdditional() != null) {existingHorse.setAdditional(dto.getAdditional());}

        if (dto.getOwnerId() != null) {
            User newOwner = userService.getUserById(dto.getOwnerId())
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
    public ResponseEntity<String> deleteHorse(@PathVariable Long id) {
        horseService.deleteHorseById(id);
        return ResponseEntity.ok("Ló sikeresen törölve.");
    }



    /**
     * Más service-ekhez tartozó lekérdezések
     */

    // Felhasználó összes lovának lekérdezése felhasználó id alapján
    @GetMapping("/byOwnerId/{ownerId}")
    public List<HorseDTO> getHorsesByOwner(@PathVariable Long ownerId) {
        if (userService.getUserById(ownerId) == null) {
            throw new RuntimeException("Felhasználó nem található.");
        } else {
            return horseService.getAllHorses().stream()
                .filter(horse -> horse.getOwner().getId().equals(ownerId))
                .map(this::toDTO).toList();
        }
    }

    // Felhasználó összes lovának lekérdezése felhasználó neve alapján
    @GetMapping("/byOwnerName/{lName}/{fName}")
    public List<HorseDTO> getHorsesByOwnerName(@PathVariable String lName, @PathVariable String fName) {
        User owner = userService.getUserByFullName(lName, fName);
        if (owner == null) {
            throw new RuntimeException("Felhasználó nem található.");
        }
        return horseService.getAllHorses().stream()
            .filter(horse -> horse.getOwner().getId().equals(owner.getId()))
            .map(this::toDTO).toList();
    }

    // Istállóban lévő összes ló lekérdezése istálló id alapján
    @GetMapping("/byStableId/{stableId}")
    public List<HorseDTO> getHorsesByStableId(@PathVariable Long stableId){
        if (stableService.getStableById(stableId) == null) {
            throw new RuntimeException("Istálló nem található.");
        } else {
            return horseService.getAllHorses().stream()
                .filter(horse -> horse.getStable().getStableId().equals(stableId))
                .map(this::toDTO).toList();
        }
    }

    // Istállóban lévő összes ló lekérdezése istálló név alapján
    @GetMapping("/byStableName/{stableName}")
    public List<HorseDTO> getHorsesByStableName(@PathVariable String stableName) {
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
}
