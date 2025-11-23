package yuseong.com.guchung.personal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import yuseong.com.guchung.auth.dto.GlobalResponseDto;
import yuseong.com.guchung.personal.service.UpstageOcrService;

@Tag(name = "OCR Verification", description = "주민등록등본 진위 확인 및 주소 추출 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ocr")
public class OcrController {

    private final UpstageOcrService upstageOcrService;

    @Operation(summary = "등본 인증 및 주소 추출", description = "이미지(png, jpg, pdf)를 업로드하여 주민등록등본인지 확인하고 주소를 추출합니다.")
    @PostMapping(value = "/verify-residence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalResponseDto<String> verifyResidence(
            @Parameter(description = "주민등록등본 이미지 파일", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart("file") MultipartFile file
    ) {
        return upstageOcrService.extractAddressFromDocument(file);
    }
}