package yuseong.com.guchung.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 추가 정보 요청 DTO")
public class SignupRequestDto {
    @Schema(description = "주소", example = "대전광역시 유성구...")
    private String address;

    @Schema(description = "면제 대상", example = "basic")
    private String exemption;

    @Schema(description = "이메일 수신 동의", example = "yes")
    private String emailSubscribe;

    @Schema(description = "SMS 수신 동의", example = "yes")
    private String smsSubscribe;

    @Schema(description = "SNS 수신 동의", example = "no")
    private String snsSubscribe;
}