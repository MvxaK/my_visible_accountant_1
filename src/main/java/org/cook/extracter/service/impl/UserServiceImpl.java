package org.cook.extracter.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.cook.extracter.entity.UserEntity;
import org.cook.extracter.mapper.RoleMapper;
import org.cook.extracter.mapper.UserMapper;
import org.cook.extracter.model.Role;
import org.cook.extracter.model.User;
import org.cook.extracter.model.UserCreateRequest;
import org.cook.extracter.repository.RoleRepository;
import org.cook.extracter.repository.UserRepository;
import org.cook.extracter.security.auth.RegisterRequest;
import org.cook.extracter.service.interfaces.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id -> " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toModel)
                .toList();
    }

    @Override
    @Transactional
    public User createUser(UserCreateRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new IllegalArgumentException("This email already in use");

        UserEntity created = new UserEntity();
        created.setEmail(request.getEmail());
        created.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        created.setRole(roleRepository.findByRole(request.getRole()));
        created.setCreatedAt(LocalDateTime.now());

        return userMapper.toModel(userRepository.save(created));
    }

    @Override
    @Transactional
    public void updateUser(Long id, String newEmail, String password, Long requestingUserId, boolean isAdmin) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no user with id -> " + id));

        if (!isAdmin && !userEntity.getId().equals(requestingUserId))
            throw new AccessDeniedException("You don't have permission to update this user");

        if (!passwordEncoder.matches(password, userEntity.getPasswordHash()))
            throw new IllegalArgumentException("Invalid password");

        if (userRepository.findByEmail(newEmail).isPresent())
            throw new IllegalArgumentException("This email already in use");

        if (!newEmail.equals(userEntity.getEmail()))
            userEntity.setEmail(newEmail);

        userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void updatePassword(Long id, String oldPassword, String newPassword, Long requestingUserId, boolean isAdmin) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no user with id -> " + id));

        if (!isAdmin && !userEntity.getId().equals(requestingUserId))
            throw new AccessDeniedException("You don't have permission to update this user's password");

        if (!passwordEncoder.matches(oldPassword, userEntity.getPasswordHash()))
            throw new IllegalArgumentException("Invalid password");

        userEntity.setPasswordHash(passwordEncoder.encode(newPassword));

        userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Long requestingUserId, boolean isAdmin) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no user with id -> " + id));

        if (!isAdmin && !userEntity.getId().equals(requestingUserId))
            throw new AccessDeniedException("You don't have permission to delete this user");

        userRepository.deleteById(id);
    }
}