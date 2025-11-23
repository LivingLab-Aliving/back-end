package yuseong.com.guchung.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yuseong.com.guchung.auth.dto.KakaoUserInfoResponseDto;
import yuseong.com.guchung.auth.service.KakaoService;

@Tag(name = "Authentication", description = "카카오 로그인 및 인증 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @Operation(summary = "카카오 로그인 콜백", description = "카카오 인증 서버로부터 Authorization Code를 받아 액세스 토큰을 발급받고 유저 정보를 조회합니다.")
    @GetMapping("/oauth")
    public ResponseEntity<?> callback(
            @Parameter(description = "카카오 인증 서버에서 받은 인가 코드") @RequestParam("code") String code
    ) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);

        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}