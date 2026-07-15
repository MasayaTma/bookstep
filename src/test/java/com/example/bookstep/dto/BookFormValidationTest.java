package com.example.bookstep.dto;

import com.example.bookstep.entity.BookStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookFormValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void タイトルが空の場合は登録できない() {
        BookForm form = new BookForm();
        form.setTitle(" ");
        form.setStatus(BookStatus.UNREAD);

        assertThat(validator.validate(form))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("title"));
    }

    @Test
    void 総ページ数は一以上である必要がある() {
        BookForm form = new BookForm();
        form.setTitle("BookStepの本");
        form.setStatus(BookStatus.UNREAD);
        form.setTotalPages(0);

        assertThat(validator.validate(form))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("totalPages"));
    }
}
