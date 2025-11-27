package edu.pte.ttk.istallo_kezelo.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.repository.FarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.FeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFarrierAppRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseFeedSchedRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseTreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.ShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.TreatmentRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;
import edu.pte.ttk.istallo_kezelo.model.FarrierApp;
import edu.pte.ttk.istallo_kezelo.model.FeedSched;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.model.Treatment;
import edu.pte.ttk.istallo_kezelo.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class HorseService {

    private final HorseRepository horseRepository;
    private final UserRepository userRepository;

    private final HorseShotRepository horseShotRepository;
    private final ShotRepository shotRepository;

    private final HorseFarrierAppRepository horseFarrierAppRepository;
    private final FarrierAppRepository farrierAppRepository;

    private final HorseFeedSchedRepository horseFeedSchedRepository;
    private final FeedSchedRepository feedSchedRepository;

    private final HorseTreatmentRepository horseTreatmentRepository;
    private final TreatmentRepository treatmentRepository;

    public HorseService(
            HorseRepository horseRepository,
            UserRepository userRepository,

            HorseShotRepository horseShotRepository,
            ShotRepository shotRepository,

            HorseFarrierAppRepository horseFarrierAppRepository,
            FarrierAppRepository farrierAppRepository,

            HorseFeedSchedRepository horseFeedSchedRepository,
            FeedSchedRepository feedSchedRepository,

            HorseTreatmentRepository horseTreatmentRepository,
            TreatmentRepository treatmentRepository
    ) {
        this.horseRepository = horseRepository;
        this.userRepository = userRepository;

        this.horseShotRepository = horseShotRepository;
        this.shotRepository = shotRepository;

        this.horseFarrierAppRepository = horseFarrierAppRepository;
        this.farrierAppRepository = farrierAppRepository;

        this.horseFeedSchedRepository = horseFeedSchedRepository;
        this.feedSchedRepository = feedSchedRepository;

        this.horseTreatmentRepository = horseTreatmentRepository;
        this.treatmentRepository = treatmentRepository;
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
    public void deleteHorseById(Long horseId) {

        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("A ló nem található."));

        // Oltások (HorseShot)
        List<HorseShot> shotLinks = horseShotRepository.findByHorse_Id(horseId);

        for (HorseShot link : shotLinks) {

            Shot shot = link.getShot();
            horseShotRepository.delete(link);
            int remaining = horseShotRepository.countByShot_Id(shot.getId());
            // ha nincs több ló hozzácsatolva, töröljük a shot-ot is
            if (remaining == 0) {
                shotRepository.delete(shot);
            }
        }

        // Kezelések (HorseTreatment)
        List<HorseTreatment> treatmentLinks = horseTreatmentRepository.findByHorse_Id(horseId);

        for (HorseTreatment link : treatmentLinks) {

            Treatment treatment = link.getTreatment();
            horseTreatmentRepository.delete(link);
            int remaining = horseTreatmentRepository.countByTreatment_Id(treatment.getId());
            if (remaining == 0) {
                treatmentRepository.delete(treatment);
            }
        }

        // Patkolások (HorseFarrierApp)
        List<HorseFarrierApp> farrierLinks = horseFarrierAppRepository.findByHorseId(horseId);

        for (HorseFarrierApp link : farrierLinks) {
            FarrierApp app = link.getFarrierApp();
            horseFarrierAppRepository.delete(link);
            int remaining = horseFarrierAppRepository.countByFarrierApp_Id(app.getId());
            if (remaining == 0) {
                farrierAppRepository.delete(app);
            }
        }

        // Etetési naplók  (HorseFeedSched)
        List<HorseFeedSched> feedLinks = horseFeedSchedRepository.findByHorseId(horseId);

        for (HorseFeedSched link : feedLinks) {
            FeedSched sched = link.getFeedSched();
            horseFeedSchedRepository.delete(link);
            int remaining = horseFeedSchedRepository.countByFeedSchedId(sched.getId());
            if (remaining == 0) {
                feedSchedRepository.delete(sched);
            }
        }

        // Ló törlése
        // Stable NEM törlődik!
        horseRepository.delete(horse);
    }

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

