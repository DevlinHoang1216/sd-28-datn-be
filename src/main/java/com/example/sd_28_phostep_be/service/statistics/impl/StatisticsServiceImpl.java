package com.example.sd_28_phostep_be.service.statistics.impl;

import com.example.sd_28_phostep_be.dto.statistics.*;
import com.example.sd_28_phostep_be.repository.bill.HoaDonRepository;
import com.example.sd_28_phostep_be.repository.bill.HoaDonChiTietRepository;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import com.example.sd_28_phostep_be.repository.product.SanPhamRepository;
import com.example.sd_28_phostep_be.service.statistics.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final SanPhamRepository sanPhamRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        // Get stats for last 30 days by default
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        return getDashboardStatsByDateRange(
            startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        );
    }

    @Override
    public DashboardStatsResponse getDashboardStatsByDateRange(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDate today = LocalDate.now();

        // Main stats - all completed orders (trangThai = 2 means completed/delivered)
        BigDecimal totalRevenue = hoaDonRepository.getTotalRevenueByDateRange(start, end);
        Long totalOrders = hoaDonRepository.getTotalOrdersByDateRange(start, end);
        Long totalCustomers = khachHangRepository.getTotalActiveCustomers();
        Long totalProducts = sanPhamRepository.getTotalActiveProducts();

        // Today's stats for breadcrumb
        BigDecimal todayRevenue = hoaDonRepository.getTotalRevenueByDate(today);
        Long todayOrders = hoaDonRepository.getTotalOrdersByDate(today);
        Long todayVisitors = 1234L; // This would need visitor tracking system

        // Revenue trend data (last 15 days)
        List<RevenueDataPoint> revenueData = getRevenueDataPoints(start, end);

        // Category performance
        List<CategoryPerformance> categoryData = getCategoryPerformance(start, end);

        // Top products
        List<TopProductStats> topProducts = getTopProducts(start, end);

        // Recent orders
        List<RecentOrderStats> recentOrders = getRecentOrders();

        return DashboardStatsResponse.builder()
            .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
            .totalOrders(totalOrders != null ? totalOrders : 0L)
            .totalCustomers(totalCustomers != null ? totalCustomers : 0L)
            .totalProducts(totalProducts != null ? totalProducts : 0L)
            .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
            .todayOrders(todayOrders != null ? todayOrders : 0L)
            .todayVisitors(todayVisitors)
            .revenueData(revenueData)
            .categoryData(categoryData)
            .topProducts(topProducts)
            .recentOrders(recentOrders)
            .build();
    }

    private List<RevenueDataPoint> getRevenueDataPoints(LocalDate startDate, LocalDate endDate) {
        List<RevenueDataPoint> dataPoints = new ArrayList<>();
        
        // Get daily revenue for the date range
        List<Object[]> dailyRevenue = hoaDonRepository.getDailyRevenue(startDate, endDate);
        
        for (Object[] row : dailyRevenue) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            BigDecimal revenue = (BigDecimal) row[1];
            Long orderCount = ((Number) row[2]).longValue();
            
            dataPoints.add(RevenueDataPoint.builder()
                .date(date.format(DateTimeFormatter.ofPattern("d/M")))
                .revenue(revenue)
                .orderCount(orderCount)
                .build());
        }
        
        return dataPoints;
    }

    @Override
    public List<RevenueDataPoint> getRevenueDataByPeriod(LocalDate startDate, LocalDate endDate, String period) {
        switch (period.toLowerCase()) {
            case "weekly":
                return getWeeklyRevenueData(startDate, endDate);
            case "monthly":
                return getMonthlyRevenueData(startDate, endDate);
            case "yearly":
                return getYearlyRevenueData(startDate, endDate);
            default:
                return getDailyRevenueData(startDate, endDate);
        }
    }
    
    private List<RevenueDataPoint> getDailyRevenueData(LocalDate startDate, LocalDate endDate) {
        return getRevenueDataPoints(startDate, endDate);
    }
    
    private List<RevenueDataPoint> getWeeklyRevenueData(LocalDate startDate, LocalDate endDate) {
        List<Object[]> weeklyRevenue = hoaDonRepository.getWeeklyRevenue(startDate, endDate);
        List<RevenueDataPoint> dataPoints = new ArrayList<>();
        
        for (Object[] row : weeklyRevenue) {
            Integer year = ((Number) row[0]).intValue();
            Integer week = ((Number) row[1]).intValue();
            BigDecimal revenue = (BigDecimal) row[2];
            Long orderCount = ((Number) row[3]).longValue();
            
            // Calculate the first day of the week for the given year and week
            // Handle edge cases where week calculation might fail
            LocalDate firstDayOfWeek;
            try {
                firstDayOfWeek = LocalDate.of(year, 1, 1)
                    .with(java.time.temporal.WeekFields.ISO.weekOfYear(), week)
                    .with(java.time.temporal.WeekFields.ISO.dayOfWeek(), 1);
            } catch (Exception e) {
                // Fallback: use the first day of the year if week calculation fails
                firstDayOfWeek = LocalDate.of(year, 1, 1);
            }
            
            dataPoints.add(RevenueDataPoint.builder()
                .date(firstDayOfWeek.toString())
                .revenue(revenue)
                .orderCount(orderCount)
                .build());
        }
        
        return dataPoints;
    }
    
    private List<RevenueDataPoint> getMonthlyRevenueData(LocalDate startDate, LocalDate endDate) {
        List<Object[]> monthlyRevenue = hoaDonRepository.getMonthlyRevenue(startDate, endDate);
        List<RevenueDataPoint> dataPoints = new ArrayList<>();
        
        for (Object[] row : monthlyRevenue) {
            Integer year = ((Number) row[0]).intValue();
            Integer month = ((Number) row[1]).intValue();
            BigDecimal revenue = (BigDecimal) row[2];
            Long orderCount = ((Number) row[3]).longValue();
            
            // Create the first day of the month with validation
            LocalDate firstDayOfMonth;
            try {
                firstDayOfMonth = LocalDate.of(year, month, 1);
            } catch (Exception e) {
                // Fallback: use January 1st if month is invalid
                firstDayOfMonth = LocalDate.of(year, 1, 1);
            }
            
            dataPoints.add(RevenueDataPoint.builder()
                .date(firstDayOfMonth.toString())
                .revenue(revenue)
                .orderCount(orderCount)
                .build());
        }
        
        return dataPoints;
    }
    
    private List<RevenueDataPoint> getYearlyRevenueData(LocalDate startDate, LocalDate endDate) {
        List<Object[]> yearlyRevenue = hoaDonRepository.getYearlyRevenue(startDate, endDate);
        List<RevenueDataPoint> dataPoints = new ArrayList<>();
        
        for (Object[] row : yearlyRevenue) {
            Integer year = ((Number) row[0]).intValue();
            BigDecimal revenue = (BigDecimal) row[1];
            Long orderCount = ((Number) row[2]).longValue();
            
            // Create January 1st of the year with validation
            LocalDate firstDayOfYear;
            try {
                firstDayOfYear = LocalDate.of(year, 1, 1);
            } catch (Exception e) {
                // Fallback: use current year if year is invalid
                firstDayOfYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
            }
            
            dataPoints.add(RevenueDataPoint.builder()
                .date(firstDayOfYear.toString())
                .revenue(revenue)
                .orderCount(orderCount)
                .build());
        }
        
        return dataPoints;
    }

    private List<CategoryPerformance> getCategoryPerformance(LocalDate startDate, LocalDate endDate) {
        List<Object[]> categoryStats = hoaDonRepository.getCategoryPerformance(startDate, endDate);
        BigDecimal totalRevenue = categoryStats.stream()
            .map(row -> (BigDecimal) row[1])
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return categoryStats.stream()
            .map(row -> {
                String categoryName = (String) row[0];
                BigDecimal revenue = (BigDecimal) row[1];
                Long orderCount = ((Number) row[2]).longValue();
                Integer productCount = ((Number) row[3]).intValue();
                
                Double percentage = totalRevenue.compareTo(BigDecimal.ZERO) > 0 
                    ? revenue.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue()
                    : 0.0;

                return CategoryPerformance.builder()
                    .categoryName(categoryName)
                    .revenue(revenue)
                    .orderCount(orderCount)
                    .productCount(productCount)
                    .percentage(percentage)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private List<TopProductStats> getTopProducts(LocalDate startDate, LocalDate endDate) {
        List<Object[]> topProductsData = hoaDonRepository.getTopProducts(startDate, endDate);
        
        return topProductsData.stream()
            .limit(5) // Top 5 products
            .map(row -> {
                Integer productId = (Integer) row[0];
                String productName = (String) row[1];
                String productCode = (String) row[2];
                String categoryName = (String) row[3];
                String imageUrl = (String) row[4];
                Long totalSold = ((Number) row[5]).longValue();
                BigDecimal totalRevenue = (BigDecimal) row[6];
                
                // Calculate growth percentage (mock for now - would need historical comparison)
                Double growthPercentage = Math.random() * 30 - 5; // Random between -5% and 25%

                return TopProductStats.builder()
                    .productId(productId)
                    .productName(productName)
                    .productCode(productCode)
                    .categoryName(categoryName)
                    .imageUrl(imageUrl)
                    .totalSold(totalSold)
                    .totalRevenue(totalRevenue)
                    .growthPercentage(growthPercentage)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private List<RecentOrderStats> getRecentOrders() {
        List<Object[]> recentOrdersData = hoaDonRepository.getRecentOrders();
        
        return recentOrdersData.stream()
            .limit(5) // Last 5 orders
            .map(row -> {
                Integer orderId = (Integer) row[0];
                String orderCode = (String) row[1];
                String customerName = (String) row[2];
                Integer productCount = ((Number) row[3]).intValue();
                BigDecimal totalAmount = (BigDecimal) row[4];
                Short trangThai = (Short) row[5];
                Object createdAtObj = row[6];
                java.time.OffsetDateTime createdAt;
                if (createdAtObj instanceof java.time.Instant) {
                    createdAt = ((java.time.Instant) createdAtObj).atOffset(java.time.ZoneOffset.UTC);
                } else if (createdAtObj instanceof java.sql.Timestamp) {
                    createdAt = ((java.sql.Timestamp) createdAtObj).toInstant().atOffset(java.time.ZoneOffset.UTC);
                } else {
                    createdAt = java.time.OffsetDateTime.now();
                }
                
                String status = getStatusText(trangThai);
                String customerAvatar = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=40&h=40&fit=crop&crop=face";

                return RecentOrderStats.builder()
                    .orderId(orderId)
                    .orderCode(orderCode)
                    .customerName(customerName)
                    .customerAvatar(customerAvatar)
                    .productCount(productCount)
                    .totalAmount(totalAmount)
                    .status(status)
                    .createdAt(createdAt)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private String getStatusText(Short trangThai) {
        if (trangThai == null) return "Không xác định";
        switch (trangThai) {
            case 0: return "Chờ xử lý";
            case 1: return "Đã xác nhận";
            case 2: return "Đã giao";
            case 3: return "Đã hủy";
            case 4: return "Hoàn thành";
            case 5: return "Trả hàng";
            default: return "Không xác định";
        }
    }
}
