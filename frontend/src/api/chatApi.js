import axios from 'axios';

const API_URL = 'http://localhost:8080/api/chat'; // Endpoint REST API backend ←

// Gửi tin nhắn cùng lịch sử hội thoại tới backend để lấy câu trả lời từ AI
export const sendMessage = async (message, history = []) => {
    const res = await axios.post(API_URL, { message, history });
    return res.data.reply;
};