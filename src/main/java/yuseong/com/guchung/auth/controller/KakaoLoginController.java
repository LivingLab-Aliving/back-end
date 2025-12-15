package yuseong.com.guchung.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yuseong.com.guchung.auth.dto.OAuth2KakaoUserInfoDto;
import yuseong.com.guchung.auth.model.User;
import yuseong.com.guchung.auth.service.KakaoService;
import yuseong.com.guchung.auth.service.OAuth2Service;
import yuseong.com.guchung.auth.service.AuthService;
import yuseong.com.guchung.jwt.JwtToken;
import yuseong.com.guchung.jwt.JwtTokenProvider;

@Tag(name = "Authentication", description = "카카오 로그인 및 인증 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final OAuth2Service oAuth2Service;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "카카오 로그인 콜백", description = "카카오 인증 서버로부터 Authorization Code를 받아 액세스 토큰을 발급받고 유저 정보를 조회 후 DB에 저장하고 JWT를 발급합니다.")
    @GetMapping("/oauth")
    public ResponseEntity<JwtToken> callback(
            @Parameter(description = "카카오 인증 서버에서 받은 인가 코드") @RequestParam("code") String code
    ) {
        String kakaoAccessToken = kakaoService.getAccessTokenFromKakao(code);
        OAuth2KakaoUserInfoDto userInfoDto = oAuth2Service.getKakaoUserInfo(kakaoAccessToken);

        User user = authService.saveOrUpdateUser(userInfoDto);

        Authentication authentication = authService.createAuthenticationForUser(user);

        JwtToken token = jwtTokenProvider.generateToken(authentication);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}