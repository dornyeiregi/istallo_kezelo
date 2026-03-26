package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDateTime;
import java.util.List;
import edu.pte.ttk.istallo_kezelo.dto.FeedSchedItemAmountDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedSchedChangeRequestDTO {
    private Long id;
    private Long feedSchedId;
    private Boolean requestedMorning;
    private Boolean requestedNoon;
    private Boolean requestedEvening;
    private String description;
    private String requestedByName;
    private LocalDateTime requestedAt;
    private List<Long> horseIds;
    private List<Long> itemIds;
    private List<FeedSchedItemAmountDTO> items;
}
