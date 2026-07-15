package com.example.bookstep.dto;

import com.example.bookstep.entity.Book;
import com.example.bookstep.entity.BookStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class BookForm {

    @NotBlank(message = "タイトルを入力してください")
    @Size(max = 200, message = "タイトルは200文字以内で入力してください")
    private String title;

    @Size(max = 100, message = "著者名は100文字以内で入力してください")
    private String author;

    @Size(max = 100, message = "カテゴリは100文字以内で入力してください")
    private String category;

    @NotNull(message = "読書状態を選択してください")
    private BookStatus status = BookStatus.UNREAD;

    @Min(value = 1, message = "総ページ数は1以上で入力してください")
    private Integer totalPages;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startedDate;

    @Size(max = 2000, message = "メモは2,000文字以内で入力してください")
    private String memo;

    public static BookForm from(Book book) {
        BookForm form = new BookForm();
        form.setTitle(book.getTitle());
        form.setAuthor(book.getAuthor());
        form.setCategory(book.getCategory());
        form.setStatus(book.getStatus());
        form.setTotalPages(book.getTotalPages());
        form.setStartedDate(book.getStartedDate());
        form.setMemo(book.getMemo());
        return form;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public LocalDate getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(LocalDate startedDate) {
        this.startedDate = startedDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
