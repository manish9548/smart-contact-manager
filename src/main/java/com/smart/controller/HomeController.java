package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.healper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

    // Password encoder for encrypting password
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Repository for database operations
    @Autowired
    private UserRepository userRepository;


    // Home page handler
    @RequestMapping("/")
    public String home(Model model) {

        model.addAttribute("title", "Home - Smart Contact Manager");

        return "home";
    }


    // About page handler
    @RequestMapping("/about")
    public String about(Model model) {

        model.addAttribute("title", "About - Smart Contact Manager");

        return "about";
    }


    // Signup page handler
    @RequestMapping("/signup")
    public String signup(Model model) {

        model.addAttribute("title", "Register - Smart Contact Manager");

        model.addAttribute("user", new User());

        return "signup";
    }



    // Register user handler
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(

            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
            Model model,
            HttpSession session) {

        try {

            // check agreement checkbox
            if (!agreement) {

                throw new Exception("You must agree to Terms and Conditions");

            }

            // validation errors
            if (result.hasErrors()) {

                model.addAttribute("user", user);

                return "signup";
            }

            // set default values
            user.setRole("ROLE_USER");

            user.setEnabled(true);

            // encrypt password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // save user in database
            userRepository.save(user);

            // success message
            session.setAttribute("message",
                    new Message("Successfully Registered!", "alert-success"));

            // clear form
            model.addAttribute("user", new User());

            return "signup";

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("user", user);

            session.setAttribute("message",
                    new Message("Something went wrong! " + e.getMessage(), "alert-danger"));

            return "signup";
        }
    }



    // Login page handler
    @GetMapping("/login")
    public String customLogin(Model model) {

        model.addAttribute("title", "Login Page");

        return "login";
    }

}
