package edu.pte.ttk.istallo_kezelo.controller;

import edu.pte.ttk.istallo_kezelo.dto.HorseDTO;
import edu.pte.ttk.istallo_kezelo.dto.HorseApprovalDTO;
import edu.pte.ttk.istallo_kezelo.mapper.HorseMapper;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.Stable;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.service.HorseService;
import edu.pte.ttk.istallo_kezelo.service.StableService;
import edu.pte.ttk.istallo_kezelo.service.UserService;
import edu.pte.ttk.istallo_kezelo.service.FeedSchedService;
import edu.pte.ttk.istallo_kezelo.service.MailService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;

/**
 * REST controller for horse CRUD and approval actions.
 */
@RestController
@RequestMapping("/api/horses")
public class HorseController {

    private final HorseService horseService;
    private final UserService userService;
    private final StableService stableService;
    private final FeedSchedService feedSchedService;
    private final MailService mailService;

    public HorseController(HorseService horseService,
                           UserService userService,
                           StableService stableService,
                           FeedSchedService feedSchedService,
                           MailService mailService) {
        this.horseService = horseService;
        this.userService = userService;
        this.stableService = stableService;
        this.feedSchedService = feedSchedService;
        this.mailService = mailService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public HorseDTO createHorse(@RequestBody HorseDTO dto, Authentication auth) {
        boolean isAdmin = hasRole(auth, "ADMIN");
        User owner;
        if (!isAdmin) {
            owner = userService.getUserByUsername(auth.getName(), auth);
        } else {
            owner = userService.getUserById(dto.getOwnerId(), auth)
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található."));
        }
        Stable stable = null;
        if (isAdmin) {
            if (dto.getStableId() != null) {
                stable = stableService.getStableById(dto.getStableId())
                    .orElseThrow(() -> new RuntimeException("Istálló nem található."));
            } else if (dto.getStableName() != null && !dto.getStableName().isBlank()) {
                stable = stableService.getStableByName(dto.getStableName());
                if (stable == null) {
                    throw new RuntimeException("Istálló nem található név alapján: " + dto.getStableName());
                }
            } else {
                throw new RuntimeException("Az istálló mező kitöltése kötelező.");
            }
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
        horse.setIsActive(isAdmin ? Boolean.TRUE : Boolean.FALSE);
        horse.setIsPending(!isAdmin);
        Horse savedHorse = horseService.saveHorse(horse);
        if (!isAdmin) {
            String subject = "Új ló jóváhagyásra vár";
            String body = "Új ló kérése érkezett: " + savedHorse.getHorseName();
            mailService.sendToAdmins(subject, body);
        }
        if (isAdmin && dto.getFeedSchedId() != null) {
            feedSchedService.addHorseToFeedSched(dto.getFeedSchedId(), savedHorse.getId());
        }
        return HorseMapper.toDTO(savedHorse);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<HorseDTO> getAllHorses(Authentication auth) {
        return horseService.getAllHorses(auth).stream().map(HorseMapper::toDTO).toList();
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<HorseDTO> getInactiveHorses() {
        return horseService.getInactiveHorses().stream().map(HorseMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public HorseDTO getHorseById(@PathVariable Long id, Authentication auth) {
        Horse horse = horseService.getHorseById(id, auth)
            .orElseThrow(() -> new RuntimeException("Ló nem található."));

        if (hasRole(auth, "OWNER") && !horse.getOwner().getUsername().equals(auth.getName())) {
            throw new RuntimeException("Nincs jogosultsága más lovait megtekinteni.");
        }
        return HorseMapper.toDTO(horse);
    }

    @GetMapping("/byName/{horseName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EMPLOYEE')")
    public HorseDTO getHorseByName(@PathVariable String horseName, Authentication auth) {
        Horse horse = horseService.getHorseByName(horseName, auth);
        return HorseMapper.toDTO(horse);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
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
        return HorseMapper.toDTO(savedHorse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteHorse(@PathVariable Long id) {
        horseService.deleteHorseById(id);
        return ResponseEntity.ok(Map.of("message", "Ló sikeresen törölve."));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public HorseDTO deactivateHorse(@PathVariable Long id) {
        Horse horse = horseService.deactivateHorseById(id);
        return HorseMapper.toDTO(horse);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public HorseDTO activateHorse(@PathVariable Long id) {
        Horse horse = horseService.activateHorseById(id);
        return HorseMapper.toDTO(horse);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseDTO> getMyHorses(Authentication auth) {
        return horseService.getAllHorses(auth).stream()
            .filter(h -> h.getOwner().getUsername().equals(auth.getName()))
            .map(HorseMapper::toDTO).toList();
    }

    @GetMapping("/requests")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<HorseDTO> getHorseRequests() {
        return horseService.getPendingHorses().stream()
            .map(HorseMapper::toDTO).toList();
    }

    @GetMapping("/requests/mine")
    @PreAuthorize("hasAnyRole('OWNER')")
    public List<HorseDTO> getMyHorseRequests(Authentication auth) {
        return horseService.getPendingHorsesForOwner(auth).stream()
            .map(HorseMapper::toDTO).toList();
    }

    @PatchMapping("/requests/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public HorseDTO approveHorseRequest(@PathVariable Long id, @RequestBody(required = false) HorseApprovalDTO dto) {
        if (dto == null || dto.getStableId() == null) {
            throw new RuntimeException("Istálló megadása kötelező.");
        }
        Stable stable = stableService.getStableById(dto.getStableId())
            .orElseThrow(() -> new RuntimeException("Istálló nem található."));
        Horse horse = horseService.approveHorseRequest(id, stable);
        if (dto.getFeedSchedId() != null) {
            feedSchedService.addHorseToFeedSched(dto.getFeedSchedId(), horse.getId());
        }
        return HorseMapper.toDTO(horse);
    }

    @DeleteMapping("/requests/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, String>> rejectHorseRequest(@PathVariable Long id) {
        horseService.deleteHorseById(id);
        return ResponseEntity.ok(Map.of("message", "Ló kérés elutasítva és törölve."));
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
