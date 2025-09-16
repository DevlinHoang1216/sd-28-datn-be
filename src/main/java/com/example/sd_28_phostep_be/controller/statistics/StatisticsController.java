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
            // Validate period parameter
            if (!period.matches("^(daily|weekly|monthly|yearly)$")) {
                return ResponseEntity.badRequest().body("Invalid period. Must be one of: daily, weekly, monthly, yearly");
            }
            
            // Parse dates with better error handling
            java.time.LocalDate start;
            java.time.LocalDate end;
            
            try {
                start = java.time.LocalDate.parse(startDate);
                end = java.time.LocalDate.parse(endDate);
            } catch (java.time.format.DateTimeParseException e) {
                return ResponseEntity.badRequest().body("Invalid date format. Expected format: YYYY-MM-DD");
            }
            
            // Validate date range
            if (start.isAfter(end)) {
                return ResponseEntity.badRequest().body("Start date must be before or equal to end date");
            }
            
            var revenueData = statisticsService.getRevenueDataByPeriod(start, end, period);
            return ResponseEntity.ok(revenueData);
            
        } catch (Exception e) {
            // Log the actual error for debugging
            System.err.println("Error in getRevenueByPeriod: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error processing request: " + e.getMessage());
        }
    }
}
