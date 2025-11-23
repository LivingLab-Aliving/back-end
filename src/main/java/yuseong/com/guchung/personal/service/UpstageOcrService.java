package yuseong.com.guchung.personal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import yuseong.com.guchung.auth.dto.GlobalResponseDto;
import yuseong.com.guchung.personal.dto.UpstageOcrResponseDto;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpstageOcrService {

    @Value("${upstage.api.key}")
    private String apiKey;

    @Value("${upstage.api.url}")
    private String apiUrl;

    public GlobalResponseDto<String> extractAddressFromDocument(MultipartFile file) {
        UpstageOcrResponseDto ocrResponse = callUpstageApi(file);

        if (ocrResponse == null || ocrResponse.getText() == null) {
            throw new RuntimeException("OCR 분석 결과가 비어있습니다.");
        }

        String fullText = ocrResponse.getText();
        log.info("Extracted Text: \n{}", fullText);

        if (!fullText.contains("주민등록") && !fullText.contains("등본") && !fullText.contains("세대주")) {
            return GlobalResponseDto.fail("업로드된 문서가 주민등록등본이 아니거나 식별할 수 없습니다.");
        }

        String address = parseAddress(fullText);

        if (address == null) {
            return GlobalResponseDto.fail("주소 정보를 찾을 수 없습니다. 선명한 이미지로 다시 시도해주세요.");
        }

        return GlobalResponseDto.success("주소 추출 성공", address);
    }

    private UpstageOcrResponseDto callUpstageApi(MultipartFile file) {
        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        try {
            builder.part("document", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename(); // 파일명 필수
                }
            });
            builder.part("model", "ocr"); // 모델 지정

        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류가 발생했습니다.", e);
        }

        return webClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(UpstageOcrResponseDto.class)
                .block();
    }

    private String parseAddress(String text) {
        String addressPattern = "(([가-힣]+(시|도))\\s+([가-힣]+(시|군|구))\\s+([가-힣0-9\\s,.-]+(로|길|동|읍|면|리|가))\\s*([0-9]+(-[0-9]+)?)*)";

        Pattern pattern = Pattern.compile(addressPattern);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(0).trim();
        }

        String keywordPattern = "주\\s*소\\s*[:.]?\\s*([가-힣0-9\\s]+(시|도|군|구)[가-힣0-9\\s,.-]+)";
        Pattern pattern2 = Pattern.compile(keywordPattern);
        Matcher matcher2 = pattern2.matcher(text);
        
        if (matcher2.find()) {
            return matcher2.group(1).trim();
        }
        
        return null;
    }
}