package com.tomi.jwtsecurity.service;

import com.tomi.jwtsecurity.dto.*;
import com.tomi.jwtsecurity.entity.Role;
import com.tomi.jwtsecurity.entity.User;
import com.tomi.jwtsecurity.repository.RoleRepo;
import com.tomi.jwtsecurity.repository.UserRepo;
import com.tomi.jwtsecurity.security.JwtGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private RoleRepo roleRepo;
    private AuthenticationManager authenticationManager;
    private JwtGenerator jwtGenerator;

    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, ModelMapper modelMapper, RoleRepo roleRepo, @Lazy AuthenticationManager authenticationManager, JwtGenerator jwtGenerator) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.roleRepo = roleRepo;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!!!"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRoleAuthorities(user.getRoles())
        );
    }

    @Override
    public UserDto register(RegisterDto registerDto) {
        if (userRepo.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("Username has been taken!!!");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Role roles = roleRepo.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepo.save(user);

        UserDto newUser = modelMapper.map(user, UserDto.class);

        return newUser;
    }

    @Override
    public AuthResponseDto login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );
            String token = jwtGenerator.generateTokenFromUsername(authentication.getName(), "access");
            String refreshToken = jwtGenerator.generateTokenFromUsername(authentication.getName(), "refresh");
            return new AuthResponseDto(token, refreshToken);
        } catch (Exception ex) {
            throw new RuntimeException("Wrong username or password");
        }
    }

    @Override
    public AuthResponseDto refresh(RefreshDto refreshDto) {
        String refreshToken = refreshDto.getRefreshToken();
        if (StringUtils.hasText(refreshToken) && jwtGenerator.validateToken(refreshToken, "refresh")) {
            String username = jwtGenerator.getUsernameFromJwt(refreshToken);
            User user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!!!"));
            if (user == null) {
                throw new RuntimeException("JWT token is not valid");
            }
            String newAccessToken = jwtGenerator.generateTokenFromUsername(username, "access");
            String newRefreshToken = jwtGenerator.generateTokenFromUsername(username, "refresh");
            return new AuthResponseDto(newAccessToken, newRefreshToken);
        } else {
            throw new RuntimeException("JWT token is not valid");
        }
    }

    private Collection<GrantedAuthority> mapRoleAuthorities(List<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
