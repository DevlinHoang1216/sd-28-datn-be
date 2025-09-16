package com.example.sd_28_phostep_be.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueDataPoint {
    private String date;
    private BigDecimal revenue;
    private Long orderCount;
}
