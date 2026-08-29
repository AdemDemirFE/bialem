package com.bialem.backend.store.web.rest;

import com.bialem.backend.store.domain.StoreBankTransfer;
import com.bialem.backend.store.service.StorePaymentService;
import com.bialem.backend.store.service.dto.StoreRefundRequest;
import com.bialem.backend.store.service.dto.StoreOrderDetailDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/admin/payments")
public class StoreAdminPaymentResource {

    private final StorePaymentService paymentService;

    public StoreAdminPaymentResource(StorePaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/refund")
    public ResponseEntity<StoreOrderDetailDTO> refund(@Valid @RequestBody StoreRefundRequest request) {
        return ResponseEntity.ok(paymentService.refund(request));
    }

    @PostMapping("/bank-transfer/{transferId}/approve")
    public ResponseEntity<StoreBankTransfer> approveBankTransfer(@PathVariable Long transferId, @RequestParam(required = false) String note) {
        return ResponseEntity.ok(paymentService.approveBankTransfer(transferId, note));
    }

    @PostMapping("/bank-transfer/{transferId}/reject")
    public ResponseEntity<StoreBankTransfer> rejectBankTransfer(@PathVariable Long transferId, @RequestParam(required = false) String note) {
        return ResponseEntity.ok(paymentService.rejectBankTransfer(transferId, note));
    }
}
