package com.example.aiassistant.dto;

import lombok.Data;
import java.util.List;

@Data
// DTO đại diện cho dữ liệu yêu cầu hội thoại gửi từ client
public class ChatRequest {
    private String message;
    private List<Message> history;
}