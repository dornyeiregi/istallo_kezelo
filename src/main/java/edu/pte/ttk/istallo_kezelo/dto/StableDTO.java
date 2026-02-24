package edu.pte.ttk.istallo_kezelo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StableDTO {
    private Long stableId;
    private String stableName;
    public List<HorseDTO> horses;
}
