package yuseong.com.guchung.program.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import yuseong.com.guchung.program.model.type.ProgramType;
import yuseong.com.guchung.program.model.type.RegionRestriction;
import yuseong.com.guchung.program.model.type.TargetAudience;

import java.time.LocalDateTime;

public class ProgramRequestDto {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private String programName;
        private ProgramType programType;
        private String eduTime;
        private Integer quarter;
        private LocalDateTime eduStartDate;
        private LocalDateTime eduEndDate;
        private LocalDateTime recruitStartDate;
        private LocalDateTime recruitEndDate;
        private String eduPlace;
        private int capacity;
        private int eduPrice;
        private String needs; // 학습자준비물
        private String institution; // 교육기관

        private RegionRestriction regionRestriction;
        private TargetAudience targetAudience;

        private Long instructorId; // 강사 ID
        private String classPlanUrl;

        private String description;
        private String info;
        private String etc;
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String programName;
        private ProgramType programType;
        private String eduTime;
        private Integer quarter;
        private LocalDateTime eduStartDate;
        private LocalDateTime eduEndDate;
        private LocalDateTime recruitStartDate;
        private LocalDateTime recruitEndDate;
        private String eduPlace;
        private int capacity;
        private int eduPrice;
        private String needs;
        private String institution;
        private RegionRestriction regionRestriction;
        private TargetAudience targetAudience;
        private Long instructorId;
        private String classPlanUrl;
        private String description;
        private String info;
        private String etc;
    }
}