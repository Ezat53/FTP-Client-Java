package com.xfer.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            if (statusCode == 404) {
                model.addAttribute("error", "Sayfa bulunamadı");
                return "404";
            } else if (statusCode == 500) {
                model.addAttribute("error", "Sunucu hatası oluştu");
                return "500";
            }
        }
        
        model.addAttribute("error", "Bilinmeyen bir hata oluştu");
        return "500";
    }

    public String getErrorPath() {
        return "/error";
    }
}
