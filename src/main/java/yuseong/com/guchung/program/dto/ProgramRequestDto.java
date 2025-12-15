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
        private String thumbnailUrl;
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
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        private String programName;
        private String thumbnailUrl;
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
    }
}