package com.example.sd_28_phostep_be.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentOrderStats {
    private Integer orderId;
    private String orderCode;
    private String customerName;
    private String customerAvatar;
    private Integer productCount;
    private BigDecimal totalAmount;
    private String status;
    private OffsetDateTime createdAt;
}
