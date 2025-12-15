package yuseong.com.guchung.client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
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

    public List<String> uploadFiles(List<MultipartFile> multipartFiles, String dirName) throws IOException {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            if (!file.isEmpty()) {
                fileUrls.add(uploadFile(file, dirName));
            }
        }
        return fileUrls;
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String key = extractKeyFromUrl(fileUrl);
            if (key != null) {
                amazonS3.deleteObject(bucket, key);
                log.info("S3 file deleted successfully: {}", key);
            }
        } catch (Exception e) {
            log.error("S3 file deletion failed for URL: {}", fileUrl, e);
            throw new RuntimeException("S3 파일 삭제 중 오류가 발생했습니다.", e);
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        String key = null;
        try {
            String decodedUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8.toString());

            String bucketPath = "/" + bucket + "/";
            int index = decodedUrl.indexOf(bucketPath);

            if (index == -1) {
                log.warn("Cannot find bucket path in URL: {}", fileUrl);
                return null;
            }

            key = decodedUrl.substring(index + bucketPath.length());

        } catch (Exception e) {
            log.error("Failed to extract key from URL: {}", fileUrl, e);
        }
        return key;
    }


    private void validateFile(MultipartFile file) {
        String contentType = file.getContentType();

        if (file.isEmpty() || contentType == null || (!contentType.startsWith("image") && !contentType.equals("application/pdf"))) {
            throw new IllegalArgumentException("이미지 파일 또는 PDF 파일만 업로드 가능합니다.");
        }
    }

}