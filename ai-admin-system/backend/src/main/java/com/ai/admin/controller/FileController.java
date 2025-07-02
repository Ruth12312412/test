package com.ai.admin.controller;

import com.ai.admin.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
@RestController
@RequestMapping("/file")
@Tag(name = "文件管理", description = "文件上传下载接口")
public class FileController {

    @Value("${file.upload-path:/uploads}")
    private String uploadPath;

    @Value("${file.max-size:10485760}")
    private long maxFileSize;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 检查文件大小
            if (file.getSize() > maxFileSize) {
                return Result.error("文件大小超过限制：" + (maxFileSize / 1024 / 1024) + "MB");
            }

            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名不能为空");
            }

            String fileExtension = getFileExtension(originalFilename);
            if (!isAllowedFileType(fileExtension)) {
                return Result.error("不支持的文件类型：" + fileExtension);
            }

            // 创建上传目录
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fullUploadPath = uploadPath + "/" + datePath;
            File uploadDir = new File(fullUploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;
            String filePath = fullUploadPath + "/" + fileName;

            // 保存文件
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());

            // 返回文件信息
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("originalName", originalFilename);
            result.put("filePath", "/" + datePath + "/" + fileName);
            result.put("fileSize", file.getSize());
            result.put("fileType", fileExtension);
            result.put("uploadTime", LocalDateTime.now());

            log.info("文件上传成功：{}", filePath);
            return Result.success(result);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "上传头像")
    @PostMapping("/upload/avatar")
    public Result<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("头像文件不能为空");
            }

            // 检查文件大小（头像限制2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("头像文件大小不能超过2MB");
            }

            // 检查是否为图片
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名不能为空");
            }

            String fileExtension = getFileExtension(originalFilename).toLowerCase();
            if (!isImageFile(fileExtension)) {
                return Result.error("只支持图片格式：jpg, jpeg, png, gif");
            }

            // 创建头像目录
            String avatarPath = uploadPath + "/avatars";
            File uploadDir = new File(avatarPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String fileName = "avatar_" + UUID.randomUUID().toString() + "." + fileExtension;
            String filePath = avatarPath + "/" + fileName;

            // 保存文件
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());

            // 返回头像信息
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("avatarUrl", "/avatars/" + fileName);
            result.put("fileSize", file.getSize());
            result.put("uploadTime", LocalDateTime.now());

            log.info("头像上传成功：{}", filePath);
            return Result.success(result);

        } catch (IOException e) {
            log.error("头像上传失败", e);
            return Result.error("头像上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量上传文件")
    @PostMapping("/upload/batch")
    public Result<Map<String, Object>> uploadBatchFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return Result.error("请选择要上传的文件");
            }

            if (files.length > 10) {
                return Result.error("批量上传文件数量不能超过10个");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("successCount", 0);
            result.put("failCount", 0);
            result.put("files", new HashMap<String, Object>());

            int successCount = 0;
            int failCount = 0;
            Map<String, Object> fileResults = new HashMap<>();

            for (MultipartFile file : files) {
                try {
                    Result<Map<String, Object>> uploadResult = uploadFile(file);
                    if (uploadResult.getCode() == 200) {
                        successCount++;
                        fileResults.put(file.getOriginalFilename(), uploadResult.getData());
                    } else {
                        failCount++;
                        fileResults.put(file.getOriginalFilename(), Map.of("error", uploadResult.getMessage()));
                    }
                } catch (Exception e) {
                    failCount++;
                    fileResults.put(file.getOriginalFilename(), Map.of("error", e.getMessage()));
                }
            }

            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("files", fileResults);

            return Result.success(result);

        } catch (Exception e) {
            log.error("批量文件上传失败", e);
            return Result.error("批量文件上传失败：" + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    private boolean isAllowedFileType(String extension) {
        String[] allowedTypes = {
            "jpg", "jpeg", "png", "gif", "bmp", "webp", // 图片
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", // 文档
            "txt", "md", "json", "xml", "csv", // 文本
            "mp3", "wav", "m4a", "flac", // 音频
            "mp4", "avi", "mov", "wmv", "flv", // 视频
            "zip", "rar", "7z", "tar", "gz" // 压缩包
        };

        String lowerExtension = extension.toLowerCase();
        for (String type : allowedTypes) {
            if (type.equals(lowerExtension)) {
                return true;
            }
        }
        return false;
    }

    private boolean isImageFile(String extension) {
        String[] imageTypes = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        for (String type : imageTypes) {
            if (type.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}