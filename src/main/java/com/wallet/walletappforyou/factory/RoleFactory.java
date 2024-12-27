package com.wallet.walletappforyou.factory;


import com.wallet.walletappforyou.model.ERole;
import com.wallet.walletappforyou.model.Role;
import com.wallet.walletappforyou.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.relation.RoleNotFoundException;

@Component
public class RoleFactory {
    @Autowired
    RoleRepository roleRepository;

    public Role getInstance(String role) throws RoleNotFoundException {
        switch (role) {
            case "admin" -> {
                return roleRepository.findByName(ERole.ROLE_ADMIN);
            }
            case "user" -> {
                return roleRepository.findByName(ERole.ROLE_USER);
            }

            default -> throw  new RoleNotFoundException("No role found for " +  role);
        }
    }

}