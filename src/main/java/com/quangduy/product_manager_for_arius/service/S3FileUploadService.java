package com.quangduy.product_manager_for_arius.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class S3FileUploadService {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folder) {
        File localFile = null;
        String fileName = folder + "/" + file.getOriginalFilename();
        localFile = this.convertMultiPartToFile(file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, localFile)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        this.amazonS3.putObject(putObjectRequest);
        if (localFile.exists()) {
            localFile.delete();
        }
        String fileUrl = this.amazonS3.getUrl(bucketName, fileName).toString();
        return fileUrl;
    }

    public File convertMultiPartToFile(MultipartFile file) {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }
        return convFile;
    }
}
