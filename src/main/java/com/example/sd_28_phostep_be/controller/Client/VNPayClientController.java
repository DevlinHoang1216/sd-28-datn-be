package com.example.sd_28_phostep_be.controller.Client;

import com.example.sd_28_phostep_be.dto.sell.request.ClientPaymentRequest;
import com.example.sd_28_phostep_be.service.VNPayService;
import com.example.sd_28_phostep_be.service.sale.Client.impl.BanHangClientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client-payment")
@CrossOrigin(origins = "*")
public class VNPayClientController {

    // Constructor để debug component loading
    public VNPayClientController() {
        System.out.println("=== VNPayClientController LOADED ===");
    }

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private BanHangClientService banHangClientService;

    @Value("${client.frontend.url:http://localhost:5173}")
    private String clientFrontendUrl;

    /**
     * Test endpoint to verify controller is loaded
     */
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "VNPayClientController is working!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Create VNPay payment URL for client checkout
     */
    @PostMapping("/vnpay/create/{hoaDonId}")
    public ResponseEntity<?> createVNPayPayment(
            HttpServletRequest request, 
            @PathVariable Integer hoaDonId,
            @RequestBody ClientPaymentRequest paymentRequest) {
        try {
            System.out.println("=== CLIENT VNPAY CREATE PAYMENT ENDPOINT HIT ===");
            System.out.println("Creating VNPay payment for client - Invoice ID: " + hoaDonId);
            System.out.println("Payment request: " + paymentRequest);
            
            // Validate payment request
            if (hoaDonId == null) {
                return ResponseEntity.badRequest().body("Invoice ID is required");
            }

            // Pre-save customer information to invoice before VNPay redirect
            // This ensures customer data is preserved even if VNPay return fails
            try {
                System.out.println("Pre-saving customer info before VNPay redirect...");
                banHangClientService.updateCustomerInfo(hoaDonId, paymentRequest);
                System.out.println("Customer info pre-saved successfully");
            } catch (Exception e) {
                System.err.println("Warning: Could not pre-save customer info: " + e.getMessage());
                // Continue with VNPay creation even if pre-save fails
            }

            // Get cart total from cart service
            var cartResponse = banHangClientService.layGioHang(hoaDonId);
            BigDecimal cartTotal = cartResponse.getTongTien();
            BigDecimal totalAmount = calculateTotalAmount(cartTotal, paymentRequest);
            
            if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Invalid payment amount");
            }

            // Convert amount to long (VNPay expects amount in VND)
            long amount = totalAmount.longValue();
            String orderInfo = "Thanh toan don hang #" + hoaDonId + " - " + paymentRequest.getTenKhachHang();
            String returnUrl = "http://localhost:8080/api/client-payment/vnpay-return";
            String invoiceId = hoaDonId.toString();

            // Create VNPay payment URL
            String paymentUrl = vnPayService.createOrder(request, amount, orderInfo, returnUrl, invoiceId);

            Map<String, Object> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            response.put("invoiceId", invoiceId);
            response.put("amount", amount);
            response.put("orderInfo", orderInfo);

            System.out.println("VNPay payment URL created: " + paymentUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error creating VNPay payment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating VNPay payment: " + e.getMessage());
        }
    }

    /**
     * Handle VNPay return callback for client
     */
    @GetMapping("/vnpay-return")
    public RedirectView vnPayReturn(HttpServletRequest request) {
        try {
            System.out.println("VNPay return callback received for CLIENT");
            
            // Use custom client VNPay processing instead of shared service
            String invoiceId = processClientVNPayReturn(request);

            if (invoiceId != null && !invoiceId.isEmpty()) {
                System.out.println("VNPay payment successful for client invoice: " + invoiceId);
                
                // Payment successful - redirect to purchase history page
                return new RedirectView(clientFrontendUrl + "/account/purchase-history?payment=vnpay&status=success&invoiceId=" + invoiceId);
            } else {
                System.out.println("VNPay payment failed for client");
                // Payment failed - redirect to client checkout with error
                return new RedirectView(clientFrontendUrl + "/checkout?payment=vnpay&status=failed");
            }
        } catch (Exception e) {
            System.err.println("Error processing client VNPay return: " + e.getMessage());
            e.printStackTrace();
            // Error occurred - redirect to client checkout with error
            return new RedirectView(clientFrontendUrl + "/checkout?payment=vnpay&status=error");
        }
    }

