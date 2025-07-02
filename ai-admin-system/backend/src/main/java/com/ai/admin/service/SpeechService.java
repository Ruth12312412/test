package com.ai.admin.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 语音服务接口
 */
public interface SpeechService {

    /**
     * 语音转文字
     * @param audioFile 音频文件
     * @param language 语言代码
     * @return 识别结果
     */
    String speechToText(MultipartFile audioFile, String language);

    /**
     * 文字转语音
     * @param text 文本内容
     * @param voice 语音类型
     * @return 音频文件路径
     */
    String textToSpeech(String text, String voice);

    /**
     * 音频翻译
     * @param audioFile 音频文件
     * @param targetLanguage 目标语言
     * @return 翻译结果
     */
    String translateAudio(MultipartFile audioFile, String targetLanguage);
}