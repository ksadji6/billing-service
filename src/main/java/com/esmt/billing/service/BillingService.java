package com.esmt.billing.service;

import com.esmt.billing.client.NotificationClient;
import com.esmt.billing.dto.*;
import com.esmt.billing.entity.*;
import com.esmt.billing.repository.AccountRepository;
import com.esmt.billing.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationClient notificationClient;

    @Transactional
    public DebitResponse debit(DebitRequest request) {
        log.info("Tentative de débit: {} XOF pour userId={}", request.getAmount(), request.getUserId());

        Account account = accountRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Compte introuvable pour l'utilisateur"));

        BigDecimal balanceBefore = account.getBalance();
        account.debit(request.getAmount());
        accountRepository.save(account);

        Transaction txn = Transaction.builder()
                .account(account)
                .amount(request.getAmount())
                .type(TxnType.DEBIT)
                .tripRef(request.getTripRef())
                .balanceBefore(balanceBefore)
                .balanceAfter(account.getBalance())
                .isFallback(request.isFallback())
                .status(TxnStatus.SUCCESS)
                .build();

        transactionRepository.save(txn);

        // AJOUT NOTIF ALERTE SOLDE FAIBLE
        try {
            // Si le solde est < 1000 XOF, on alerte
            if (account.getBalance().compareTo(new BigDecimal("1000")) < 0) {
                notificationClient.sendLowBalanceAlert(
                        "PASS-" + request.getUserId(),
                        account.getBalance().doubleValue()
                );
            }
        } catch (Exception e) {
            log.warn("Notification d'alerte non envoyée (Service indisponible)");
        }

        return DebitResponse.builder()
                .txnRef(txn.getTxnRef())
                .status("SUCCESS")
                .newBalance(account.getBalance())
                .fallback(request.isFallback())
                .build();
    }

    @Transactional
    public String simulatePayDunyaRecharge(CreditRequest request) {
        log.info("Simulation rechargement PayDunya: {} XOF pour userId={}", request.getAmount(), request.getUserId());

        Account account = accountRepository.findByUserId(request.getUserId())
                .orElseGet(() -> Account.builder().userId(request.getUserId()).build());

        BigDecimal balanceBefore = account.getBalance();
        account.credit(request.getAmount());
        accountRepository.save(account);

        Transaction txn = Transaction.builder()
                .account(account)
                .amount(request.getAmount())
                .type(TxnType.CREDIT)
                .balanceBefore(balanceBefore)
                .balanceAfter(account.getBalance())
                .status(TxnStatus.SUCCESS)
                .build();

        transactionRepository.save(txn);

        // AJOUT NOTIF CONFIRMATION RECHARGE
        try {
            notificationClient.sendRechargeConfirmation(
                    "PASS-" + request.getUserId(),
                    request.getAmount().doubleValue(),
                    account.getBalance().doubleValue()
            );
        } catch (Exception e) {
            log.warn("Confirmation de recharge non envoyée (Service indisponible)");
        }

        return "Rechargement réussi. Nouveau solde: " + account.getBalance() + " XOF";
    }

    public List<Transaction> getTransactionHistory(Long userId) {
        return transactionRepository.findByAccountUserIdOrderByCreatedAtDesc(userId);
    }
}