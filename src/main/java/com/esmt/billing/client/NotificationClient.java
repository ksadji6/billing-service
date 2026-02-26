package com.esmt.billing.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/notifications/low-balance-alert")
    void sendLowBalanceAlert(
            @RequestParam("passNumber") String passNumber,
            @RequestParam("balance") double balance);

    @PostMapping("/api/notifications/recharge-confirmation")
    void sendRechargeConfirmation(
            @RequestParam("passNumber") String passNumber,
            @RequestParam("amount") double amount,
            @RequestParam("newBalance") double newBalance);
}