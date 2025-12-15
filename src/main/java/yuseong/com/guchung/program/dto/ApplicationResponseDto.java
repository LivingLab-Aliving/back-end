package yuseong.com.guchung.program.dto;

import lombok.Builder;
import lombok.Getter;
import yuseong.com.guchung.program.model.Application;
import yuseong.com.guchung.program.model.type.ApplicationStatus;

import java.time.LocalDateTime;

public class ApplicationResponseDto {

    @Getter
    @Builder
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
                    .appliedAt(application.getAppliedAt())
                    .status(application.getStatus())
                    .build();
        }
    }
}