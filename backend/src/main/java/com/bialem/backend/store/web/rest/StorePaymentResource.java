package com.bialem.backend.store.web.rest;

import com.bialem.backend.store.service.StorePaymentService;
import com.bialem.backend.store.service.dto.*;
import com.bialem.backend.store.domain.StoreBankTransfer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/payments")
public class StorePaymentResource {

    private final StorePaymentService paymentService;

    public StorePaymentResource(StorePaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/order/{orderNumber}")
    public ResponseEntity<StoreOrderDetailDTO> getOrderForPayment(@PathVariable String orderNumber) {
        return ResponseEntity.ok(paymentService.getOrderForPayment(orderNumber));
    }

    @PostMapping("/initiate")
    public ResponseEntity<StorePaymentInitiateResponse> initiate(@Valid @RequestBody StorePaymentInitiateRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(request));
    }

    @PostMapping("/callback/{orderNumber}")
    public ResponseEntity<StoreOrderDetailDTO> callback(
        @PathVariable String orderNumber,
        @RequestParam Map<String, String> params,
        @RequestBody(required = false) String payload
    ) {
        return ResponseEntity.ok(paymentService.handleCallback(orderNumber, params, payload));
    }

    @PostMapping("/webhook/{provider}")
    public ResponseEntity<Void> webhook(@PathVariable String provider, HttpServletRequest request, @RequestBody String body) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        paymentService.handleWebhook(provider, headers, body);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bank-transfer")
    public ResponseEntity<StoreBankTransfer> createBankTransfer(@Valid @RequestBody StoreBankTransferRequest request) {
        return ResponseEntity.ok(paymentService.createBankTransfer(request));
    }
}
