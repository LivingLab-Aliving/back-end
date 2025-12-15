package yuseong.com.guchung.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuseong.com.guchung.auth.dto.OAuth2KakaoUserInfoDto;
import yuseong.com.guchung.auth.model.User;
import yuseong.com.guchung.auth.repository.UserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public User saveOrUpdateUser(OAuth2KakaoUserInfoDto userInfoDto) {

        String oauthId = userInfoDto.getId();
        Optional<User> existingUser = userRepository.findByOauthId(oauthId);

        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.updateInfo(
                    userInfoDto.getName(),
                    userInfoDto.getPhoneNumber(),
                    user.getAddress()
            );
            log.info("[AuthService] 기존 사용자 정보 업데이트 완료. ID: {}", user.getUserId());

        } else {
            LocalDate birthDate = null;
            if (userInfoDto.getBirthday() != null && userInfoDto.getBirthYear() != null) {
                try {
                    String fullBirth = userInfoDto.getBirthYear() + userInfoDto.getBirthday();
                    birthDate = LocalDate.parse(fullBirth, DateTimeFormatter.ofPattern("yyyyMMdd"));
                } catch (Exception e) {
                    log.warn("생년월일 파싱 실패: {}", e.getMessage());
                }
            }

            user = User.builder()
                    .oauthId(oauthId)
                    .email(userInfoDto.getEmail())
                    .name(userInfoDto.getName())
                    .birth(birthDate)
                    .gender(userInfoDto.getGender())
                    .phoneNumber(userInfoDto.getPhoneNumber())
                    .address(null)
                    .build();

            userRepository.save(user);
            log.info("[AuthService] 신규 사용자 등록 완료. ID: {}", user.getUserId());
        }

        return user;
    }

    public Authentication createAuthenticationForUser(User user) {
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        UserDetails principal = new org.springframework.security.core.userdetails.User(
                user.getOauthId(),
                "",
                authorities
        );

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}