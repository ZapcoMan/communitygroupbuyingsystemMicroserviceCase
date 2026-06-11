package com.cgb.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件工具类 FileUtil 的单元测试
 */
@DisplayName("FileUtil - 文件处理工具")
class FileUtilTest {

    @Nested
    @DisplayName("getFileExtension() 获取文件扩展名")
    class GetFileExtensionTests {

        @Test
        @DisplayName("正常文件名 - 返回扩展名")
        void normalFilename() {
            assertEquals("jpg", FileUtil.getFileExtension("photo.jpg"));
            assertEquals("pdf", FileUtil.getFileExtension("document.pdf"));
            assertEquals("docx", FileUtil.getFileExtension("report.docx"));
        }

        @Test
        @DisplayName("大写扩展名 - 返回小写")
        void uppercaseExtension() {
            assertEquals("jpg", FileUtil.getFileExtension("photo.JPG"));
            assertEquals("png", FileUtil.getFileExtension("image.PNG"));
        }

        @Test
        @DisplayName("多个点 - 取最后一个扩展名")
        void multipleDots() {
            assertEquals("gz", FileUtil.getFileExtension("archive.tar.gz"));
        }

        @Test
        @DisplayName("null - 返回空字符串")
        void nullFilename() {
            assertEquals("", FileUtil.getFileExtension(null));
        }

        @Test
        @DisplayName("空字符串 - 返回空字符串")
        void emptyFilename() {
            assertEquals("", FileUtil.getFileExtension(""));
        }

        @Test
        @DisplayName("无扩展名 - 返回空字符串")
        void noExtension() {
            assertEquals("", FileUtil.getFileExtension("Makefile"));
        }

        @Test
        @DisplayName("以点结尾 - 返回空字符串")
        void endsWithDot() {
            assertEquals("", FileUtil.getFileExtension("file."));
        }
    }

    @Nested
    @DisplayName("isImage() 图片类型判断")
    class IsImageTests {

        @Test
        @DisplayName("常见图片格式 - 返回 true")
        void commonImageFormats() {
            assertTrue(FileUtil.isImage("photo.jpg"));
            assertTrue(FileUtil.isImage("photo.jpeg"));
            assertTrue(FileUtil.isImage("image.png"));
            assertTrue(FileUtil.isImage("animation.gif"));
            assertTrue(FileUtil.isImage("bitmap.bmp"));
            assertTrue(FileUtil.isImage("modern.webp"));
        }

        @Test
        @DisplayName("非图片格式 - 返回 false")
        void nonImageFormats() {
            assertFalse(FileUtil.isImage("doc.pdf"));
            assertFalse(FileUtil.isImage("video.mp4"));
            assertFalse(FileUtil.isImage("code.java"));
        }

        @Test
        @DisplayName("无扩展名 - 返回 false")
        void noExtension() {
            assertFalse(FileUtil.isImage("README"));
        }
    }

    @Nested
    @DisplayName("isDocument() 文档类型判断")
    class IsDocumentTests {

        @Test
        @DisplayName("常见文档格式 - 返回 true")
        void commonDocFormats() {
            assertTrue(FileUtil.isDocument("file.doc"));
            assertTrue(FileUtil.isDocument("file.docx"));
            assertTrue(FileUtil.isDocument("file.pdf"));
            assertTrue(FileUtil.isDocument("file.xls"));
            assertTrue(FileUtil.isDocument("file.xlsx"));
            assertTrue(FileUtil.isDocument("file.ppt"));
            assertTrue(FileUtil.isDocument("file.pptx"));
            assertTrue(FileUtil.isDocument("file.txt"));
        }

        @Test
        @DisplayName("非文档格式 - 返回 false")
        void nonDocFormats() {
            assertFalse(FileUtil.isDocument("photo.jpg"));
            assertFalse(FileUtil.isDocument("video.mp4"));
            assertFalse(FileUtil.isDocument("code.java"));
        }
    }

    @Nested
    @DisplayName("getContentType() MIME 类型")
    class GetContentTypeTests {

        @Test
        @DisplayName("JPEG 图片 - 返回 image/jpeg")
        void jpeg() {
            assertEquals("image/jpeg", FileUtil.getContentType("photo.jpg"));
            assertEquals("image/jpeg", FileUtil.getContentType("photo.jpeg"));
        }

        @Test
        @DisplayName("PNG 图片 - 返回 image/png")
        void png() {
            assertEquals("image/png", FileUtil.getContentType("image.png"));
        }

        @Test
        @DisplayName("GIF 图片 - 返回 image/gif")
        void gif() {
            assertEquals("image/gif", FileUtil.getContentType("animation.gif"));
        }

        @Test
        @DisplayName("PDF 文档 - 返回 application/pdf")
        void pdf() {
            assertEquals("application/pdf", FileUtil.getContentType("doc.pdf"));
        }

        @Test
        @DisplayName("未知类型 - 返回 application/octet-stream")
        void unknown() {
            assertEquals("application/octet-stream", FileUtil.getContentType("file.xyz"));
        }
    }

    @Nested
    @DisplayName("文件读写")
    class FileReadWriteTests {

        @Test
        @DisplayName("writeStringToFile + readFileToString - 写入并读取")
        void writeAndRead(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("test.txt");
            String content = "Hello, 社区团购!";
            FileUtil.writeStringToFile(file.toString(), content);
            String result = FileUtil.readFileToString(file.toString());
            assertEquals(content, result);
        }

        @Test
        @DisplayName("deleteFile() - 删除已存在的文件返回 true")
        void deleteFile_exists(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("to-delete.txt");
            Files.writeString(file, "temp");
            assertTrue(FileUtil.deleteFile(file.toString()));
            assertFalse(Files.exists(file));
        }

        @Test
        @DisplayName("deleteFile() - 删除不存在的文件返回 false")
        void deleteFile_notExists() {
            assertFalse(FileUtil.deleteFile("/nonexistent/path/file.txt"));
        }
    }
}
