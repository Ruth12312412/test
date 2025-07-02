package com.ai.admin.controller;

import com.ai.admin.common.Result;
import com.ai.admin.service.SpeechService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/speech")
@Tag(name = "语音服务", description = "语音识别和合成接口")
public class SpeechController {

    @Autowired
    private SpeechService speechService;

    @Operation(summary = "语音转文字")
    @PostMapping("/transcribe")
    public Result<Map<String, Object>> speechToText(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", required = false) String language) {
        try {
            if (file.isEmpty()) {
                return Result.error("音频文件不能为空");
            }

            // 检查文件大小（限制25MB）
            if (file.getSize() > 25 * 1024 * 1024) {
                return Result.error("音频文件大小不能超过25MB");
            }

            String text = speechService.speechToText(file, language);

            Map<String, Object> result = new HashMap<>();
            result.put("text", text);
            result.put("language", language);
            result.put("filename", file.getOriginalFilename());
            result.put("fileSize", file.getSize());

            return Result.success(result);

        } catch (Exception e) {
            log.error("语音转文字失败", e);
            return Result.error("语音转文字失败：" + e.getMessage());
        }
    }

    @Operation(summary = "文字转语音")
    @PostMapping("/synthesize")
    public Result<Map<String, Object>> textToSpeech(
            @RequestParam("text") String text,
            @RequestParam(value = "voice", required = false) String voice) {
        try {
            if (text == null || text.trim().isEmpty()) {
                return Result.error("文本内容不能为空");
            }

            if (text.length() > 4000) {
                return Result.error("文本长度不能超过4000个字符");
            }

            String audioPath = speechService.textToSpeech(text, voice);

            Map<String, Object> result = new HashMap<>();
            result.put("audioUrl", audioPath);
            result.put("text", text);
            result.put("voice", voice != null ? voice : "alloy");
            result.put("textLength", text.length());

            return Result.success(result);

        } catch (Exception e) {
            log.error("文字转语音失败", e);
            return Result.error("文字转语音失败：" + e.getMessage());
        }
    }

    @Operation(summary = "音频翻译")
    @PostMapping("/translate")
    public Result<Map<String, Object>> translateAudio(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "target_language", required = false, defaultValue = "en") String targetLanguage) {
        try {
            if (file.isEmpty()) {
                return Result.error("音频文件不能为空");
            }

            // 检查文件大小（限制25MB）
            if (file.getSize() > 25 * 1024 * 1024) {
                return Result.error("音频文件大小不能超过25MB");
            }

            String translatedText = speechService.translateAudio(file, targetLanguage);

            Map<String, Object> result = new HashMap<>();
            result.put("translatedText", translatedText);
            result.put("targetLanguage", targetLanguage);
            result.put("filename", file.getOriginalFilename());
            result.put("fileSize", file.getSize());

            return Result.success(result);

        } catch (Exception e) {
            log.error("音频翻译失败", e);
            return Result.error("音频翻译失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取支持的语音类型")
    @GetMapping("/voices")
    public Result<Map<String, Object>> getSupportedVoices() {
        Map<String, Object> voices = new HashMap<>();
        
        // OpenAI TTS支持的语音类型
        Map<String, String> openaiVoices = new HashMap<>();
        openaiVoices.put("alloy", "Alloy - 中性语音");
        openaiVoices.put("echo", "Echo - 男性语音");
        openaiVoices.put("fable", "Fable - 英式男性语音");
        openaiVoices.put("onyx", "Onyx - 深沉男性语音");
        openaiVoices.put("nova", "Nova - 女性语音");
        openaiVoices.put("shimmer", "Shimmer - 柔和女性语音");

        voices.put("openai", openaiVoices);

        Map<String, Object> result = new HashMap<>();
        result.put("voices", voices);
        result.put("defaultVoice", "alloy");

        return Result.success(result);
    }

    @Operation(summary = "获取支持的语言")
    @GetMapping("/languages")
    public Result<Map<String, Object>> getSupportedLanguages() {
        Map<String, String> languages = new HashMap<>();
        
        // 常用语言代码
        languages.put("zh", "中文");
        languages.put("en", "English");
        languages.put("ja", "日本語");
        languages.put("ko", "한국어");
        languages.put("fr", "Français");
        languages.put("de", "Deutsch");
        languages.put("es", "Español");
        languages.put("it", "Italiano");
        languages.put("pt", "Português");
        languages.put("ru", "Русский");
        languages.put("ar", "العربية");
        languages.put("hi", "हिन्दी");

        Map<String, Object> result = new HashMap<>();
        result.put("languages", languages);
        result.put("defaultLanguage", "zh");

        return Result.success(result);
    }
}