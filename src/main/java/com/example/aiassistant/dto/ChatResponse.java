package com.example.aiassistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// DTO đại diện cho dữ liệu phản hồi hội thoại gửi về client
public class ChatResponse {
    private String reply;
}