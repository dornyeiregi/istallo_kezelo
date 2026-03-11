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
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseFarrierApp;
import edu.pte.ttk.istallo_kezelo.model.HorseFeedSched;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.HorseTreatment;
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

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Horse saveHorse(Horse horse) {
        return horseRepository.save(horse);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'OWNER')")
    public List<Horse> getAllHorses(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        if (user.getUserType().name().equals("ADMIN")
                || user.getUserType().name().equals("EMPLOYEE")) {
            return horseRepository.findByIsActiveTrueOrIsActiveIsNull();
        }
        return horseRepository.findByOwnerAndIsActiveTrueOrOwnerAndIsActiveIsNull(user, user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'OWNER')")
    public Optional<Horse> getHorseById(Long id, Authentication auth) {
        Optional<Horse> horse = horseRepository.findById(id);
        return horse.filter(h -> canAccessHorse(h, auth));
    }

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

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteHorseById(Long horseId) {
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("A ló nem található."));
        List<HorseShot> shotLinks = horseShotRepository.findByHorse_Id(horseId);
        List<Long> shotIds = shotLinks.stream()
                .map(link -> link.getShot().getId())
                .toList();
        horseShotRepository.deleteAll(shotLinks);
        shotIds.forEach(shotId -> {
            if (horseShotRepository.countByShot_Id(shotId) == 0) {
                shotRepository.deleteById(shotId);
            }
        });
        List<HorseTreatment> treatmentLinks = horseTreatmentRepository.findByHorse_Id(horseId);
        List<Long> treatmentIds = treatmentLinks.stream()
                .map(link -> link.getTreatment().getId())
                .toList();
        horseTreatmentRepository.deleteAll(treatmentLinks);
        treatmentIds.forEach(treatmentId -> {
            if (horseTreatmentRepository.countByTreatment_Id(treatmentId) == 0) {
                treatmentRepository.deleteById(treatmentId);
            }
        });
        List<HorseFarrierApp> farrierLinks = horseFarrierAppRepository.findByHorseId(horseId);
        List<Long> farrierIds = farrierLinks.stream()
                .map(link -> link.getFarrierApp().getId())
                .toList();
        horseFarrierAppRepository.deleteAll(farrierLinks);
        farrierIds.forEach(farrierAppId -> {
            if (horseFarrierAppRepository.countByFarrierApp_Id(farrierAppId) == 0) {
                farrierAppRepository.deleteById(farrierAppId);
            }
        });
        List<HorseFeedSched> feedLinks = horseFeedSchedRepository.findByHorseId(horseId);
        List<Long> feedIds = feedLinks.stream()
                .map(link -> link.getFeedSched().getId())
                .toList();
        horseFeedSchedRepository.deleteAll(feedLinks);
        feedIds.forEach(feedSchedId -> {
            if (horseFeedSchedRepository.countByFeedSchedId(feedSchedId) == 0) {
                feedSchedRepository.deleteById(feedSchedId);
            }
        });
        horseRepository.delete(horse);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Horse deactivateHorseById(Long horseId) {
        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("A ló nem található."));
        horse.setIsActive(Boolean.FALSE);
        return horseRepository.save(horse);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<Horse> getInactiveHorses() {
        return horseRepository.findByIsActiveFalse();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<Horse> getPendingHorses() {
        return horseRepository.findByIsActiveFalse();
    }

    @PreAuthorize("hasAnyRole('OWNER')")
    public List<Horse> getPendingHorsesForOwner(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        return horseRepository.findByOwnerAndIsActiveFalse(user);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Horse approveHorseRequest(Long horseId) {
        Horse horse = horseRepository.findById(horseId)
            .orElseThrow(() -> new RuntimeException("A ló nem található."));
        horse.setIsActive(Boolean.TRUE);
        return horseRepository.save(horse);
    }

    private boolean canAccessHorse(Horse horse, Authentication auth) {
        if (auth == null) return false;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isEmployee = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
        if (isAdmin) return true;
        if (isEmployee) {
            return horse.getIsActive() == null || Boolean.TRUE.equals(horse.getIsActive());
        }
        String username = auth.getName();
        return horse.getOwner() != null && horse.getOwner().getUsername().equals(username);
    }
}
