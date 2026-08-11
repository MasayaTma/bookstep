package com.example.bookstep.dto;

import com.example.bookstep.entity.ReadingRecord;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ReadingRecordForm {

    @NotNull(message = "書籍を選択してください")
    private Long bookId;

    @NotNull(message = "読書日を入力してください")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate readingDate = LocalDate.now();

    @NotNull(message = "読書時間を入力してください")
    @Min(value = 1, message = "読書時間は1分以上で入力してください")
    @Max(value = 1440, message = "読書時間は1,440分以内で入力してください")
    private Integer readingMinutes;

    @Min(value = 0, message = "開始ページは0以上で入力してください")
    private Integer startPage;

    @Min(value = 0, message = "終了ページは0以上で入力してください")
    private Integer endPage;

    @Size(max = 3000, message = "感想は3,000文字以内で入力してください")
    private String reflection;

    private boolean generateAiComment;

    @AssertTrue(message = "終了ページは開始ページ以上で入力してください")
    public boolean isPageRangeValid() {
        return startPage == null || endPage == null || endPage >= startPage;
    }

    public static ReadingRecordForm from(ReadingRecord record) {
        ReadingRecordForm form = new ReadingRecordForm();
        form.bookId = record.getBook().getId();
        form.readingDate = record.getReadingDate();
        form.readingMinutes = record.getReadingMinutes();
        form.startPage = record.getStartPage();
        form.endPage = record.getEndPage();
        form.reflection = record.getReflection();
        return form;
    }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }
    public Integer getReadingMinutes() { return readingMinutes; }
    public void setReadingMinutes(Integer readingMinutes) { this.readingMinutes = readingMinutes; }
    public Integer getStartPage() { return startPage; }
    public void setStartPage(Integer startPage) { this.startPage = startPage; }
    public Integer getEndPage() { return endPage; }
    public void setEndPage(Integer endPage) { this.endPage = endPage; }
    public String getReflection() { return reflection; }
    public void setReflection(String reflection) { this.reflection = reflection; }
    public boolean isGenerateAiComment() { return generateAiComment; }
    public void setGenerateAiComment(boolean generateAiComment) { this.generateAiComment = generateAiComment; }
}
