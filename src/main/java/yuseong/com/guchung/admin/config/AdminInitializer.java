package yuseong.com.guchung.admin.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import yuseong.com.guchung.admin.model.Admin;
import yuseong.com.guchung.admin.repository.AdminRepository;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initAdmin() {
        // admin 계정이 없으면 생성
        if (!adminRepository.existsByLoginId("admin")) {
            String encodedPassword = passwordEncoder.encode("admin");
            
            Admin admin = Admin.builder()
                    .loginId("admin")
                    .password(encodedPassword)
                    .name("관리자")
                    .build();
            
            adminRepository.save(admin);
            System.out.println("초기 관리자 계정이 생성되었습니다. (loginId: admin, password: admin)");
        }
    }
}

