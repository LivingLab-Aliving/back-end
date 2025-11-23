package yuseong.com.guchung.client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile multipartFile, String dirName) throws IOException {
        validateFile(multipartFile);

        String fileName = dirName + "/" + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void validateFile(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || (!contentType.startsWith("image") && !contentType.equals("application/pdf"))) {
            throw new IllegalArgumentException("이미지 파일 또는 PDF 파일만 업로드 가능합니다.");
        }
    }

}