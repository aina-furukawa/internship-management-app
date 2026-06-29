package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
        model.addAttribute("isEdit", false);
        return "form";
    }
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        User user = currentUserService.getCurrentUser();
        Application application = repository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("application", application);
        model.addAttribute("isEdit", true);
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
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute Application form) {
        User user = currentUserService.getCurrentUser();
        Application existing = repository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        existing.setCompanyName(form.getCompanyName());
        existing.setApplicationDate(form.getApplicationDate());
        existing.setStatus(form.getStatus());
        existing.setSkills(form.getSkills());
        existing.setLocation(form.getLocation());
        existing.setHourlyWage(form.getHourlyWage());
        existing.setMemo(form.getMemo());
        repository.save(existing);
        return "redirect:/";
    }
}
