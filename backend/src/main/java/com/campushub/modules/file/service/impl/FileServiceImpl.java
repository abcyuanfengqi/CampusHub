package com.campushub.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.file.entity.FileInfo;
import com.campushub.modules.file.mapper.FileInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文件管理服务（M9）：本地存储 + 元数据落库
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl {

    private final FileInfoMapper fileInfoMapper;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    public FileInfo upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件为空");
        }
        long maxSize = 20L * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过20MB");
        }
        String originalName = file.getOriginalFilename() == null ? "unnamed" : file.getOriginalFilename();
        String suffix = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) {
            suffix = originalName.substring(dot);
        }
        String storedName = UUID.randomUUID().toString().replace("-", "") + suffix;
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File target = new File(dir, storedName);
            file.transferTo(target.getAbsoluteFile());

            FileInfo info = new FileInfo();
            info.setFileName(originalName);
            info.setFilePath(target.getAbsolutePath());
            info.setFileSize(file.getSize());
            info.setFileType(file.getContentType());
            info.setUploaderId(UserContext.getUserId());
            info.setCreateTime(LocalDateTime.now());
            fileInfoMapper.insert(info);
            return info;
        } catch (Exception e) {
            throw new BusinessException("文件保存失败: " + e.getMessage());
        }
    }

    public Page<FileInfo> page(int page, int size) {
        return fileInfoMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<FileInfo>().orderByDesc(FileInfo::getCreateTime));
    }

    public void delete(Long id) {
        FileInfo info = fileInfoMapper.selectById(id);
        if (info == null) {
            throw new BusinessException("文件不存在");
        }
        try {
            File f = new File(info.getFilePath());
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception ignored) {
        }
        fileInfoMapper.deleteById(id);
    }
}
