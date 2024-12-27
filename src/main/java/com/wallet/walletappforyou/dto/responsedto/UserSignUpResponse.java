package com.wallet.walletappforyou.dto.responsedto;

import com.wallet.walletappforyou.model.Wallet;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class UserSignUpResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String dateOfBirth;
    private String address;
    private String bvn;
    private Date createdAt;
    private List<Wallet> wallets;

}
