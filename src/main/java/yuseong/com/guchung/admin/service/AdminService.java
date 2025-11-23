package yuseong.com.guchung.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuseong.com.guchung.admin.dto.AdminRequestDto;
import yuseong.com.guchung.admin.model.Admin;
import yuseong.com.guchung.admin.repository.AdminRepository;
import yuseong.com.guchung.jwt.JwtToken;
import yuseong.com.guchung.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(AdminRequestDto.SignUp requestDto) {
        if (adminRepository.existsByLoginId(requestDto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Admin admin = Admin.builder()
                .loginId(requestDto.getLoginId())
                .password(encodedPassword)
                .name(requestDto.getName())
                .build();

        return adminRepository.save(admin).getAdminId();
    }

    public JwtToken login(AdminRequestDto.Login requestDto) {
        Admin admin = adminRepository.findByLoginId(requestDto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getLoginId(), admin.getPassword());

        return jwtTokenProvider.generateToken(authenticationToken);
    }
}