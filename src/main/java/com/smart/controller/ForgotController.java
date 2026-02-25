package com.smart.controller;

import java.util.Random;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

@Controller
public class ForgotController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final Random random = new Random();

    // =========================
    // 1️⃣ Open Forgot Email Page
    // =========================
    @GetMapping("/forgot-password")
    public String openForgotPassword() {
        return "forgot_email_form";
    }

    // =========================
    // 2️⃣ Send OTP
    // =========================
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email") String email,
                          HttpSession session,
                          Model model) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            model.addAttribute("message", "User not found!");
            return "forgot_email_form";
        }

        // Generate 6 digit OTP
        int otp = 100000 + random.nextInt(900000);

        boolean emailSent = emailService.sendOtpEmail(email, String.valueOf(otp));

        if (!emailSent) {
            model.addAttribute("message", "Failed to send OTP. Try again!");
            return "forgot_email_form";
        }

        // Save OTP & email in session
        session.setAttribute("otp", otp);
        session.setAttribute("email", email);

        model.addAttribute("message", "OTP sent successfully!");
        return "verify_otp";
    }

    // =========================
    // 3️⃣ Verify OTP
    // =========================
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp,
                            HttpSession session,
                            Model model) {

        Integer sessionOtp = (Integer) session.getAttribute("otp");

        if (sessionOtp == null) {
            model.addAttribute("message", "Session expired. Try again.");
            return "forgot_email_form";
        }

        if (sessionOtp.equals(otp)) {
            return "reset_password";
        } else {
            model.addAttribute("message", "Invalid OTP!");
            return "verify_otp";
        }
    }

    // =========================
    // 4️⃣ Update Password
    // =========================
    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("password") String password,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session,
                                 Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match!");
            return "reset_password";
        }

        String email = (String) session.getAttribute("email");

        if (email == null) {
            model.addAttribute("message", "Session expired!");
            return "forgot_email_form";
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            model.addAttribute("message", "User not found!");
            return "forgot_email_form";
        }

        // Encode and save new password
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        // Clear session
        session.removeAttribute("otp");
        session.removeAttribute("email");

        return "redirect:/signin?reset=success";
    }
}