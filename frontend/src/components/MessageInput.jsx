import { useState } from 'react';

export default function MessageInput({ onSend, disabled }) {
    const [text, setText] = useState('');

    const handleSend = () => {
        if (!text.trim() || disabled) return;
        onSend(text);
        setText('');
    };

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