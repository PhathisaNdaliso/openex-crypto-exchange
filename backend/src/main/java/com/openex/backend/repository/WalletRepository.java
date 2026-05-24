package com.openex.backend.repository;

import com.openex.backend.model.Wallet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByUserId(Long userId);

    Optional<Wallet> findByUserIdAndCurrency(Long userId, String currency);
}
