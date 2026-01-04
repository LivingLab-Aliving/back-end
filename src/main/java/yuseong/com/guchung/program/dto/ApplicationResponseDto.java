package yuseong.com.guchung.program.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuseong.com.guchung.program.model.Application;
import yuseong.com.guchung.program.model.ApplicationAnswer;
import yuseong.com.guchung.program.model.type.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListResponse {
        private Long applicationId;
        private Long programId;
        private String programName;
        private LocalDateTime appliedAt;
        private ApplicationStatus status;

        public static ListResponse from(Application application) {
            return ListResponse.builder()
                    .applicationId(application.getApplicationId())
                    .programId(application.getProgram().getProgramId())
                    .programName(application.getProgram().getProgramName())
                    .appliedAt(application.getAppliedAt()) // 🌟 getCreatedAt -> getAppliedAt으로 수정
                    .status(application.getStatus())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdminListResponse {
        private Long applicationId;
        private String userName;
        private String loginId;  // oauthId 사용
        private String phone;
        private String email;
        private String address;
        private String birthDate;
        private String status;   // PENDING, APPROVED 등
        private String createdAt;
        private List<AnswerDetail> answers; // 추가 질문 답변 목록

        public static AdminListResponse from(Application app, List<ApplicationAnswer> answers) {
            return AdminListResponse.builder()
                    .applicationId(app.getApplicationId())
                    .userName(app.getUser().getName())
                    .loginId(app.getUser().getOauthId())   // 🌟 User 엔티티 필드에 맞춤
                    .phone(app.getUser().getPhoneNumber()) // 🌟 getPhone -> getPhoneNumber
                    .email(app.getUser().getEmail())
                    .address(app.getUser().getAddress())
                    .birthDate(app.getUser().getBirth() != null ? app.getUser().getBirth().toString() : null) // 🌟 getBirthDate -> getBirth
                    .status(app.getStatus().name())
                    .createdAt(app.getAppliedAt().toString()) // 🌟 getCreatedAt -> getAppliedAt
                    .answers(answers.stream()
                            .map(ans -> new AnswerDetail(ans.getFormItem().getLabel(), ans.getAnswer()))
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerDetail {
        private String label;  // 질문명
        private String answer; // 답변 내용
    }
}