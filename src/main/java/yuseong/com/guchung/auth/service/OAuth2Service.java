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
        log.info("[+] getKakaoUserInfo() 수행 시작");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, Object>> userInfoReq = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> responseUserInfo = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                userInfoReq,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> body = responseUserInfo.getBody();
        log.info(">>>>>> [카카오 응답 전체 데이터] : {}", body);

        if (body != null) {
            Map<String, Object> kakaoAccount = cvtObjectToMap(body.get("kakao_account"));
            log.info(">>>>>> [kakao_account 상세] : {}", kakaoAccount);

            String bYear = (String) kakaoAccount.get("birthyear");
            String bDay = (String) kakaoAccount.get("birthday");

            String realName = (String) kakaoAccount.get("name");
            log.info(">>>>>> [추출된 실명(name)] : {}", realName);

            return OAuth2KakaoUserInfoDto.builder()
                    .id(String.valueOf(body.get("id")))
                    .email((String) kakaoAccount.get("email"))
                    .name(realName)
                    .gender((String) kakaoAccount.get("gender"))
                    .birthYear(bYear)
                    .birthday(bDay)
                    .phoneNumber((String) kakaoAccount.get("phone_number"))
                    .build();
        }
        return null;
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