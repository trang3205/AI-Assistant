package com.example.aiassistant.controller;

import com.example.aiassistant.dto.ChatRequest;
import com.example.aiassistant.dto.ChatResponse;
import com.example.aiassistant.dto.Message;
import com.example.aiassistant.service.LmStudioService;
import com.example.aiassistant.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
// REST Endpoint tiếp nhận và xử lý các yêu cầu hội thoại từ phía client
public class ChatController {

    private final LmStudioService lmStudioService;
    private final RagService ragService;

    // Tiếp nhận câu hỏi, truy vấn ngữ cảnh (RAG) và chuyển tới LLM xử lý
    @PostMapping
    public Mono<ChatResponse> chat(@RequestBody ChatRequest req) {
        List<Message> messages = new ArrayList<>();

        // Tìm context từ RAG
        String context = ragService.findContext(req.getMessage());

        if (!context.isBlank()) {
            // Có tài liệu → ghép context vào prompt
            messages.add(new Message("system",
                    "Bạn là trợ lý AI phân tích tài liệu. Nhiệm vụ của bạn:\n" +
                            "1. Đọc kỹ CONTEXT được cung cấp\n" +
                            "2. Trả lời câu hỏi dựa trên thông tin trong CONTEXT\n" +
                            "3. Nếu CONTEXT có thông tin liên quan, hãy cố gắng tổng hợp và trả lời đầy đủ\n" +
                            "4. Chỉ nói 'Tôi không tìm thấy thông tin này trong tài liệu' khi CONTEXT HOÀN TOÀN không liên quan\n"
                            +
                            "5. Trả lời bằng tiếng Việt, rõ ràng, có cấu trúc"));

            String userPrompt = "CONTEXT:\n" + context + "\n\nCÂU HỎI:\n" + req.getMessage();
            messages.add(new Message("user", userPrompt));
        } else {
            // Không có tài liệu → trả lời bình thường
            messages.add(new Message("system",
                    "Bạn là trợ lý AI thân thiện, trả lời ngắn gọn bằng tiếng Việt."));

            if (req.getHistory() != null) {
                messages.addAll(req.getHistory());
            }
            messages.add(new Message("user", req.getMessage()));
        }

        return lmStudioService.chat(messages)
                .map(ChatResponse::new);
    }

    // Kiểm tra trạng thái hoạt động của backend service
    @GetMapping("/ping")
    public String ping() {
        return "AI Assistant is running!";
    }
}