    /**
     * Process VNPay return specifically for client (separate from admin)
     */
    private String processClientVNPayReturn(HttpServletRequest request) {
        try {
            // Debug: Print all parameters
            System.out.println("=== CLIENT VNPAY RETURN DEBUG ===");
            request.getParameterNames().asIterator().forEachRemaining(paramName -> {
                System.out.println(paramName + " = " + request.getParameter(paramName));
            });
            
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus");
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            String vnp_Amount = request.getParameter("vnp_Amount");
            
            System.out.println("Client VNPay - TxnRef: " + vnp_TxnRef + ", Status: " + vnp_TransactionStatus + ", ResponseCode: " + vnp_ResponseCode);
            
            // Extract invoice ID from TxnRef
            String invoiceId = null;
            if (vnp_TxnRef != null && vnp_TxnRef.contains("_")) {
                invoiceId = vnp_TxnRef.split("_")[0];
            }
            
            if (invoiceId == null) {
                System.err.println("CLIENT: Could not extract invoiceId from vnp_TxnRef: " + vnp_TxnRef);
                return null;
            }
            
            // Check if payment is successful
            if ("00".equals(vnp_TransactionStatus) && "00".equals(vnp_ResponseCode)) {
                System.out.println("CLIENT: VNPay payment successful for invoice: " + invoiceId);
                
                try {
                    Integer hoaDonId = Integer.parseInt(invoiceId);
                    
                    // Simply complete the payment - let the service preserve existing customer data
                    // The thanhToan method will use existing HoaDon data and only update payment method
                    ClientPaymentRequest vnpayRequest = ClientPaymentRequest.builder()
                            .phuongThucThanhToan("VNPAY")
                            .ghiChu("Thanh toán qua VNPay")
                            .build();
                    
                    // Complete the payment
                    banHangClientService.thanhToan(hoaDonId, vnpayRequest);
                    System.out.println("CLIENT: VNPay payment completed successfully for invoice: " + invoiceId);
                    
                    return invoiceId;
                } catch (Exception e) {
                    System.err.println("CLIENT: Error completing VNPay payment for invoice " + invoiceId + ": " + e.getMessage());
                    e.printStackTrace();
                    // Even if completion fails, return success since VNPay payment was successful
                    return invoiceId;
                }
            } else {
                System.out.println("CLIENT: VNPay payment failed - Status: " + vnp_TransactionStatus + ", ResponseCode: " + vnp_ResponseCode);
                return null;
            }
        } catch (Exception e) {
            System.err.println("CLIENT: Error processing VNPay return: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get VNPay payment status
     */
    @GetMapping("/vnpay/status/{hoaDonId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable Integer hoaDonId) {
        try {
            // Check invoice status in database
            var cartResponse = banHangClientService.layGioHang(hoaDonId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("invoiceId", hoaDonId);
            
            // Determine status based on invoice data
            if (cartResponse != null) {
                // If cart still exists, payment is pending
                response.put("status", "pending");
                response.put("message", "Payment is still pending");
            } else {
                // If cart is cleared, payment was successful
                response.put("status", "success");
                response.put("message", "Payment completed successfully");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting payment status: " + e.getMessage());
            
            // If cart not found, assume payment was completed
            Map<String, Object> response = new HashMap<>();
            response.put("invoiceId", hoaDonId);
            response.put("status", "success");
            response.put("message", "Payment status unknown, assuming completed");
            
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Calculate total amount from cart total and payment request
     */
    private BigDecimal calculateTotalAmount(BigDecimal cartTotal, ClientPaymentRequest paymentRequest) {
        if (cartTotal == null) {
            cartTotal = BigDecimal.ZERO;
        }
        
        BigDecimal shippingFee = paymentRequest.getPhiVanChuyen() != null ? 
            paymentRequest.getPhiVanChuyen() : BigDecimal.ZERO;
        BigDecimal discount = paymentRequest.getTienGiam() != null ? 
            paymentRequest.getTienGiam() : BigDecimal.ZERO;
            
        BigDecimal totalAmount = cartTotal.add(shippingFee).subtract(discount);
        
        System.out.println("Payment calculation - Cart: " + cartTotal + 
                         ", Shipping: " + shippingFee + 
                         ", Discount: " + discount + 
                         ", Total: " + totalAmount);
                         
        return totalAmount;
    }
}
