package yuseong.com.guchung.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import yuseong.com.guchung.admin.model.Admin;

public class AdminRequestDto {

    @Getter
    @NoArgsConstructor
    public static class SignUp {
        private String loginId;
        private String password;
        private String name;

        // 현재 암호화 없음
        public Admin toEntity() {
            return Admin.builder()
                    .loginId(loginId)
                    .password(password)
                    .name(name)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Login {
        private String loginId;
        private String password;
    }
}