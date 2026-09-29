import { useEffect, useRef } from 'react';

// Hiển thị danh sách các tin nhắn hội thoại và tự động cuộn theo nội dung mới
export default function MessageList({ messages, loading }) {
    const bottomRef = useRef(null);

    // Tự động scroll xuống tin nhắn mới nhất
    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages, loading]);

    return (
        <div className="message-list">
            {messages.map((m, i) => (
                <div key={i} className={`msg msg-${m.role}`}>
                    <div className="msg-avatar">
                        {m.role === 'user' ? '👤' : '🤖'}
                    </div>
                    <div className="msg-content">
                        <div className="msg-role">
                            {m.role === 'user' ? 'Bạn' : 'AI Assistant'}
                        </div>
                        <div className="msg-text">{m.content}</div>
                    </div>
                </div>
            ))}

            {loading && (
                <div className="msg msg-assistant">
                    <div className="msg-avatar">🤖</div>
                    <div className="msg-content">
                        <div className="msg-role">AI Assistant</div>
                        <div className="msg-text typing">
                            <span></span><span></span><span></span>
                        </div>
                    </div>
                </div>
            )}

            <div ref={bottomRef} />
        </div>
    );
}