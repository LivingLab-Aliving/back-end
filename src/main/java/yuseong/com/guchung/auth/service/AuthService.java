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
import yuseong.com.guchung.auth.dto.SignupRequestDto;
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

        LocalDate parsedBirth = null;

        if (userInfoDto.getBirthYear() != null && userInfoDto.getBirthday() != null) {
            try {
                String fullBirthStr = userInfoDto.getBirthYear() + userInfoDto.getBirthday();
                parsedBirth = LocalDate.parse(fullBirthStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
            } catch (Exception e) {
                log.warn("생년월일 파싱 실패: {}{}", userInfoDto.getBirthYear(), userInfoDto.getBirthday());
            }
        }

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.updateInfo(userInfoDto.getName(), userInfoDto.getPhoneNumber(), user.getAddress());

            if (user.getBirth() == null && parsedBirth != null) {
                user.setBirth(parsedBirth);
            }

            return user;
        } else {
            User user = User.builder()
                    .oauthId(oauthId)
                    .email(userInfoDto.getEmail())
                    .name(userInfoDto.getName())
                    .phoneNumber(userInfoDto.getPhoneNumber())
                    .birth(parsedBirth)
                    .gender(userInfoDto.getGender())
                    .build();
            return userRepository.save(user);
        }
    }

    @Transactional
    public void completeSignup(Long userId, SignupRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        user.setAddress(dto.getAddress());
        user.setExemption(dto.getExemption());
        user.setEmailSend("yes".equals(dto.getEmailSubscribe()));
        user.setAlarmSend("yes".equals(dto.getSmsSubscribe()));
        user.setSnsSend("yes".equals(dto.getSnsSubscribe()));

        log.info("[AuthService] 추가 정보 업데이트 성공. 유저ID: {}", userId);
    }

    public Authentication createAuthenticationForUser(User user) {
        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails principal = new org.springframework.security.core.userdetails.User(
                user.getOauthId(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}