import { useState } from 'react';
import { sendMessage } from '../api/chatApi';
import MessageList from './MessageList';
import MessageInput from './MessageInput';

export default function ChatBox() {
    const [messages, setMessages] = useState([
        {
            role: 'assistant',
            content: 'Xin chào! Tôi là AI Assistant chạy local. Bạn cần hỏi gì không? 😊'
        }
    ]);
    const [loading, setLoading] = useState(false);

    const handleSend = async (text) => {
        const userMsg = { role: 'user', content: text };
        setMessages((prev) => [...prev, userMsg]);
        setLoading(true);

        try {
            // Gửi history (không gồm tin vừa thêm, vì Spring Boot tự thêm)
            const history = messages.map((m) => ({
                role: m.role,
                content: m.content
            }));

            const reply = await sendMessage(text, history);
            setMessages((prev) => [
                ...prev,
                { role: 'assistant', content: reply }
            ]);
        } catch (err) {
            console.error(err);
            const errorMsg =
                err.response?.data?.message ||
                err.message ||
                'Không kết nối được server';

            setMessages((prev) => [
                ...prev,
                {
                    role: 'assistant',
                    content: `❌ Lỗi: ${errorMsg}. Kiểm tra Spring Boot (port 8080) và LM Studio (port 1234) đã chạy chưa.`
                }
            ]);
        } finally {
            setLoading(false);
        }
    };

    const handleClear = () => {
        if (confirm('Xóa toàn bộ cuộc trò chuyện?')) {
            setMessages([
                {
                    role: 'assistant',
                    content: 'Cuộc trò chuyện mới. Bạn cần hỏi gì?'
                }
            ]);
        }
    };

    return (
        <div className="chat-box">
            <div className="chat-header">
                <div className="chat-title">
                    <span className="status-dot"></span>
                    AI Assistant Local
                </div>
                <button className="btn-clear" onClick={handleClear}>
                    🗑️ Xóa
                </button>
            </div>

            <MessageList messages={messages} loading={loading} />
            <MessageInput onSend={handleSend} disabled={loading} />
        </div>
    );
}