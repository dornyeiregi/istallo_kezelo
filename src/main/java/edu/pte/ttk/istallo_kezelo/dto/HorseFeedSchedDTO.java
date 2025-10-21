package edu.pte.ttk.istallo_kezelo.dto;

import java.util.List;

import edu.pte.ttk.istallo_kezelo.model.Item;

public class HorseFeedSchedDTO {
    public Long horseId;
    public Long feedSchedId;
    public String horseName;
    public String feedDescription;
    public List<Item> items;
}
