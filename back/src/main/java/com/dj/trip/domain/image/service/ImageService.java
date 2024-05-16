package com.dj.trip.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.dj.trip.domain.image.dto.UploadImageResponse;
import com.dj.trip.domain.image.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService implements ImageServiceUtils {
    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.base-url}")
    private String baseUrl;

    private final static Integer IMAGE_MAX_SIZE = 25;
    private final AmazonS3 amazonS3;

    public UploadImageResponse uploadImage(MultipartFile file) {

        return new UploadImageResponse(getImageUrl(upload(file)));
    }

    public String upload(MultipartFile file) {
        if (file.isEmpty() && file.getOriginalFilename() != null)
            throw FileEmptyException.EXCEPTION;

        if (file.getSize() <= IMAGE_MAX_SIZE) {
            throw FileSizeException.EXCEPTION;
        }

        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        if (!(ext.equals("jpg")
                || ext.equals("HEIC")
                || ext.equals("jpeg")
                || ext.equals("png")
                || ext.equals("heic"))) {
            throw BadFileExtensionException.EXCEPTION;
        }

        String randomName = UUID.randomUUID().toString();
        String fileName = "|" + randomName + "." + ext;

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            byte[] byteArray = IOUtils.toByteArray(file.getInputStream());
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(byteArray.length);
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), objectMetadata).
                    withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw FileUploadFailException.EXCEPTION;
        }

        log.info("url = {}", fileName);

        return fileName;
    }

    public void deleteImage(String fileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
        } catch (Exception e) {
            throw FileDeleteFailException.EXCEPTION;
        }
    }

    public String getImageUrl(String fileName) {
        return baseUrl + "/" + fileName;
    }
}
