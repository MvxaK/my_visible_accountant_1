package org.cook.extracter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendLimitExceededAlert(String email, String categoryName, BigDecimal currentSpending, BigDecimal threshold) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Лимит расходов превышен: " + categoryName);
        message.setText("Внимание!\n\nВы превысили установленный лимит по категории '" + categoryName + "'.\n" +
                "Текущие расходы: " + currentSpending + "\n" +
                "Ваш лимит: " + threshold + "\n\n" +
                "Пожалуйста, проверьте свои транзакции в приложении MY Visible Accountant.");
        mailSender.send(message);
    }

    public void sendOtpCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Код подтверждения — FinanceScan");
        message.setText("Ваш код для сброса пароля: " + code +
                "\nКод действителен в течение 5 минут.");
        mailSender.send(message);
    }

}
