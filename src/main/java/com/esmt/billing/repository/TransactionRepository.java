package com.esmt.billing.repository;

import com.esmt.billing.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Récupère l'historique financier complet d'un utilisateur.

    List<Transaction> findByAccountUserIdOrderByCreatedAtDesc(Long userId);
}