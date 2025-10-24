package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedSchedItemDTO {
    private Long feedSchedId;
    private Long itemId;
    private String itemName;
    private String feedDescription;
}
