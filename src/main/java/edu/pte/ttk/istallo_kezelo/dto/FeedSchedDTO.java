package edu.pte.ttk.istallo_kezelo.dto;

import edu.pte.ttk.istallo_kezelo.model.enums.FeedTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedSchedDTO {
    private long feedSchedId;
    private FeedTime feedTime;
    private String description;
    private List<Long> horseIds;
    private List<Long> itemIds;
    private List<FeedSchedItemAmountDTO> items;
}
