package com.tomi.jwtsecurity.controller;

import com.tomi.jwtsecurity.dto.AuthResponseDto;
import com.tomi.jwtsecurity.dto.LoginDto;
import com.tomi.jwtsecurity.dto.RegisterDto;
import com.tomi.jwtsecurity.entity.Role;
import com.tomi.jwtsecurity.entity.User;
import com.tomi.jwtsecurity.repository.RoleRepo;
import com.tomi.jwtsecurity.repository.UserRepo;
import com.tomi.jwtsecurity.security.JwtGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private AuthenticationManager authenticationManager;
    private UserRepo userRepo;
    private RoleRepo roleRepo;
    private PasswordEncoder passwordEncoder;
    private JwtGenerator jwtGenerator;

    public UserController(AuthenticationManager authenticationManager, UserRepo userRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (userRepo.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Role roles = roleRepo.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepo.save(user);

        return new ResponseEntity<>("User registered success", HttpStatus.CREATED);
    }

}
