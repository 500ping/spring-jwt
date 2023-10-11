package com.tomi.jwtsecurity.service;

import com.tomi.jwtsecurity.dto.*;
import com.tomi.jwtsecurity.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);
    UserDto register(RegisterDto registerDto);
    AuthResponseDto login(LoginDto loginDto);
    AuthResponseDto refresh(RefreshDto refreshDto);
}
