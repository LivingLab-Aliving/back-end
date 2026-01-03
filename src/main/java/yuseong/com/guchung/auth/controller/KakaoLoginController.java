package yuseong.com.guchung.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Authentication", description = "카카오 로그인 및 인증 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final OAuth2Service oAuth2Service;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "카카오 로그인 콜백", description = "인가 코드를 받아 유저를 식별하고 JWT를 발급합니다.")
    @GetMapping("/oauth")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        String kakaoAccessToken = kakaoService.getAccessTokenFromKakao(code);
        OAuth2KakaoUserInfoDto userInfoDto = oAuth2Service.getKakaoUserInfo(kakaoAccessToken);

        User user = authService.saveOrUpdateUser(userInfoDto);

        boolean isNewUser = (user.getAddress() == null || user.getAddress().isEmpty());

        Authentication authentication = authService.createAuthenticationForUser(user);
        JwtToken token = jwtTokenProvider.generateToken(authentication);

        Map<String, Object> response = new HashMap<>();
        response.put("grantType", token.getGrantType());
        response.put("accessToken", token.getAccessToken());
        response.put("refreshToken", token.getRefreshToken());
        response.put("name", user.getName());
        response.put("userId", user.getUserId());
        response.put("isNewUser", isNewUser);

        response.put("email", user.getEmail());
        response.put("phone", user.getPhoneNumber());
        response.put("gender", user.getGender());

        String birthStr = "";
        if (user.getBirth() != null) {
            birthStr = user.getBirth().format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        }
        response.put("birth", birthStr);
        log.info(">>>>>> [추출된 실명(name)] : {}", birthStr);

        return ResponseEntity.ok(response);
    }
}