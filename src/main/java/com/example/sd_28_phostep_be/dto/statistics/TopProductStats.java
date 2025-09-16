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
public class TopProductStats {
    private Integer productId;
    private String productName;
    private String productCode;
    private String categoryName;
    private String imageUrl;
    private Long totalSold;
    private BigDecimal totalRevenue;
    private Double growthPercentage;
}
