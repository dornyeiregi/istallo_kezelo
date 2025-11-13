package edu.pte.ttk.istallo_kezelo.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class HorseService {

    private final HorseRepository horseRepository;
    private final UserRepository userRepository;

    public HorseService(HorseRepository horseRepository,
                        UserRepository userRepository) {
        this.horseRepository = horseRepository; 
        this.userRepository = userRepository;
    }

    // Új ló mentése
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Horse saveHorse(Horse horse) {
        return horseRepository.save(horse);
    }

    // Összes ló lekérdezése
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'OWNER')")
    public List<Horse> getAllHorses(Authentication auth) {
    User user = userRepository.findByUsername(auth.getName());
    if (user.getUserType().name().equals("ADMIN") || user.getUserType().name().equals("EMPLOYEE")) {
        return horseRepository.findAll();
    } else {
        return horseRepository.findByOwner(user);
    }
}


    // Ló lekérdezése id alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'OWNER')")
    public Optional<Horse> getHorseById(Long id, Authentication auth) {
        Optional<Horse> horse = horseRepository.findById(id);
        return horse.filter(h -> canAccessHorse(h, auth));
    }

    // Ló lekérdezése név alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'OWNER')")
    public Horse getHorseByName(String horseName, Authentication auth) {
        Horse horse = horseRepository.findByHorseName(horseName);
        if (horse == null) {
            throw new RuntimeException("Ló nem található.");
        }

        if (!canAccessHorse(horse, auth)) {
            throw new RuntimeException("Nincs jogosultságod megtekinteni ezt a lovat.");
        }
        return horse;
    }


    // Ló frissítése
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Horse updateHorse(String horseName, Horse updatedHorse, Authentication auth) {
        Horse horse = horseRepository.findByHorseName(horseName);

        if (!canAccessHorse(horse, auth)) {
            throw new RuntimeException("Nincs jogosultságod ennek a lónak a szerkesztéséhez.");
        }
        horse.setHorseName(updatedHorse.getHorseName());
        horse.setDob(updatedHorse.getDob());
        horse.setSex(updatedHorse.getSex());
        horse.setPassportNum(updatedHorse.getPassportNum());
        horse.setMicrochipNum(updatedHorse.getMicrochipNum());
        horse.setAdditional(updatedHorse.getAdditional());
        horse.setStable(updatedHorse.getStable());
        horse.setOwner(updatedHorse.getOwner());
        return horseRepository.save(horse);
    }


    // Ló törlése
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteHorseById(Long id) {
        horseRepository.deleteById(id);
    }

    // Segédmetódus – OWNER csak a saját lovait láthatja
    // private List<Horse> filterHorsesForOwner(List<Horse> all, Authentication auth) {
    //     if (auth == null) return all;

    //     boolean isAdmin = auth.getAuthorities().stream()
    //             .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    //     boolean isEmployee = auth.getAuthorities().stream()
    //             .anyMatch(a -> a.getAuthority().equals("EMPLOYEE"));

    //     if (isAdmin || isEmployee) {
    //         return all;
    //     }

    //     String username = auth.getName();
    //     return all.stream()
    //             .filter(h -> h.getOwner() != null && h.getOwner().getUsername().equals(username))
    //             .toList();
    // }

    // Egyetlen ló elérhetőségének ellenőrzése
    private boolean canAccessHorse(Horse horse, Authentication auth) {
        if (auth == null) return false;

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isEmployee = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
        if (isAdmin || isEmployee) return true;

        String username = auth.getName();
        return horse.getOwner() != null && horse.getOwner().getUsername().equals(username);
    }
    
}

