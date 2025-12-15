package yuseong.com.guchung.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yuseong.com.guchung.auth.model.User;
import yuseong.com.guchung.auth.model.UserFile;
import yuseong.com.guchung.auth.model.type.DocumentType;
import yuseong.com.guchung.auth.repository.UserFileRepository;
import yuseong.com.guchung.auth.repository.UserRepository;
import yuseong.com.guchung.client.S3Uploader;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFileService {

    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;
    private final UserFileRepository userFileRepository;

    @Transactional
    public List<String> uploadUserDocuments(Long userId, List<MultipartFile> files, DocumentType documentType) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<UserFile> userFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        String fileUrl = s3Uploader.uploadFile(file, "user/documents");
                        
                        return UserFile.builder()
                                .originalName(file.getOriginalFilename())
                                .fileUrl(fileUrl)
                                .documentType(documentType)
                                .user(user)
                                .build();
                    } catch (IOException e) {
                        log.error("S3 파일 업로드 실패: {}", file.getOriginalFilename(), e);
                        throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
                    }
                }).collect(Collectors.toList());

        userFileRepository.saveAll(userFiles);
        
        return userFiles.stream().map(UserFile::getFileUrl).collect(Collectors.toList());
    }
}