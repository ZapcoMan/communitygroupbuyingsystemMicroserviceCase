package com.cgb.common.utils;

import com.cgb.common.EIException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件处理工具
 */
public class FileUtil {

    /** 允许的图片类型 */
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");
    /** 允许的文档类型 */
    private static final List<String> DOC_EXTENSIONS = Arrays.asList("doc", "docx", "pdf", "xls", "xlsx", "ppt", "pptx", "txt");
    /** 单文件最大 10MB */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 文件上传
     * @param file 上传的文件
     * @param uploadPath 保存目录（相对路径，如 static/upload）
     * @param projectPath 项目根路径
     * @return 保存后的文件名
     */
    public static String upload(MultipartFile file, String uploadPath, String projectPath) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new EIException("上传文件不能为空");
        }

        // 文件大小校验
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new EIException("单文件大小不能超过 10MB");
        }

        // 文件类型校验
        String originalFilename = file.getOriginalFilename();
        String ext = getFileExtension(originalFilename);
        if (ext.isEmpty()) {
            throw new EIException("无法识别文件类型");
        }

        // 创建目录
        Path targetDir = Paths.get(projectPath, uploadPath);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 生成唯一文件名
        String newFilename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path targetPath = targetDir.resolve(newFilename);

        // 写入磁盘
        file.transferTo(targetPath.toFile());

        return newFilename;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || "".equals(filename)) return "";
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) return "";
        return filename.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * 判断是否为图片类型
     */
    public static boolean isImage(String filename) {
        return IMAGE_EXTENSIONS.contains(getFileExtension(filename));
    }

    /**
     * 判断是否为允许的文档类型
     */
    public static boolean isDocument(String filename) {
        return DOC_EXTENSIONS.contains(getFileExtension(filename));
    }

    /**
     * 获取文件 MIME 类型
     */
    public static String getContentType(String filename) {
        String ext = getFileExtension(filename);
        return switch (ext) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };
    }

    /**
     * 读取文件内容为字符串
     */
    public static String readFileToString(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }

    /**
     * 将字符串写入文件
     */
    public static void writeStringToFile(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);
        Files.writeString(path, content);
    }
}