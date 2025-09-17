package com.xfer.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xfer.service.AuthService;

@Controller
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @GetMapping("/")
    public String index() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "index";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Geçersiz kullanıcı adı veya şifre");
        }
        return "login";
    }
    
    
    @GetMapping("/test")
    public String test() {
        return "test";
    }
    
    @GetMapping("/browse/{accountId}")
    public String browse(@PathVariable Long accountId, Model model, Authentication authentication) {
        // TODO: Implement browse functionality
        model.addAttribute("accountId", accountId);
        return "browse";
    }
    
    @GetMapping("/add-account")
    public String addAccountForm() {
        return "add_account";
    }
    
    @PostMapping("/add-account")
    public String addAccount(@RequestParam Map<String, String> params, 
                            Authentication authentication, RedirectAttributes redirectAttributes) {
        // TODO: Implement add account functionality
        redirectAttributes.addFlashAttribute("success", "Hesap eklendi");
        return "redirect:/dashboard";
    }
}
