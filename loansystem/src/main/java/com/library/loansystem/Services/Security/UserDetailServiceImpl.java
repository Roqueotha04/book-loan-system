package com.library.loansystem.Services.Security;

import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.DTO.Response.UserEntityResponse;
import com.library.loansystem.DTO.Security.AuthLoginRequest;
import com.library.loansystem.DTO.Security.AuthRegisterRequest;
import com.library.loansystem.DTO.Security.AuthResponse;
import com.library.loansystem.Entities.Role;
import com.library.loansystem.Entities.UserEntity;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.RoleRepository;
import com.library.loansystem.Repositories.UserEntityRepository;
import com.library.loansystem.Services.UserEntityService;
import com.library.loansystem.Utils.JwtUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class UserDetailServiceImpl implements UserDetailsService {

   // private final UserEntityRepository userEntityRepository;
    private final UserEntityService userEntityService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserDetailServiceImpl(UserEntityRepository userEntityRepository, UserEntityService userEntityService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
       // this.userEntityRepository = userEntityRepository;
        this.userEntityService = userEntityService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userEntityService.findByUsername(username);

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userEntity.getRoles()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority(role.getRole())));

        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialsNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList);
    }

    public AuthResponse login (AuthLoginRequest authLoginRequest){
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);
        return new AuthResponse(username, "User logged succesfully", accessToken, true);
    }

    public Authentication authenticate (String username, String password){
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null){
            throw new BadCredentialsException ("Invalid username");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
    }

    public AuthResponse createUser(AuthRegisterRequest authRegisterRequest){
        saveUser(authRegisterRequest);
        Authentication authentication = this.authenticate(authRegisterRequest.username(), authRegisterRequest.password());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.createToken(authentication);
        return new AuthResponse(authRegisterRequest.username(), "User created", accessToken, true);

    }

    public void saveUser(AuthRegisterRequest authRegisterRequest){
        UserEntityRequest userEntityRequest = new UserEntityRequest(authRegisterRequest.email(), authRegisterRequest.username(), passwordEncoder.encode(authRegisterRequest.password()));
        userEntityService.save(userEntityRequest);
    }
}