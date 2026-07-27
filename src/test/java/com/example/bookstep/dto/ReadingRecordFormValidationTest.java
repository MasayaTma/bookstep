package com.example.bookstep.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ReadingRecordFormValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void 必須項目があれば検証を通過する() {
        ReadingRecordForm form = validForm();
        assertThat(validator.validate(form)).isEmpty();
    }

    @Test
    void 終了ページが開始ページより前ならエラーになる() {
        ReadingRecordForm form = validForm();
        form.setStartPage(100);
        form.setEndPage(90);
        assertThat(validator.validate(form))
                .anySatisfy(error -> assertThat(error.getMessage()).contains("終了ページ"));
    }

    private ReadingRecordForm validForm() {
        ReadingRecordForm form = new ReadingRecordForm();
        form.setBookId(1L);
        form.setReadingDate(LocalDate.now());
        form.setReadingMinutes(30);
        return form;
    }
}
