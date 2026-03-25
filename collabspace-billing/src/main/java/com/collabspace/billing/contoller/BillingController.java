package com.collabspace.billing.contoller;

import com.collabspace.billing.dto.OrderRequest;
import com.collabspace.billing.dto.OrderResponse;
import com.collabspace.billing.entity.Subscription;
import com.collabspace.billing.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest request,
            Authentication auth) throws Exception {
        return ResponseEntity.ok(
            billingService.createOrder(request, auth.getName()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload) {
        billingService.handleWebhook(payload);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/plan/{workspaceId}")
    public ResponseEntity<Subscription> getPlan(
            @PathVariable Long workspaceId) {
        return ResponseEntity.ok(billingService.getPlan(workspaceId));
    }
}
