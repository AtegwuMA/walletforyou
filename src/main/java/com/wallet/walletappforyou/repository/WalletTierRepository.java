package com.wallet.walletappforyou.repository;


import com.wallet.walletappforyou.model.WalletTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTierRepository extends JpaRepository<WalletTier, Long> {
    WalletTier findByName(String name);
}
