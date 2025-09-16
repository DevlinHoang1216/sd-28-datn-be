package com.example.sd_28_phostep_be.controller.statistics;

import com.example.sd_28_phostep_be.dto.statistics.DashboardStatsResponse;
import com.example.sd_28_phostep_be.service.statistics.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = statisticsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/dashboard/range")
    public ResponseEntity<DashboardStatsResponse> getDashboardStatsByRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        DashboardStatsResponse stats = statisticsService.getDashboardStatsByDateRange(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/revenue/period")
    public ResponseEntity<?> getRevenueByPeriod(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "daily") String period) {
        try {
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            var revenueData = statisticsService.getRevenueDataByPeriod(start, end, period);
            return ResponseEntity.ok(revenueData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date format or period");
        }
    }
}
