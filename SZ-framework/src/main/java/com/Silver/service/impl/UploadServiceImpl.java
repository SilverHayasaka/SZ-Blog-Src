package com.Silver.service.impl;

import com.Silver.domain.ResponseResult;
import com.Silver.enums.AppHttpCodeEnum;
import com.Silver.exception.SystemException;
import com.Silver.service.UploadService;
import com.Silver.utils.PathUtils;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Data
public class UploadServiceImpl implements UploadService {

    public static final String SAVE_PATH = "/usr/src/SZBlog/static/storage" + File.separator;

    @Override
    public ResponseResult uploadImg(MultipartFile img) {
        String originalFilename = img.getOriginalFilename();
        if (!originalFilename.endsWith(".png") && !originalFilename.endsWith(".jpg")) {
            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }
        //如果判断通过上传文件到本地
        String filePath = PathUtils.generateFilePath(originalFilename);
        String url = uploadToServer(img, filePath);
        return ResponseResult.okResult(url);
    }

    private String uploadToServer(MultipartFile imgFile, String filePath) {
        try {
            InputStream inputStream = imgFile.getInputStream();
            File file = new File(SAVE_PATH + filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byte[] bytes = new byte[1024];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                return "http://silver-hayasaka.top/storage/" + filePath;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
