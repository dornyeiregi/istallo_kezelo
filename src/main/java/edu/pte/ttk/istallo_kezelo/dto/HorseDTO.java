package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;

import edu.pte.ttk.istallo_kezelo.model.Sex;
//import edu.pte.ttk.istallo_kezelo.model.User;

public class HorseDTO {
    public String horseName;
    public LocalDate dob;
    public Sex sex;
    public String ownerName;
    public Long ownerId;
//    public User owner;
    public String stableName;
    public Long stableId;
    public String microchipNum;
    public String passportNum;
    public String additional;
}