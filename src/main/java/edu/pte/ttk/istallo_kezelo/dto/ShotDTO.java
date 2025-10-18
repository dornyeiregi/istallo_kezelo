package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDate;
import java.util.List;

public class ShotDTO {
    public String shotName;
    public Integer frequencyValue;
    public String frequencyUnit;
    public LocalDate date;
    public List<Long> horseIds;
}
