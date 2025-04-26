package com.dotwavesoftware.importscheduler.features.Api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class S3Service {
    private final S3Client s3Client;
    private static final Logger logger = Logger.getLogger(S3Service.class.getName());

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    private static final String LOGOS_DIRECTORY = "Logos";
    private static final String LOGO_BUCKET = "import-manager-bucket";

    public String uploadImage(MultipartFile file, String userId) {
        try {
            // Generate a unique file name
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            
            // Create the full path in the Users directory
            String key = String.format("Users/%s/%s", userId, fileName);

            // Create the upload request
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            // Upload the file
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Return the URL of the uploaded file
            return String.format(key);
            //return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);

        } catch (IOException e) {
            logger.severe("Failed to upload file: " + e.getMessage());
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    /**
     * Upload a logo image to the Logos directory in import-manager-bucket
     * @param file The image file to upload
     * @return The S3 key/path of the uploaded logo
     */
    public String uploadLogo(MultipartFile file) {
        try {
            // Generate a unique file name
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            
            // Create the full path in the Logos directory
            String key = String.format("%s/%s", LOGOS_DIRECTORY, fileName);

            // Create the upload request
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(LOGO_BUCKET)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            // Upload the file
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            logger.info("Logo uploaded successfully to: " + key);
            
            // Return the full S3 URL
            return String.format("%s", key);

        } catch (IOException e) {
            logger.severe("Failed to upload logo: " + e.getMessage());
            throw new RuntimeException("Failed to upload logo", e);
        }
    }

    /**
     * Get a logo image from the Logos directory in import-manager-bucket
     * @param fileName The file name of the logo
     * @return The logo image as a byte array
     */
    public byte[] getLogo(String fileName) {
        String key = String.format("%s/%s", LOGOS_DIRECTORY, fileName);
        try {
            var getObjectRequest = software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                    .bucket(LOGO_BUCKET)
                    .key(key)
                    .build();

            try (var s3Object = s3Client.getObject(getObjectRequest)) {
                return s3Object.readAllBytes();
            }
        } catch (IOException e) {
            logger.severe("Failed to read logo from S3: " + e.getMessage());
            throw new RuntimeException("Failed to read logo from S3", e);
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            logger.severe("S3 error: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3 error: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    
    public byte[] getImage(String userId, String fileName) {
        String key = String.format("Users/%s/%s", userId, fileName);
        try {
            var getObjectRequest = software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            try (var s3Object = s3Client.getObject(getObjectRequest)) {
                return s3Object.readAllBytes();
            }
        } catch (IOException e) {
            logger.severe("Failed to read image from S3: " + e.getMessage());
            throw new RuntimeException("Failed to read image from S3", e);
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            logger.severe("S3 error: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3 error: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }
} 