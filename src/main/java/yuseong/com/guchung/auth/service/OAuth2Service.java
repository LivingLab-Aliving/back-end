package yuseong.com.guchung.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import yuseong.com.guchung.auth.dto.OAuth2KakaoUserInfoDto;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final RestTemplate restTemplate = new RestTemplate();

    public OAuth2KakaoUserInfoDto getKakaoUserInfo(String accessToken) {
        log.debug("[+] getKakaoUserInfo() 수행 :: {}", accessToken);

        OAuth2KakaoUserInfoDto resultDto = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, Object> userInfoParam = new LinkedMultiValueMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            userInfoParam.add("property_keys",
                    objectMapper.writeValueAsString(new String[]{
                            "kakao_account.email",
                            "kakao_account.profile",
                            "kakao_account.gender",
                            "kakao_account.birthday",
                            "kakao_account.birthyear",
                            "kakao_account.phone_number"
                    })
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpEntity<MultiValueMap<String, Object>> userInfoReq = new HttpEntity<>(userInfoParam, headers);

        ResponseEntity<Map<String, Object>> responseUserInfo = null;
        try {
            responseUserInfo = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    userInfoReq,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            log.debug("카카오 응답 :: {}", responseUserInfo);

        } catch (Exception e) {
            log.error("[-] 사용자 정보 요청 중 오류 발생 :: {}", e.getMessage());
        }

        if (responseUserInfo != null && responseUserInfo.getBody() != null && responseUserInfo.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = responseUserInfo.getBody();

            Map<String, Object> kakaoAccount = cvtObjectToMap(body.get("kakao_account"));
            Map<String, Object> profile = cvtObjectToMap(kakaoAccount.get("profile"));

            resultDto = OAuth2KakaoUserInfoDto.builder()
                    .id(String.valueOf(body.get("id")))
                    .statusCode(responseUserInfo.getStatusCode().value())
                    .email((String) kakaoAccount.get("email"))
                    .name((String) profile.get("nickname"))
                    .gender((String) kakaoAccount.get("gender"))
                    .birthday((String) kakaoAccount.get("birthday"))
                    .birthYear((String) kakaoAccount.get("birthyear"))
                    .phoneNumber((String) kakaoAccount.get("phone_number"))
                    .build();

            log.debug("최종 구성 결과 :: {}", resultDto);
        }

        return resultDto;
    }

    private Map<String, Object> cvtObjectToMap(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            Map<String, Object> result = new HashMap<>();
            map.forEach((k, v) -> result.put(String.valueOf(k), v));
            return result;
        }
        return new HashMap<>();
    }
}