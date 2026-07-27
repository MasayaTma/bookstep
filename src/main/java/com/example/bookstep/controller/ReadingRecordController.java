package com.example.bookstep.controller;

import com.example.bookstep.dto.ReadingRecordForm;
import com.example.bookstep.entity.ReadingRecord;
import com.example.bookstep.service.BookService;
import com.example.bookstep.service.ReadingRecordService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReadingRecordController {

    private final ReadingRecordService readingRecordService;
    private final BookService bookService;

    public ReadingRecordController(ReadingRecordService readingRecordService, BookService bookService) {
        this.readingRecordService = readingRecordService;
        this.bookService = bookService;
    }

    @GetMapping("/reading-records")
    public String list(Model model) {
        model.addAttribute("records", readingRecordService.findAll());
        return "reading-records/list";
    }

    @GetMapping("/reading-records/new")
    public String createForm(@RequestParam(required = false) Long bookId, Model model) {
        ReadingRecordForm form = new ReadingRecordForm();
        if (bookId != null) {
            bookService.findById(bookId);
            form.setBookId(bookId);
        }
        prepareForm(model, form, null, "読書記録を追加");
        return "reading-records/form";
    }

    @GetMapping("/books/{bookId}/reading-records/new")
    public String createFormForBook(@PathVariable Long bookId, Model model) {
        return createForm(bookId, model);
    }

    @PostMapping("/reading-records")
    public String create(@Valid @ModelAttribute ReadingRecordForm readingRecordForm,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, readingRecordForm, null, "読書記録を追加");
            return "reading-records/form";
        }
        ReadingRecord saved = readingRecordService.create(readingRecordForm);
        redirectAttributes.addFlashAttribute("successMessage", "読書記録を保存しました。");
        return "redirect:/books/" + saved.getBook().getId();
    }

    @GetMapping("/reading-records/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ReadingRecord record = readingRecordService.findById(id);
        prepareForm(model, ReadingRecordForm.from(record), id, "読書記録を編集");
        return "reading-records/form";
    }

    @PostMapping("/reading-records/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute ReadingRecordForm readingRecordForm,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, readingRecordForm, id, "読書記録を編集");
            return "reading-records/form";
        }
        ReadingRecord updated = readingRecordService.update(id, readingRecordForm);
        redirectAttributes.addFlashAttribute("successMessage", "読書記録を更新しました。");
        return "redirect:/books/" + updated.getBook().getId();
    }

    @PostMapping("/reading-records/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long bookId = readingRecordService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "読書記録を削除しました。");
        return "redirect:/books/" + bookId;
    }

    private void prepareForm(Model model, ReadingRecordForm form, Long recordId, String pageTitle) {
        model.addAttribute("readingRecordForm", form);
        model.addAttribute("books", bookService.findAll());
        model.addAttribute("recordId", recordId);
        model.addAttribute("pageTitle", pageTitle);
    }
}
