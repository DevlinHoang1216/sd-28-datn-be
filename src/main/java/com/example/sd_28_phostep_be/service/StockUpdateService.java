package com.example.sd_28_phostep_be.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StockUpdateService {
    
    // In-memory storage for stock updates (in production, use Redis or WebSocket)
    private final Map<Integer, Integer> stockUpdates = new ConcurrentHashMap<>();
    private final List<StockUpdateListener> listeners = new CopyOnWriteArrayList<>();
    
    public interface StockUpdateListener {
        void onStockUpdate(Integer chiTietSanPhamId, Integer newStock);
    }
    
    /**
     * Notify stock update to all listeners
     */
    public void notifyStockUpdate(Integer chiTietSanPhamId, Integer newStock) {
        System.out.println("Broadcasting stock update: ChiTietSanPham ID " + chiTietSanPhamId + " -> " + newStock);
        
        // Store the update
        stockUpdates.put(chiTietSanPhamId, newStock);
        
        // Notify all listeners
        for (StockUpdateListener listener : listeners) {
            try {
                listener.onStockUpdate(chiTietSanPhamId, newStock);
            } catch (Exception e) {
                System.err.println("Error notifying stock update listener: " + e.getMessage());
            }
        }
    }
    
    /**
     * Add a listener for stock updates
     */
    public void addListener(StockUpdateListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a listener
     */
    public void removeListener(StockUpdateListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Get latest stock for a product detail
     */
    public Integer getLatestStock(Integer chiTietSanPhamId) {
        return stockUpdates.get(chiTietSanPhamId);
    }
    
    /**
     * Get all recent stock updates
     */
    public Map<Integer, Integer> getAllStockUpdates() {
        return new ConcurrentHashMap<>(stockUpdates);
    }
    
    /**
     * Clear old updates (call this periodically)
     */
    public void clearOldUpdates() {
        stockUpdates.clear();
    }
}
