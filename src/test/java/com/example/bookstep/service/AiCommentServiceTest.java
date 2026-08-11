package com.example.bookstep.service;

import com.example.bookstep.repository.AiCommentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AiCommentServiceTest {
    @Test
    void APIキー未設定ならユーザー向けメッセージを返す() {
        AiCommentService service = new AiCommentService(mock(AiCommentRepository.class),
                mock(ReadingRecordService.class), new ObjectMapper(), "", "gemini-2.0-flash");

        AiCommentService.GenerationResult result = service.generateAndSave(1L);

        assertThat(result.status()).isEqualTo(AiCommentService.GenerationResult.Status.KEY_MISSING);
        assertThat(result.message()).contains("通常の読書記録は保存されています");
    }
}
