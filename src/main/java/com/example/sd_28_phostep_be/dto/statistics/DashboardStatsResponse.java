package com.example.sd_28_phostep_be.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalCustomers;
    private Long totalProducts;
    
    // Today's stats for breadcrumb
    private BigDecimal todayRevenue;
    private Long todayOrders;
    private Long todayVisitors;
    
    // Revenue trend data
    private List<RevenueDataPoint> revenueData;
    
    // Category performance
    private List<CategoryPerformance> categoryData;
    
    // Top products
    private List<TopProductStats> topProducts;
    
    // Recent orders
    private List<RecentOrderStats> recentOrders;
}
