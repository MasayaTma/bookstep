package com.example.bookstep.controller;

import com.example.bookstep.entity.BookStatus;
import com.example.bookstep.repository.BookRepository;
import com.example.bookstep.repository.ReadingRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class BookManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReadingRecordRepository readingRecordRepository;

    @BeforeEach
    void cleanDatabase() {
        readingRecordRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void ダッシュボードを表示できる() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("今日の一ページ")));
    }

    @Test
    void 正常な書籍を登録できる() throws Exception {
        mockMvc.perform(post("/books")
                        .param("title", "嫌われる勇気")
                        .param("author", "岸見一郎、古賀史健")
                        .param("status", BookStatus.UNREAD.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/books/*"));

        assertThat(bookRepository.findAll())
                .singleElement()
                .satisfies(book -> assertThat(book.getTitle()).isEqualTo("嫌われる勇気"));
    }

    @Test
    void タイトルが空なら登録画面へ戻る() throws Exception {
        mockMvc.perform(post("/books")
                        .param("title", "")
                        .param("status", BookStatus.UNREAD.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("タイトルを入力してください")));

        assertThat(bookRepository.count()).isZero();
    }

    @Test
    void 書籍の詳細確認と編集と削除ができる() throws Exception {
        mockMvc.perform(post("/books")
                        .param("title", "リファクタリング")
                        .param("author", "Martin Fowler")
                        .param("status", BookStatus.READING.name())
                        .param("totalPages", "400"))
                .andExpect(status().is3xxRedirection());

        Long bookId = bookRepository.findAll().getFirst().getId();

        mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("books/detail"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("リファクタリング")));

        mockMvc.perform(get("/books/{id}/edit", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"));

        mockMvc.perform(post("/books/{id}", bookId)
                        .param("title", "リファクタリング 第2版")
                        .param("author", "Martin Fowler")
                        .param("status", BookStatus.COMPLETED.name())
                        .param("totalPages", "422"))
                .andExpect(status().is3xxRedirection());

        assertThat(bookRepository.findById(bookId))
                .get()
                .satisfies(book -> {
                    assertThat(book.getTitle()).isEqualTo("リファクタリング 第2版");
                    assertThat(book.getStatus()).isEqualTo(BookStatus.COMPLETED);
                    assertThat(book.getCompletedDate()).isNotNull();
                });

        mockMvc.perform(post("/books/{id}/delete", bookId))
                .andExpect(status().is3xxRedirection());

        assertThat(bookRepository.findById(bookId)).isEmpty();
    }

    @Test
    void 読書記録の登録表示編集削除ができる() throws Exception {
        mockMvc.perform(post("/books")
                        .param("title", "達人プログラマー")
                        .param("status", BookStatus.READING.name()))
                .andExpect(status().is3xxRedirection());
        Long bookId = bookRepository.findAll().getFirst().getId();

        mockMvc.perform(post("/reading-records")
                        .param("bookId", bookId.toString())
                        .param("readingDate", "2026-07-27")
                        .param("readingMinutes", "45")
                        .param("startPage", "10")
                        .param("endPage", "35")
                        .param("reflection", "小さな改善を積み重ねる"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/books/*"));

        var record = readingRecordRepository.findAll().getFirst();
        mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("45分")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("小さな改善")));

        mockMvc.perform(post("/reading-records/{id}", record.getId())
                        .param("bookId", bookId.toString())
                        .param("readingDate", "2026-07-27")
                        .param("readingMinutes", "60")
                        .param("startPage", "10")
                        .param("endPage", "50")
                        .param("reflection", "更新した感想"))
                .andExpect(status().is3xxRedirection());
        assertThat(readingRecordRepository.findById(record.getId()))
                .get().satisfies(updated -> assertThat(updated.getReadingMinutes()).isEqualTo(60));

        mockMvc.perform(post("/reading-records/{id}/delete", record.getId()))
                .andExpect(status().is3xxRedirection());
        assertThat(readingRecordRepository.count()).isZero();
    }
}
