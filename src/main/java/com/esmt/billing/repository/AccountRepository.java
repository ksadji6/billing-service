package com.esmt.billing.repository;

import com.esmt.billing.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Utilisé pour récupérer le solde et l'état du compte (Actif/Gelé)
    Optional<Account> findByUserId(Long userId);
}