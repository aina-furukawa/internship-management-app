package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ApplicationController {

    private final ApplicationRepository repository;
    private final CurrentUserService currentUserService;

    public ApplicationController(ApplicationRepository repository, CurrentUserService currentUserService) {
        this.repository = repository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/")
    public String list(Model model) {
        User user = currentUserService.getCurrentUser();
        model.addAttribute("applications", repository.findByUserIdOrderByApplicationDateDesc(user.getId()));
        model.addAttribute("currentUserName", user.getName());
        return "list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("application", new Application());
        return "form";
    }

    @PostMapping("/")
    public String create(@ModelAttribute Application application) {
        User user = currentUserService.getCurrentUser();
        application.setUserId(user.getId());
        repository.save(application);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        User user = currentUserService.getCurrentUser();
        repository.findByIdAndUserId(id, user.getId()).ifPresent(repository::delete);
        return "redirect:/";
    }
}
