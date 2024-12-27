package com.wallet.walletappforyou.service;


import com.wallet.walletappforyou.dto.requestdto.LoginRequestDto;
import com.wallet.walletappforyou.dto.requestdto.SignUpRequestDto;
import com.wallet.walletappforyou.dto.responsedto.GenericResponse;

public interface UserService {
    GenericResponse signUp(SignUpRequestDto signUpRequest);

    GenericResponse login(LoginRequestDto loginRequest);
}
