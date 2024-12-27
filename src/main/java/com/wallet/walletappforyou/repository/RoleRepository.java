package com.wallet.walletappforyou.repository;


import com.wallet.walletappforyou.model.ERole;
import com.wallet.walletappforyou.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(ERole name);
}
