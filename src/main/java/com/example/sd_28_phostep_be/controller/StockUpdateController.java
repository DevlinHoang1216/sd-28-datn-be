package com.example.sd_28_phostep_be.controller;

import com.example.sd_28_phostep_be.service.StockUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stock-updates")
@CrossOrigin(origins = "*")
public class StockUpdateController {

    @Autowired
    private StockUpdateService stockUpdateService;

    /**
     * Get all recent stock updates
     */
    @GetMapping
    public ResponseEntity<Map<Integer, Integer>> getAllStockUpdates() {
        Map<Integer, Integer> updates = stockUpdateService.getAllStockUpdates();
        return ResponseEntity.ok(updates);
    }

    /**
     * Get stock for specific product detail
     */
    @GetMapping("/{chiTietSanPhamId}")
    public ResponseEntity<Integer> getStockForProduct(@PathVariable Integer chiTietSanPhamId) {
        Integer stock = stockUpdateService.getLatestStock(chiTietSanPhamId);
        if (stock != null) {
            return ResponseEntity.ok(stock);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Clear old stock updates (admin endpoint)
     */
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearOldUpdates() {
        stockUpdateService.clearOldUpdates();
        return ResponseEntity.ok("Stock updates cleared successfully");
    }
}
