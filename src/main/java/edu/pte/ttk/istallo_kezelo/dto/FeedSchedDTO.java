package edu.pte.ttk.istallo_kezelo.dto;

import edu.pte.ttk.istallo_kezelo.model.FeedTime;
import java.util.List;

public class FeedSchedDTO {
    public FeedTime feedTime;
    public String description;
    public List<Long> horseIds;
    public List<Long> itemIds;
}
