package com.rodrigodip.workshop_springboot4_jpa.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

        @GetMapping("/")
        public String index(@RequestParam(name = "tab", required = false, defaultValue = "client") String tab,
                        Model model) {
                model.addAttribute("initialTab", "admin".equalsIgnoreCase(tab) ? "admin" : "client");
                return "index";
        }
}
