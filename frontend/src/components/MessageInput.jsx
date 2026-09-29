import { useState } from 'react';

// Cung cấp ô nhập liệu văn bản và xử lý sự kiện gửi tin nhắn từ người dùng
export default function MessageInput({ onSend, disabled }) {
    const [text, setText] = useState('');

    // Kiểm tra và thực hiện gửi nội dung tin nhắn
    const handleSend = () => {
        if (!text.trim() || disabled) return;
        onSend(text);
        setText('');
    };

    // Xử lý phím Enter để gửi tin và Shift+Enter để xuống dòng
    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    return (
        <div className="message-input">
            <textarea
                value={text}
                onChange={(e) => setText(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="Nhập tin nhắn... (Enter để gửi, Shift+Enter xuống dòng)"
                disabled={disabled}
                rows={1}
            />
            <button onClick={handleSend} disabled={disabled || !text.trim()}>
                {disabled ? '⏳' : '📤 Gửi'}
            </button>
        </div>
    );
}