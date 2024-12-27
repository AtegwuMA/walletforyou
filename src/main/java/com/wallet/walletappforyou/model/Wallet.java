package com.wallet.walletappforyou.model;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tier; // e.g., BASIC, SILVER or GOLD
    private double balance = 0.0; // Initial balance set to 0
    private LocalDateTime lastFundingTime;
    private LocalDateTime lastWithdrawTime;
    private LocalDateTime lastTransferFundTime;
    private double dailyFundingAmount;
    private double weeklyFundingAmount;
    private double dailyTransferAmount;
    private double weeklyTransferAmount;
    private double dailyWithdrawAmount;
    private double weeklyWithdrawAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
