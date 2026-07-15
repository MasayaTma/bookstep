package com.example.bookstep.controller;

import com.example.bookstep.dto.BookForm;
import com.example.bookstep.entity.Book;
import com.example.bookstep.entity.BookStatus;
import com.example.bookstep.service.BookService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("statuses")
    public BookStatus[] statuses() {
        return BookStatus.values();
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        model.addAttribute("bookId", null);
        model.addAttribute("pageTitle", "書籍を登録");
        return "books/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute BookForm bookForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookId", null);
            model.addAttribute("pageTitle", "書籍を登録");
            return "books/form";
        }

        Book savedBook = bookService.create(bookForm);
        redirectAttributes.addFlashAttribute("successMessage", "書籍を登録しました。");
        return "redirect:/books/" + savedBook.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        return "books/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        model.addAttribute("bookForm", BookForm.from(book));
        model.addAttribute("bookId", id);
        model.addAttribute("pageTitle", "書籍情報を編集");
        return "books/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute BookForm bookForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", bookService.findById(id));
            model.addAttribute("bookId", id);
            model.addAttribute("pageTitle", "書籍情報を編集");
            return "books/form";
        }

        bookService.update(id, bookForm);
        redirectAttributes.addFlashAttribute("successMessage", "書籍情報を更新しました。");
        return "redirect:/books/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "書籍を削除しました。");
        return "redirect:/books";
    }
}
