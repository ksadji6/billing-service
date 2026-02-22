package com.esmt.billing.controller;

import com.esmt.billing.dto.*;
import com.esmt.billing.entity.Transaction;
import com.esmt.billing.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@Tag(name = "Billing Service", description = "Gestion des comptes et transactions")
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/debit")
    @Operation(summary = "Débiter le compte d'un utilisateur (appelé par Trip Service)")
    public ResponseEntity<DebitResponse> debit(@RequestBody DebitRequest request) {
        return ResponseEntity.ok(billingService.debit(request));
    }

    @PostMapping("/recharge")
    @Operation(summary = "Simuler un rechargement PayDunya")
    public ResponseEntity<String> recharge(@RequestBody CreditRequest request) {
        return ResponseEntity.ok(billingService.simulatePayDunyaRecharge(request));
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "Consulter l'historique des transactions d'un utilisateur")
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(billingService.getTransactionHistory(userId));
    }
}