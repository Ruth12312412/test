const express = require('express');
const cors = require('cors');
const app = express();

app.use(cors());
app.use(express.json());

// Mock data
const users = [
  { id: 1, username: 'admin', password: 'admin123', email: 'admin@example.com', nickname: '管理员' }
];

const models = [
  { id: 1, name: 'GPT-3.5-turbo', provider: 'OpenAI', status: 1, apiUrl: 'https://api.openai.com/v1' },
  { id: 2, name: 'GPT-4', provider: 'OpenAI', status: 1, apiUrl: 'https://api.openai.com/v1' },
  { id: 3, name: 'DALL-E-3', provider: 'OpenAI', status: 1, apiUrl: 'https://api.openai.com/v1' }
];

const apiKeys = [
  { id: 1, name: 'Default Key', keyValue: 'sk-xxxxxxxxxxxxxxxx', status: 1, usage: 1000, limit: 10000 }
];

// Auth endpoints
app.post('/api/auth/login', (req, res) => {
  const { username, password } = req.body;
  const user = users.find(u => u.username === username && u.password === password);
  
  if (user) {
    res.json({
      code: 200,
      message: '登录成功',
      data: {
        token: 'mock-jwt-token-' + Date.now(),
        user: { id: user.id, username: user.username, nickname: user.nickname }
      }
    });
  } else {
    res.status(401).json({ code: 401, message: '用户名或密码错误' });
  }
});

// Models endpoints
app.get('/api/models', (req, res) => {
  res.json({ code: 200, data: models });
});

app.post('/api/models', (req, res) => {
  const newModel = { id: Date.now(), ...req.body };
  models.push(newModel);
  res.json({ code: 200, message: '创建成功', data: newModel });
});

// API Keys endpoints
app.get('/api/api-keys', (req, res) => {
  res.json({ code: 200, data: apiKeys });
});

app.post('/api/api-keys', (req, res) => {
  const newKey = { id: Date.now(), ...req.body };
  apiKeys.push(newKey);
  res.json({ code: 200, message: '创建成功', data: newKey });
});

// Chat endpoints
app.post('/api/chat/completions', (req, res) => {
  const { messages, model } = req.body;
  
  // Mock response
  res.json({
    id: 'chatcmpl-' + Date.now(),
    object: 'chat.completion',
    created: Math.floor(Date.now() / 1000),
    model: model || 'gpt-3.5-turbo',
    choices: [{
      index: 0,
      message: {
        role: 'assistant',
        content: '这是一个模拟的AI回复。您的消息已收到：' + messages[messages.length - 1].content
      },
      finish_reason: 'stop'
    }],
    usage: {
      prompt_tokens: 10,
      completion_tokens: 20,
      total_tokens: 30
    }
  });
});

// Image generation endpoints
app.post('/api/images/generations', (req, res) => {
  const { prompt } = req.body;
  
  res.json({
    created: Math.floor(Date.now() / 1000),
    data: [{
      url: 'https://via.placeholder.com/512x512?text=' + encodeURIComponent(prompt)
    }]
  });
});

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'OK', message: 'Mock API Server is running' });
});

const PORT = 8080;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Mock API Server running on port ${PORT}`);
});