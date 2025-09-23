package com.papercup.controller;

import com.papercup.common.Result;
import com.papercup.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件控制器
 */
@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    /**
     * 上传文件 (需要管理员权限)
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileService.uploadFile(file);
            return Result.success("文件上传成功", fileUrl);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 访问上传的文件
     */
    @GetMapping("/{year}/{month}/{day}/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String year,
                                           @PathVariable String month,
                                           @PathVariable String day,
                                           @PathVariable String filename) {
        try {
            // 构建文件路径
            String filePath = uploadPath + year + "/" + month + "/" + day + "/" + filename;
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(path);
            
            // 获取文件的MIME类型
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("读取文件失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
