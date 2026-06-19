package com.example.demo;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupForm signupForm, RedirectAttributes redirectAttributes) {
        if (userRepository.existsByEmail(signupForm.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "このメールアドレスは既に登録されています。");
            return "redirect:/signup";
        }
        if (!signupForm.getPassword().equals(signupForm.getPasswordConfirm())) {
            redirectAttributes.addFlashAttribute("errorMessage", "パスワードが一致しません。");
            return "redirect:/signup";
        }

        User user = new User();
        user.setEmail(signupForm.getEmail());
        user.setName(signupForm.getName());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        userRepository.save(user);

        return "redirect:/login?registered";
    }

    public static class SignupForm {
        private String name = "";
        private String email = "";
        private String password = "";
        private String passwordConfirm = "";

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getPasswordConfirm() { return passwordConfirm; }
        public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }
    }
}
