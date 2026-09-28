import axios from 'axios';

const API_URL = 'http://localhost:8080/api/chat';

export const sendMessage = async (message, history = []) => {
    const res = await axios.post(API_URL, { message, history });
    return res.data.reply;
};