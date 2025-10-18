package edu.pte.ttk.istallo_kezelo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.pte.ttk.istallo_kezelo.repository.HorseRepository;
import edu.pte.ttk.istallo_kezelo.model.Horse;

import java.util.List;
import java.util.Optional;

@Service
public class HorseService {

    private final HorseRepository horseRepository;

    public HorseService(HorseRepository horseRepository) {
        this.horseRepository = horseRepository; 
    }

    // Új ló mentése
    @Transactional
    public Horse saveHorse(Horse horse) {
        return horseRepository.save(horse);
    }

    // Összes ló lekérdezése
    public List<Horse> getAllHorses() {
        return horseRepository.findAll();
    }

    // Ló lekérdezése id alapján
    public Optional<Horse> getHorseById(Long id) {
        return horseRepository.findById(id);
    }

    // Ló lekérdezése név alapján
    public Horse getHorseByName(String horseName) {
        return horseRepository.findByHorseName(horseName);
    }


    // Ló frissítése
    @Transactional
    public Horse updateHorse(Long id, Horse updatedHorse) {
        Optional<Horse> existingHorse = horseRepository.findById(id);
        if (existingHorse.isPresent()) {
            Horse horse = existingHorse.get();
            horse.setHorseName(updatedHorse.getHorseName());
            horse.setDob(updatedHorse.getDob());
            horse.setSex(updatedHorse.getSex());
            horse.setPassportNum(updatedHorse.getPassportNum());
            horse.setMicrochipNum(updatedHorse.getMicrochipNum());
            horse.setAdditional(updatedHorse.getAdditional());
            horse.setStable(updatedHorse.getStable());
            horse.setOwner(updatedHorse.getOwner());
            return horseRepository.save(horse);
        } else {
            throw new RuntimeException("Ló nem található.");
        }
    }


    // Ló törlése
    @Transactional
    public void deleteHorseById(Long id) {
        horseRepository.deleteById(id);
    }
    
}

