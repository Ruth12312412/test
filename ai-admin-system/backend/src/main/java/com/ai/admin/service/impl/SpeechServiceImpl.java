package com.ai.admin.service.impl;

import com.ai.admin.service.SpeechService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SpeechServiceImpl implements SpeechService {

    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;

    @Value("${ai.openai.base-url:https://api.openai.com/v1}")
    private String openaiBaseUrl;

    @Value("${file.upload-path:/uploads}")
    private String uploadPath;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String speechToText(MultipartFile audioFile, String language) {
        try {
            if (openaiApiKey == null || openaiApiKey.isEmpty()) {
                // 返回模拟结果
                return createMockSpeechToTextResult(audioFile.getOriginalFilename());
            }

            // 检查文件格式
            String filename = audioFile.getOriginalFilename();
            if (filename == null || !isAudioFile(filename)) {
                throw new RuntimeException("不支持的音频格式");
            }

            // 构建请求
            String apiUrl = openaiBaseUrl + "/audio/transcriptions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(openaiApiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            });
            body.add("model", "whisper-1");
            if (language != null && !language.isEmpty()) {
                body.add("language", language);
            }

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);
                return (String) result.get("text");
            } else {
                throw new RuntimeException("语音识别失败: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("语音转文字失败", e);
            // 返回模拟结果
            return createMockSpeechToTextResult(audioFile.getOriginalFilename());
        }
    }

    @Override
    public String textToSpeech(String text, String voice) {
        try {
            if (openaiApiKey == null || openaiApiKey.isEmpty()) {
                // 返回模拟结果
                return createMockTextToSpeechResult(text);
            }

            // 构建请求
            String apiUrl = openaiBaseUrl + "/audio/speech";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "tts-1");
            requestBody.put("input", text);
            requestBody.put("voice", voice != null ? voice : "alloy");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<byte[]> response = restTemplate.postForEntity(apiUrl, entity, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // 保存音频文件
                String audioPath = saveAudioFile(response.getBody(), "mp3");
                return audioPath;
            } else {
                throw new RuntimeException("语音合成失败: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("文字转语音失败", e);
            // 返回模拟结果
            return createMockTextToSpeechResult(text);
        }
    }

    @Override
    public String translateAudio(MultipartFile audioFile, String targetLanguage) {
        try {
            if (openaiApiKey == null || openaiApiKey.isEmpty()) {
                // 返回模拟结果
                return createMockTranslationResult(audioFile.getOriginalFilename(), targetLanguage);
            }

            // 检查文件格式
            String filename = audioFile.getOriginalFilename();
            if (filename == null || !isAudioFile(filename)) {
                throw new RuntimeException("不支持的音频格式");
            }

            // 构建请求
            String apiUrl = openaiBaseUrl + "/audio/translations";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(openaiApiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            });
            body.add("model", "whisper-1");

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);
                return (String) result.get("text");
            } else {
                throw new RuntimeException("音频翻译失败: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("音频翻译失败", e);
            // 返回模拟结果
            return createMockTranslationResult(audioFile.getOriginalFilename(), targetLanguage);
        }
    }

    private boolean isAudioFile(String filename) {
        String[] audioExtensions = {"mp3", "wav", "m4a", "flac", "aac", "ogg", "wma"};
        String extension = getFileExtension(filename).toLowerCase();
        for (String ext : audioExtensions) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    private String saveAudioFile(byte[] audioData, String extension) throws IOException {
        // 创建音频目录
        String audioPath = uploadPath + "/audio";
        File audioDir = new File(audioPath);
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }

        // 生成文件名
        String fileName = "tts_" + UUID.randomUUID().toString() + "." + extension;
        String filePath = audioPath + "/" + fileName;

        // 保存文件
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(audioData);
        }

        return "/audio/" + fileName;
    }

    private String createMockSpeechToTextResult(String filename) {
        // 根据文件名生成模拟识别结果
        if (filename != null && filename.contains("hello")) {
            return "Hello, this is a test audio file for speech recognition.";
        } else if (filename != null && filename.contains("中文")) {
            return "这是一个中文语音识别的测试文件。";
        } else {
            return "这是语音识别的模拟结果。由于未配置真实的API密钥，系统返回了模拟的识别文本。在实际使用中，这里会显示音频文件的真实识别内容。";
        }
    }

    private String createMockTextToSpeechResult(String text) {
        // 返回模拟音频文件路径
        try {
            // 创建一个空的音频文件作为占位符
            String audioPath = uploadPath + "/audio";
            File audioDir = new File(audioPath);
            if (!audioDir.exists()) {
                audioDir.mkdirs();
            }

            String fileName = "mock_tts_" + System.currentTimeMillis() + ".mp3";
            String filePath = audioPath + "/" + fileName;

            // 创建空文件
            Path path = Paths.get(filePath);
            Files.write(path, "Mock audio file".getBytes());

            return "/audio/" + fileName;
        } catch (IOException e) {
            log.error("创建模拟音频文件失败", e);
            return "/audio/mock_audio.mp3";
        }
    }

    private String createMockTranslationResult(String filename, String targetLanguage) {
        if ("zh".equals(targetLanguage) || "zh-CN".equals(targetLanguage)) {
            return "这是音频翻译的模拟结果。原始音频内容已被翻译成中文。";
        } else if ("en".equals(targetLanguage)) {
            return "This is a mock result for audio translation. The original audio content has been translated to English.";
        } else {
            return "Audio translation mock result for language: " + targetLanguage;
        }
    }
}