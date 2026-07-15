package com.example.bookstep.controller;

import com.example.bookstep.exception.BookNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public String handleBookNotFound(
            BookNotFoundException exception,
            HttpServletResponse response,
            Model model) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("message", "対象の書籍が見つかりませんでした。");
        return "error/404";
    }
}
