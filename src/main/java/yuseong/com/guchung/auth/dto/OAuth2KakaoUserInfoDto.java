package yuseong.com.guchung.auth.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2KakaoUserInfoDto {
    private String id;
    private int statusCode;
    private String email;
    private String name;
    private String gender;
    private String birthday;
    private String birthYear;
    private String phoneNumber;

    @Builder
    public OAuth2KakaoUserInfoDto(String id, int statusCode, String email, String name, String gender, String birthday, String birthYear, String phoneNumber) {
        this.id = id;
        this.statusCode = statusCode;
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.birthYear = birthYear;
        this.phoneNumber = phoneNumber;
    }
}