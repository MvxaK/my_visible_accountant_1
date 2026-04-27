package org.cook.extracter.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.cook.extracter.entity.RoleEntity;
import org.cook.extracter.entity.UserEntity;
import org.cook.extracter.model.Role;
import org.cook.extracter.repository.RoleRepository;
import org.cook.extracter.repository.UserRepository;
import org.cook.extracter.security.auth.AuthResponse;
import org.cook.extracter.security.auth.LoginRequest;
import org.cook.extracter.security.auth.RegisterRequest;
import org.cook.extracter.security.jwt.JwtService;
import org.cook.extracter.service.interfaces.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtService.generateToken(userDetails);

        UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String role = userEntity.getRole().getRole().name();

        return new AuthResponse(jwt, "Bearer", userEntity.getEmail(), userEntity.getId(), role);
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already exists");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(registerRequest.getEmail());
        userEntity.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        userEntity.setCreatedAt(LocalDateTime.now());

        RoleEntity userRole = roleRepository.findByRole(Role.ROLE_USER);
        userEntity.setRole(userRole);

        UserEntity savedUser = userRepository.save(userEntity);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), registerRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtService.generateToken(userDetails);

        String role = savedUser.getRole().getRole().name();

        return new AuthResponse(jwt, "Bearer", savedUser.getEmail(), savedUser.getId(), role);
    }

}
