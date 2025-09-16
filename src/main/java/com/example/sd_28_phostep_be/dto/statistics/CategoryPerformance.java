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
public class CategoryPerformance {
    private String categoryName;
    private BigDecimal revenue;
    private Long orderCount;
    private Integer productCount;
    private Double percentage;
}
