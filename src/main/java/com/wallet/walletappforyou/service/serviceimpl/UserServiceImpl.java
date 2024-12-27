package com.wallet.walletappforyou.service.serviceimpl;


import com.wallet.walletappforyou.dto.requestdto.LoginRequestDto;
import com.wallet.walletappforyou.dto.requestdto.SignUpRequestDto;
import com.wallet.walletappforyou.dto.responsedto.GenericResponse;
import com.wallet.walletappforyou.dto.responsedto.UserDTOMapper;
import com.wallet.walletappforyou.dto.responsedto.UserSignInResponse;
import com.wallet.walletappforyou.dto.responsedto.UserSignUpResponse;
import com.wallet.walletappforyou.factory.RoleFactory;
import com.wallet.walletappforyou.model.Role;
import com.wallet.walletappforyou.model.User;
import com.wallet.walletappforyou.model.Wallet;
import com.wallet.walletappforyou.repository.UserRepository;
import com.wallet.walletappforyou.repository.WalletRepository;
import com.wallet.walletappforyou.security.UserDetailsImpl;
import com.wallet.walletappforyou.security.jwt.JwtUtils;
import com.wallet.walletappforyou.service.UserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDTOMapper userDTOMapper;

    @Autowired
    private RoleFactory roleFactory;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtil;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public GenericResponse signUp(SignUpRequestDto signUpRequest) {
        // Validate fields using the validator
        Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(signUpRequest);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<SignUpRequestDto> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException(sb.toString().trim());
        }

        // Check if the email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new GenericResponse(01, "Email is already in use", HttpStatus.BAD_REQUEST, null);
        }
        // Create and save a new user
        User newUser = new User();
        newUser.setFirstName(signUpRequest.getFirstName());
        newUser.setLastName(signUpRequest.getLastName());
        newUser.setMiddleName(signUpRequest.getMiddleName());
        newUser.setEmail(signUpRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword())); // Encode password
        newUser.setDateOfBirth(signUpRequest.getDateOfBirth());
        newUser.setAddress(signUpRequest.getAddress());
        newUser.setBvn(signUpRequest.getBvn());
        try {
            newUser.setRoles(determineRoles(signUpRequest.getRoles()));
        } catch (RoleNotFoundException e) {
            return new GenericResponse(01, "Role not found", HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.save(newUser); // Save user to the database
        UserSignUpResponse response = userDTOMapper.apply(user);
        Wallet wallet = new Wallet();
        wallet.setUser(newUser); // Link wallet to user
        wallet.setBalance(0.0);
        wallet.setTier(signUpRequest.getWalletTier());
        walletRepository.save(wallet);
        return new GenericResponse(00, "User registered successfully", HttpStatus.CREATED, response);

    }


    private Set<Role> determineRoles(Set<String> strRoles) throws RoleNotFoundException {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(roleFactory.getInstance("user"));
        } else {
            for (String role : strRoles) {
                roles.add(roleFactory.getInstance(role));
            }
        }
        return roles;
    }

    public GenericResponse login(LoginRequestDto loginRequest) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());


            UserSignInResponse response = UserSignInResponse.builder()
                    .email(userDetails.getEmail())
                    .id(userDetails.getId())
                    .token(jwt)
                    .type("Bearer")
                    .roles(roles)
                    .build();

            // Return response with token
            return new GenericResponse(00, "Login successful", HttpStatus.OK, response);
        } catch (Exception e) {
            return new GenericResponse(02, "Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }
}




//import com.example.martwallet.dto.requestdto.SignUpRequestDto;
//import com.example.martwallet.dto.responsedto.GenericResponse;
//import com.example.martwallet.repository.UserRepository;
//import com.example.martwallet.security.util.JwtUtil;
//import com.example.martwallet.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    public GenericResponse signUp(SignUpRequestDto signUpRequest) {
//        // Check if the email already exists
//        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            return new GenericResponse(01, "Email is already in use", HttpStatus.BAD_REQUEST, null);
//        }
//
//        // Create a new user
//        User newUser = new User();
//        newUser.setFirstName(signUpRequest.getFirstName());
//        newUser.setLastName(signUpRequest.getLastName());
//        newUser.setMiddleName(signUpRequest.getMiddleName());
//        newUser.setEmail(signUpRequest.getEmail());
//        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword())); // Encode password
//        newUser.setDateOfBirth(signUpRequest.getDateOfBirth());
//        newUser.setAddress(signUpRequest.getAddress());
//        newUser.setBvn(signUpRequest.getBvn());
//        newUser.setWalletTier(signUpRequest.getWalletTier());
//
//        userRepository.save(newUser); // Save user to the database
//
//        return new GenericResponse("00", "User registered successfully", HttpStatus.CREATED, null);
//    }
//
//    public GenericResponse login(LoginRequestDto loginRequest) {
//        try {
//            // Authenticate user
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
//            );
//
//            // Load user details
//            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
//            String jwt = jwtUtil.generateToken(userDetails.getUsername());
//
//            return new GenericResponse("00", "Login successful", HttpStatus.OK, jwt);
//        } catch (Exception e) {
//            return new GenericResponse("02", "Invalid email or password", HttpStatus.UNAUTHORIZED, null);
//        }
//    }
//}