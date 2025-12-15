package yuseong.com.guchung.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yuseong.com.guchung.auth.dto.GlobalResponseDto;
import yuseong.com.guchung.auth.model.type.DocumentType;
import yuseong.com.guchung.auth.service.UserFileService;

import java.util.List;

@Tag(name = "User Document", description = "사용자 증빙 서류 업로드 및 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/{userId}/documents")
public class UserFileController {

    private final UserFileService userFileService;

    @Operation(summary = "사용자 증빙 파일 업로드", description = "주소 인증, 자격증 등 사용자의 증빙 파일을 업로드합니다. 다중 파일 업로드 가능.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalResponseDto<List<String>> uploadDocuments(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Long userId,

            @Parameter(description = "업로드할 파일 목록", required = true)
            @RequestPart(value = "files") List<MultipartFile> files,

            @Parameter(description = "문서 유형 (ADDRESS_PROOF, CERTIFICATE, ETC)", required = true)
            @RequestParam DocumentType type
    ) {
        List<String> uploadedUrls = userFileService.uploadUserDocuments(userId, files, type);
        
        return GlobalResponseDto.success("문서 업로드 성공", uploadedUrls);
    }
}