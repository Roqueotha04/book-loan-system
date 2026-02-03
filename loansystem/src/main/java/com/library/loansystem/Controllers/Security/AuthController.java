package com.library.loansystem.Controllers.Security;

import com.library.loansystem.DTO.Security.AuthLoginRequest;
import com.library.loansystem.DTO.Security.AuthRegisterRequest;
import com.library.loansystem.DTO.Security.AuthResponse;
import com.library.loansystem.Services.Security.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserDetailServiceImpl userDetailServiceImpl;

    public AuthController(UserDetailServiceImpl userDetailServiceImpl) {
        this.userDetailServiceImpl = userDetailServiceImpl;
    }

    @PostMapping("sign-up")
    public ResponseEntity<AuthResponse> register (@Valid @RequestBody AuthRegisterRequest authRegisterRequest){
        return new ResponseEntity<>(this.userDetailServiceImpl.createUser(authRegisterRequest), HttpStatus.CREATED);

    }

    @PostMapping("log-in")
    public ResponseEntity<AuthResponse> login (@Valid @RequestBody AuthLoginRequest authLoginRequest){
        return new ResponseEntity<>(userDetailServiceImpl.login(authLoginRequest), HttpStatus.OK);

    }
}
