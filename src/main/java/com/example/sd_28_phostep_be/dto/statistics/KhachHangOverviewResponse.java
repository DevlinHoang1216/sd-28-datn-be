package com.example.sd_28_phostep_be.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangOverviewResponse {
    // Order statistics
    private Long totalOrders;
    private Long completedOrders;
    private Long processingOrders;
    private Long cancelledOrders;
    
    // Financial statistics
    private BigDecimal totalSpent;
    private BigDecimal averageOrderValue;
    
    // Customer info
    private String customerName;
    private String customerCode;
    private String membershipLevel;
    private Integer loyaltyPoints;
    
    // Recent activity
    private String lastOrderDate;
    private String lastOrderStatus;
    private BigDecimal lastOrderAmount;
}
