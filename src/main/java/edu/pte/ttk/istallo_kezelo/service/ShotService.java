package edu.pte.ttk.istallo_kezelo.service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.dto.ShotDTO;
import edu.pte.ttk.istallo_kezelo.model.Horse;
import edu.pte.ttk.istallo_kezelo.model.HorseShot;
import edu.pte.ttk.istallo_kezelo.model.Shot;
import edu.pte.ttk.istallo_kezelo.model.User;
import edu.pte.ttk.istallo_kezelo.model.enums.EventType;
import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.repository.HorseShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.ShotRepository;
import edu.pte.ttk.istallo_kezelo.repository.UserRepository;

@Service
public class ShotService {

    private final ShotRepository shotRepository;
    private final HorseShotRepository horseShotRepository;
    private final HorseRepository horseRepository;
    private final UserRepository userRepository;
    private final CalendarEventService calendarEventService;

    public ShotService(ShotRepository shotRepository,
                       HorseShotRepository horseShotRepository,
                       HorseRepository horseRepository,
                       UserRepository userRepository,
                       CalendarEventService calendarEventService) {
        this.shotRepository = shotRepository;
        this.horseShotRepository = horseShotRepository;
        this.horseRepository = horseRepository;
        this.userRepository = userRepository;
        this.calendarEventService = calendarEventService;
    }

    // Új oltás létrehozása
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Shot saveShot(Shot shot, List<Long> horseIds, Authentication auth) {

        Shot saved = shotRepository.save(shot);

        if (horseIds != null) {
            for (Long horseId : horseIds) {

                if (auth != null && !isAdmin(auth)) {
                    checkHorseOwnership(auth, horseId);
                }
                Horse horse = horseRepository.findById(horseId)
                    .orElseThrow(() -> new RuntimeException("Ló nem található: " + horseId));

                HorseShot link = new HorseShot();
                link.setHorse(horse);
                link.setShot(saved);

                horseShotRepository.save(link);

                saved.getHorses_treated().add(link);

                calendarEventService.createEvent(
                        horse.getId(),
                        EventType.SHOT,
                        saved.getDate(),
                        saved.getId()
                );
            }
        }
        return saved;
    }

    // Összes oltás lekérdezése
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<Shot> getAllShots(Authentication auth) {
        List<Shot> all = shotRepository.findAll();
        return filterShotsForOwner(all, auth);
    }

    // Oltás lekérdezése ID alapján
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Shot getShotById(Long shotId, Authentication auth) {
        Shot shot = shotRepository.findById(shotId)
            .orElseThrow(() -> new RuntimeException("Oltás nem található."));

        if (!isAdmin(auth) && shot.getHorses_treated().stream()
            .noneMatch(link -> link.getHorse().getOwner().getUsername().equals(auth.getName()))) {
            throw new RuntimeException("Nincs jogosultságod ehhez az oltáshoz.");
        }
        return shot;
    }

    // Ló összes oltásának lekérdezése ID alapján
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<Shot> getShotsByHorseId(Long horseId, Authentication auth) {
        checkHorseOwnership(auth, horseId);
        List<HorseShot> horseShots = horseShotRepository.findByHorse_Id(horseId);
        return horseShots.stream().map(HorseShot::getShot).toList();
    }

    // Ló összes oltásának lekérdezése név alapján
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public List<Shot> getShotsByHorseName(String horseName, Authentication auth) {
        Horse horse = horseRepository.findByHorseName(horseName);
        if (horse == null) {
            throw new RuntimeException("Ló nem található.");
        }

        checkHorseOwnership(auth, horse.getId());

        List<HorseShot> horseShots = horseShotRepository.findByHorse_horseName(horseName);
        return horseShots.stream().map(HorseShot::getShot).toList();
    }

