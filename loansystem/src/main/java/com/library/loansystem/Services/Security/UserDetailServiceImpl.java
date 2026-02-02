package com.library.loansystem.Services.Security;

import com.library.loansystem.Entities.UserEntity;
import com.library.loansystem.Repositories.UserEntityRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;

    public UserDetailServiceImpl(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException ("User not found with username: " + username));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userEntity.getRoles()
                .forEach(role-> authorityList.add(new SimpleGrantedAuthority(role.getRole())));

        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialsNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList);
    }

    @Bean
    public UserDetailsService userDetailsService(){
        List<UserDetails> userDetailsList = new ArrayList<>();
        userDetailsList.add(User.withUsername("Roque")
                .password("1234")
                .roles("ADMIN")
                .authorities("READ", "CREATE", "UPDATE", "DELETE", "REFACTO")
                .build());
        userDetailsList.add(User.withUsername("Roque 2")
                .password("12345")
                .roles("USER")
                .authorities("READ", "LOAN")
                .build());

        return new InMemoryUserDetailsManager(userDetailsList);
    }
}
