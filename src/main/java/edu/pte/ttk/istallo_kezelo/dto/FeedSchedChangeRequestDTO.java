package edu.pte.ttk.istallo_kezelo.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedSchedChangeRequestDTO {
    private Long id;
    private Long feedSchedId;
    private String feedTime;
    private String description;
    private String requestedByName;
    private LocalDateTime requestedAt;
    private List<Long> horseIds;
    private List<Long> itemIds;
}
