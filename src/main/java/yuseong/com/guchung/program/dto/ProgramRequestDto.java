package yuseong.com.guchung.program.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import yuseong.com.guchung.program.model.type.ProgramType;
import yuseong.com.guchung.program.model.type.RegionRestriction;
import yuseong.com.guchung.program.model.type.TargetAudience;

import java.time.LocalDateTime;
import java.util.List;

public class ProgramRequestDto {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private String programName;
        private String thumbnailUrl;
        private String dongName;
        private String eduTime;
        private Integer quarter;
        private LocalDateTime eduStartDate;
        private LocalDateTime eduEndDate;
        private LocalDateTime recruitStartDate;
        private LocalDateTime recruitEndDate;
        private String eduPlace;
        private int capacity;
        private TargetAudience targetAudience;
        private int eduPrice;
        private String needs;
        private String description;
        private String info;
        private String etc;
        private String classPlanUrl;
        private String institution;
        private RegionRestriction regionRestriction;
        private ProgramType programType;
        private Long instructorId;

        private List<FormItemRequest> additionalFields;
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String programName;
        private String thumbnailUrl;
        private String dongName;
        private String eduTime;
        private Integer quarter;
        private LocalDateTime eduStartDate;
        private LocalDateTime eduEndDate;
        private LocalDateTime recruitStartDate;
        private LocalDateTime recruitEndDate;
        private String eduPlace;
        private int capacity;
        private TargetAudience targetAudience;
        private int eduPrice;
        private String needs;
        private String description;
        private String info;
        private String etc;
        private String classPlanUrl;
        private String institution;
        private RegionRestriction regionRestriction;
        private ProgramType programType;
        private Long instructorId;

        public LocalDateTime getEndDate() {
            return this.eduEndDate;
        }

        private List<FormItemRequest> additionalFields;
    }

    @Getter
    @NoArgsConstructor
    public static class FormItemRequest {
        private String label;      // 질문명 (예: "경력사항")
        private String type;       // "TEXT" 또는 "RADIO"
        private boolean required;  // 필수여부
        private List<String> options; // 객관식일 경우 옵션들
    }
}