package org.cook.extracter.controller.web;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthWebController {

    private final PasswordResetService resetService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @GetMapping("/forgot-password")
    public String forgotPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password/send")
    public String sendCode(@RequestParam String email, Model model) {
        try {
            resetService.generateAndSendCode(email);
            model.addAttribute("email", email);
            model.addAttribute("message", "Код отправлен на вашу почту");

            return "auth/reset-password";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());

            return "auth/forgot-password";
        }
    }

    @PostMapping("/forgot-password/reset")
    public String resetPassword(@RequestParam String email, @RequestParam String code, @RequestParam String password, Model model) {
        try {
            resetService.resetPassword(email, code, password);

            return "redirect:/login?resetSuccess";
        } catch (Exception e) {
            model.addAttribute("email", email);
            model.addAttribute("error", e.getMessage());

            return "auth/reset-password";
        }
    }
}