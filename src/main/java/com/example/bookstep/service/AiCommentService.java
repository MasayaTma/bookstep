package com.example.bookstep.service;

import com.example.bookstep.entity.AiComment;
import com.example.bookstep.entity.Book;
import com.example.bookstep.entity.ReadingRecord;
import com.example.bookstep.repository.AiCommentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Service
public class AiCommentService {
    public static final String KEY_MISSING_MESSAGE = "AI APIキーが設定されていないため、コメントを生成できませんでした。通常の読書記録は保存されています。";
    public static final String FAILURE_MESSAGE = "AIコメントの生成に失敗しました。時間を置いて再度お試しください。";

    private final AiCommentRepository aiCommentRepository;
    private final ReadingRecordService readingRecordService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;
    private final String model;

    public AiCommentService(AiCommentRepository aiCommentRepository, ReadingRecordService readingRecordService,
                            ObjectMapper objectMapper,
                            @Value("${bookstep.ai.gemini-api-key:}") String apiKey,
                            @Value("${bookstep.ai.model:gemini-2.0-flash}") String model) {
        this.aiCommentRepository = aiCommentRepository;
        this.readingRecordService = readingRecordService;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    @Transactional
    public GenerationResult generateAndSave(Long readingRecordId) {
        if (apiKey == null || apiKey.isBlank()) return GenerationResult.keyMissing();
        try {
            ReadingRecord record = readingRecordService.findById(readingRecordId);
            String requestJson = objectMapper.writeValueAsString(Map.of("contents", new Object[]{
                    Map.of("parts", new Object[]{Map.of("text", buildPrompt(record))})}));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent"))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) return GenerationResult.failed();
            JsonNode parts = objectMapper.readTree(response.body()).path("candidates").path(0).path("content").path("parts");
            StringBuilder generated = new StringBuilder();
            parts.forEach(part -> generated.append(part.path("text").asText()));
            if (generated.isEmpty()) return GenerationResult.failed();

            AiComment comment = aiCommentRepository.findByReadingRecordId(readingRecordId).orElseGet(AiComment::new);
            comment.setReadingRecord(record);
            comment.setComment(generated.toString().trim());
            comment.setProvider("Gemini");
            aiCommentRepository.save(comment);
            record.setAiComment(comment);
            return GenerationResult.success();
        } catch (Exception ex) {
            return GenerationResult.failed();
        }
    }

    private String buildPrompt(ReadingRecord record) {
        Book book = record.getBook();
        return """
                あなたは読書習慣を支援するアシスタントです。以下の読書記録に対して、日本語で150文字から300文字程度のコメントを生成してください。
                1. 読書行動を肯定する 2. 感想を深掘りする質問を1つ提示する 3. 次回に意識するとよいポイントを1つ提示する
                過度に褒めすぎず、具体的で親しみやすい文章にしてください。
                書籍タイトル：%s
                著者名：%s
                カテゴリ：%s
                読書時間：%d分
                読んだページ：%sページから%sページ
                感想：%s
                これまでの累計読書時間：%d分
                """.formatted(book.getTitle(), value(book.getAuthor()), value(book.getCategory()),
                record.getReadingMinutes(), value(record.getStartPage()), value(record.getEndPage()),
                value(record.getReflection()), readingRecordService.getTotalMinutesForBook(book.getId()));
    }

    private String value(Object value) { return value == null ? "未設定" : value.toString(); }

    public record GenerationResult(Status status, String message) {
        public enum Status { SUCCESS, KEY_MISSING, FAILED }
        static GenerationResult success() { return new GenerationResult(Status.SUCCESS, null); }
        static GenerationResult keyMissing() { return new GenerationResult(Status.KEY_MISSING, KEY_MISSING_MESSAGE); }
        static GenerationResult failed() { return new GenerationResult(Status.FAILED, FAILURE_MESSAGE); }
    }
}
