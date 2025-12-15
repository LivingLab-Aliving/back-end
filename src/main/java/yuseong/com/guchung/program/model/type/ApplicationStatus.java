package yuseong.com.guchung.program.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationStatus {
    PENDING("승인 대기"),    // 최초 신청 상태
    APPROVED("승인 완료"),   // 관리자가 최종 승인
    REJECTED("신청 거부"),   // 관리자가 거부
    CANCELED("신청 취소");   // 사용자 본인이 취소

    private final String description;
}