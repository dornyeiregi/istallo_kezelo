package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.ShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;

/**
 * Application service for linking horses to shots.
 */
@Service
public class HorseShotService {
    private final HorseRepository horseRepository;
    private final ShotRepository shotRepository;
    private final HorseShotRepository horseShotRepository;
    private final UserRepository userRepository;
    private final CalendarEventService calendarEventService;

    public HorseShotService(HorseRepository horseRepository,
                            ShotRepository shotRepository,
                            HorseShotRepository horseShotRepository,
                            UserRepository userRepository,
                            CalendarEventService calendarEventService) {
        this.horseRepository = horseRepository;
        this.shotRepository = shotRepository;
        this.horseShotRepository = horseShotRepository;
        this.userRepository = userRepository;
        this.calendarEventService = calendarEventService;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public HorseShot addShotToHorse(Long shotId, Long horseId, Authentication auth) {
        checkHorseOwnership(auth, horseId);
        Shot shot = shotRepository.findById(shotId)
                .orElseThrow(() -> new RuntimeException("Oltás nem található."));
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("Ló nem található."));
        boolean exists = horseShotRepository.existsByShotAndHorse(shot, horse);
        if (exists) {
            throw new RuntimeException("Az oltás már hozzá van csatolva a lóhoz.");
        }
        HorseShot link = new HorseShot();
        link.setShot(shot);
        link.setHorse(horse);
        HorseShot saved = horseShotRepository.save(link);
        calendarEventService.syncFromDomain(horse, EventType.SHOT, shot.getDate(), shot.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseShot> getAllHorseShots(Authentication auth) {
        List<HorseShot> all = horseShotRepository.findAll();
        return filterHorseShotsForOwner(all, auth);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public HorseShot getHorseShotById(Long horseShotId, Authentication auth) {
        HorseShot link = horseShotRepository.findById(horseShotId)
                .orElseThrow(() -> new RuntimeException("Kapcsolat nem található."));
        if (auth == null) {
            return link;
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return link;
        }
        String username = auth.getName();
        if (!link.getHorse().getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Csak a saját lovaidhoz tartozó oltásokat érheted el.");
        }
        return link;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseShot> getHorseForShot(Long shotId, Authentication auth) {
        List<HorseShot> all = horseShotRepository.findByShot_Id(shotId);
        return filterHorseShotsForOwner(all, auth);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<HorseShot> getShotsForHorse(Long horseId, Authentication auth) {
        checkHorseOwnership(auth, horseId);
        return horseShotRepository.findByHorse_Id(horseId);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public void removeShotFromHorse(Long shotId, Long horseId, Authentication auth) {
        checkHorseOwnership(auth, horseId);
        horseShotRepository.deleteByShot_IdAndHorse_Id(shotId, horseId);
        calendarEventService.deleteFromDomain(EventType.SHOT, shotId, horseId);
    }

    private void checkHorseOwnership(Authentication auth, Long horseId) {
        if (auth == null) return;
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("Ló nem található."));
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !horse.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Csak a saját lovaidhoz adhatsz vagy törölhetsz oltásokat.");
        }
    }

    private List<HorseShot> filterHorseShotsForOwner(List<HorseShot> all, Authentication auth) {
        if (auth == null) return all;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return all;
        String username = auth.getName();
        return all.stream()
                .filter(link -> link.getHorse().getOwner().getUsername().equals(username))
                .toList();
    }
}
