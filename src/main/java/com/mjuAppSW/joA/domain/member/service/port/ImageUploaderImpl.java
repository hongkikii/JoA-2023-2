package com.mjuAppSW.joA.domain.member.service.port;

import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;
import static com.mjuAppSW.joA.common.constant.Constants.S3Uploader.ERROR;

import com.mjuAppSW.joA.domain.member.exception.InvalidS3Exception;
import com.mjuAppSW.joA.domain.member.infrastructure.ImageUploader;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;


@Slf4j
@RequiredArgsConstructor
@Component
public class ImageUploaderImpl implements ImageUploader {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public String put(Long memberId, String base64Picture) {
        String key = String.valueOf(memberId);
        byte[] pictureBytes = Base64.getDecoder().decode(base64Picture);
        ByteBuffer byteBuffer = ByteBuffer.wrap(pictureBytes);
        int random = ThreadLocalRandom.current().nextInt(10000000, 100000000);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key + ":" + random)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromByteBuffer(byteBuffer));
            log.info("Picture uploaded successfully to S3");
            String address = key + ":" + random;
            return address;
        }
        catch (S3Exception e) {
            log.error("Error uploading picture to S3: " + e.getMessage());
            throw new InvalidS3Exception();
        }
    }

    public boolean delete(String key) {
        if(key.equals(EMPTY_STRING)) {
            return true;
        }
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Picture deleted successfully from S3");
            return true;
        }
        catch (Exception e) {
            log.error("Error deleting picture from S3: " + e.getMessage());
            throw new InvalidS3Exception();
        }
    }
}
