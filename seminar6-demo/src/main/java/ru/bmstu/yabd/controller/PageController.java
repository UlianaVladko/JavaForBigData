package ru.bmstu.yabd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Streaming Analytics Dashboard");
        return "index"; // templates/index.html
    }
}