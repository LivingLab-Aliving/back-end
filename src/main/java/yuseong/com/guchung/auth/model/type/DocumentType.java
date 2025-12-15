package yuseong.com.guchung.auth.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentType {
    
    ADDRESS_PROOF("주소 인증 문서"), // 주민등록 등본 등 주소 증명
    CERTIFICATE("자격 증명서류"), // 강사/일반 자격증명
    ETC("기타 서류");

    private final String description;
}