    // Oltás frissítése
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public Shot updateShot(Long shotId, ShotDTO updatedShot, Authentication auth) {
        Shot shot = shotRepository.findById(shotId)
                .orElseThrow(() -> new RuntimeException("Oltás nem található."));

        if (!isAdmin(auth) && shot.getHorses_treated().stream()
                .noneMatch(link -> link.getHorse().getOwner().getUsername().equals(auth.getName()))) {
            throw new RuntimeException("Csak saját lovakhoz tartozó oltásokat módosíthatsz.");
        }

        if (updatedShot.getShotName() != null) shot.setShotName(updatedShot.getShotName());
        if (updatedShot.getDate() != null) shot.setDate(updatedShot.getDate());
        if (updatedShot.getFrequencyUnit() != null) shot.setFrequencyUnit(updatedShot.getFrequencyUnit());
        if (updatedShot.getFrequencyValue() != null) shot.setFrequencyValue(updatedShot.getFrequencyValue());

        if (updatedShot.getHorseIds() != null) {
            Set<Long> desiredHorseIds = new HashSet<>(updatedShot.getHorseIds());
            Set<Long> existingHorseIds = shot.getHorses_treated().stream()
                    .map(link -> link.getHorse().getId())
                    .collect(java.util.stream.Collectors.toSet());

            java.util.Iterator<HorseShot> iterator = shot.getHorses_treated().iterator();
            while (iterator.hasNext()) {
                HorseShot link = iterator.next();
                Long horseId = link.getHorse().getId();
                if (!desiredHorseIds.contains(horseId)) {
                    iterator.remove();
                    calendarEventService.deleteFromDomain(EventType.SHOT, shotId, horseId);
                    horseShotRepository.delete(link);
                }
            }

            for (Long horseId : desiredHorseIds) {
                if (!existingHorseIds.contains(horseId)) {
                    if (auth != null && !isAdmin(auth)) {
                        checkHorseOwnership(auth, horseId);
                    }
                    Horse horse = horseRepository.findById(horseId)
                            .orElseThrow(() -> new RuntimeException("Ló nem található: " + horseId));
                    HorseShot link = new HorseShot();
                    link.setHorse(horse);
                    link.setShot(shot);
                    shot.getHorses_treated().add(link);
                }
            }
        }

        Shot saved = shotRepository.save(shot);
        for (HorseShot link : saved.getHorses_treated()) {
            Horse horse = link.getHorse();
            calendarEventService.syncFromDomain(
                    horse,
                    EventType.SHOT,
                    saved.getDate(),
                    saved.getId()
            );
        }

        return saved;
    }

    // Oltás törlése
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public void deleteShotById(Long shotId, Authentication auth) {
        Shot shot = shotRepository.findById(shotId)
                .orElseThrow(() -> new RuntimeException("Oltás nem található."));

        if (!isAdmin(auth)) {
            boolean ownsShot = shot.getHorses_treated().stream()
                    .anyMatch(link -> link.getHorse().getOwner().getUsername().equals(auth.getName()));
            if (!ownsShot) {
                throw new RuntimeException("Csak a saját lovakhoz tartozó oltásokat törölheted.");
            }
        }   

        shotRepository.deleteById(shotId);
        calendarEventService.deleteFromDomain(EventType.SHOT, shotId);
    }

    // Helper – OWNER csak a saját lovait módosíthatja
    private void checkHorseOwnership(Authentication auth, Long horseId) {
        if (auth == null || isAdmin(auth)) return;

        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("Ló nem található."));

        if (!horse.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Csak a saját lovaidhoz adhatsz hozzá oltást.");
        }
    }

    // Helper – OWNER-szűrés minden GET metódushoz
    private List<Shot> filterShotsForOwner(List<Shot> allShots, Authentication auth) {
        if (auth == null || isAdmin(auth)) return allShots;

        String username = auth.getName();

        return allShots.stream()
                .filter(shot -> shot.getHorses_treated().stream()
                        .anyMatch(link -> link.getHorse().getOwner().getUsername().equals(username)))
                .toList();
    }

    // Helper – ADMIN-e az adott felhasználó
    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
