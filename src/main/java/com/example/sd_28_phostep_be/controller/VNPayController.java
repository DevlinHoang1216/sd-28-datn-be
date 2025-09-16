package com.example.sd_28_phostep_be.controller;

import com.example.sd_28_phostep_be.dto.request.PaymentRequest;
import com.example.sd_28_phostep_be.service.VNPayService;
import com.example.sd_28_phostep_be.service.bill.impl.HoaDonServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private HoaDonServiceImpl hoaDonService;

    @Value("${admin.frontend.url}")
    private String adminFrontendUrl;

    @PostMapping("/vnpay/create")
    public ResponseEntity<?> createVNPayPayment(HttpServletRequest request, @RequestBody PaymentRequest paymentRequest) {
        try {
            // Validate payment request
            if (paymentRequest.getInvoiceId() == null || paymentRequest.getAmount() == null) {
                return ResponseEntity.badRequest().body("Invoice ID and amount are required");
            }

            // Convert amount to long (VNPay expects amount in VND)
            long amount = paymentRequest.getAmount().longValue();
            String orderInfo = "Thanh toan hoa don " + paymentRequest.getInvoiceId();
            String returnUrl = "http://localhost:8080/api/payment/vnpay-return";
            String invoiceId = paymentRequest.getInvoiceId().toString();

            // Create VNPay payment URL
            String paymentUrl = vnPayService.createOrder(request, amount, orderInfo, returnUrl, invoiceId);

            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            response.put("invoiceId", invoiceId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating VNPay payment: " + e.getMessage());
        }
    }

    @GetMapping("/vnpay-return")
    public RedirectView vnPayReturn(HttpServletRequest request) {
        try {
            String invoiceId = vnPayService.orderReturn(request);

            if (invoiceId != null && !invoiceId.isEmpty()) {
                // Payment successful - redirect to sales counter with success message
                return new RedirectView(adminFrontendUrl + "/ban-tai-quay?payment=success&invoiceId=" + invoiceId);
            } else {
                // Payment failed - redirect to sales counter with error message
                return new RedirectView(adminFrontendUrl + "/ban-tai-quay?payment=failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Error occurred - redirect to sales counter with error message
            return new RedirectView(adminFrontendUrl + "/ban-tai-quay?payment=error");
        }
    }

    @PostMapping("/vnpay/process-combined")
    public ResponseEntity<?> processCombinedPayment(HttpServletRequest request, @RequestBody PaymentRequest paymentRequest) {
        try {
            // Validate payment request for combined payment
            if (paymentRequest.getInvoiceId() == null || 
                paymentRequest.getVnpayAmount() == null || 
                paymentRequest.getCashAmount() == null) {
                return ResponseEntity.badRequest().body("Invoice ID, VNPay amount, and cash amount are required");
            }

            // Process cash payment first
            hoaDonService.processCashPayment(paymentRequest.getInvoiceId(), paymentRequest.getCashAmount());

            // Create VNPay payment for remaining amount
            long vnpayAmount = paymentRequest.getVnpayAmount().longValue();
            String orderInfo = "Thanh toan ket hop hoa don " + paymentRequest.getInvoiceId();
            String returnUrl = "http://localhost:8080/api/payment/vnpay-return";
            String invoiceId = paymentRequest.getInvoiceId().toString();

            String paymentUrl = vnPayService.createOrder(request, vnpayAmount, orderInfo, returnUrl, invoiceId);

            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            response.put("invoiceId", invoiceId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing combined payment: " + e.getMessage());
        }
    }
}
