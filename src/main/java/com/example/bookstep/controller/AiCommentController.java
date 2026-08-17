package com.example.bookstep.controller;

import com.example.bookstep.entity.ReadingRecord;
import com.example.bookstep.service.AiCommentService;
import com.example.bookstep.service.ReadingRecordService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AiCommentController {
    private final AiCommentService aiCommentService;
    private final ReadingRecordService readingRecordService;

    public AiCommentController(AiCommentService aiCommentService, ReadingRecordService readingRecordService) {
        this.aiCommentService = aiCommentService;
        this.readingRecordService = readingRecordService;
    }

    @PostMapping("/reading-records/{id}/ai-comment")
    public String generate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ReadingRecord record = readingRecordService.findById(id);
        AiCommentService.GenerationResult result = aiCommentService.generateAndSave(id);
        if (result.status() == AiCommentService.GenerationResult.Status.SUCCESS) {
            redirectAttributes.addFlashAttribute("successMessage", "AIコメントを生成しました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", result.message());
        }
        return "redirect:/books/" + record.getBook().getId();
    }
}
