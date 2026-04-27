package org.cook.extracter.service;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.entity.UserEntity;
import org.cook.extracter.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public void generateAndSendCode(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new RuntimeException("Пользователь не найден");
        }

        String code = String.format("%06d", new Random().nextInt(1000000));
        otpStorage.put(email, code);

        emailService.sendOtpCode(email, code);

        CompletableFuture.delayedExecutor(5, TimeUnit.MINUTES).execute(() -> {
            otpStorage.remove(email);
        });
    }

    public void resetPassword(String email, String code, String newPassword) {
        String savedCode = otpStorage.get(email);

        if (savedCode == null || !savedCode.equals(code)) {
            throw new RuntimeException("Неверный или просроченный код");
        }

        UserEntity user = userRepository.findByEmail(email).get();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpStorage.remove(email);
    }
}
