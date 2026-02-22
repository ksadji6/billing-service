package com.esmt.billing.service;

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

    /*
     * Débite le compte de l'utilisateur après un trajet.
     * Appelé par le Trip-Service via Feign.
     */
    @Transactional
    public DebitResponse debit(DebitRequest request) {
        log.info("Tentative de débit: {} XOF pour userId={}", request.getAmount(), request.getUserId());

        Account account = accountRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Compte introuvable pour l'utilisateur"));

        BigDecimal balanceBefore = account.getBalance();

        // vérifie le solde
        account.debit(request.getAmount());
        accountRepository.save(account);

        // Enregistrement de la transaction pour l'historique
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

        return DebitResponse.builder()
                .txnRef(txn.getTxnRef())
                .status("SUCCESS")
                .newBalance(account.getBalance())
                .fallback(request.isFallback())
                .build();
    }

    /**
     * SIMULATION de rechargement PayDunya
     */
    @Transactional
    public String simulatePayDunyaRecharge(CreditRequest request) {
        log.info("Simulation rechargement PayDunya: {} XOF pour userId={}", request.getAmount(), request.getUserId());

        Account account = accountRepository.findByUserId(request.getUserId())
                .orElseGet(() -> {
                    log.info("Premier rechargement : création du compte pour l'userId={}", request.getUserId());
                    return Account.builder().userId(request.getUserId()).build();
                });

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

        return "Rechargement réussi. Nouveau solde: " + account.getBalance() + " XOF";
    }

    public List<Transaction> getTransactionHistory(Long userId) {
        return transactionRepository.findByAccountUserIdOrderByCreatedAtDesc(userId);
    }
}