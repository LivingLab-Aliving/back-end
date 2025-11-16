package yuseong.com.guchung.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuseong.com.guchung.admin.model.Admin;
import yuseong.com.guchung.admin.dto.AdminRequestDto;
import yuseong.com.guchung.admin.repository.AdminRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminRepository adminRepository;

    /**
     * 관리자 회원가입
     */
    @Transactional
    public Long signUp(AdminRequestDto.SignUp requestDto) {
        if (adminRepository.existsByLoginId(requestDto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        // 임시: 비밀번호 암호화 X
        Admin admin = adminRepository.save(requestDto.toEntity());
        return admin.getAdminId();
    }

    /**
     * 관리자 로그인 (임시)
     */
    public Long login(AdminRequestDto.Login requestDto) {
        Admin admin = adminRepository.findByLoginId(requestDto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        // 임시: 비밀번호 비교
        if (!admin.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 인증/인가(JWT) 대신 임시로 adminId만 반환
        return admin.getAdminId();
    }
}