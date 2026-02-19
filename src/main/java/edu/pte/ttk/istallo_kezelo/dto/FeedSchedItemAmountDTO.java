package edu.pte.ttk.istallo_kezelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedSchedItemAmountDTO {
    private Long itemId;
    private Double amount;
}
