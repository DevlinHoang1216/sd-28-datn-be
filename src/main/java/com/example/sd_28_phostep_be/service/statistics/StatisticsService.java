package com.example.sd_28_phostep_be.service.statistics;

import com.example.sd_28_phostep_be.dto.statistics.DashboardStatsResponse;
import com.example.sd_28_phostep_be.dto.statistics.RevenueDataPoint;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    
    DashboardStatsResponse getDashboardStats();
    
    DashboardStatsResponse getDashboardStatsByDateRange(String startDate, String endDate);
    
    List<RevenueDataPoint> getRevenueDataByPeriod(LocalDate startDate, LocalDate endDate, String period);
    
}
