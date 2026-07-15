package com.example.bookstep.controller;

import com.example.bookstep.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final BookService bookService;

    public DashboardController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("bookCount", bookService.countAll());
        model.addAttribute("completedBookCount", bookService.countCompleted());
        model.addAttribute("recentBooks", bookService.findRecentBooks());
        return "index";
    }
}
