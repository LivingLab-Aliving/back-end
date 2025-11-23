package yuseong.com.guchung.program.dto;

import lombok.Getter;
import yuseong.com.guchung.program.model.Program;
import yuseong.com.guchung.program.model.type.RegionRestriction;
import yuseong.com.guchung.program.model.type.TargetAudience;

import java.time.LocalDateTime;

public class ProgramResponseDto {

    @Getter
    public static class CreateResponse {
        private Long programId;
        private String applicationFormUrl;
        private String classPlanUrl;

        public CreateResponse(Long programId, String applicationFormUrl, String classPlanUrl) {
            this.programId = programId;
            this.applicationFormUrl = applicationFormUrl;
            this.classPlanUrl = classPlanUrl;
        }
    }

    @Getter
    public static class ListResponse {
        private Long programId;
        private String programName;
        private String eduPlace;
        private LocalDateTime recruitStartDate;
        private LocalDateTime recruitEndDate;
        private int eduPrice;
        private int capacity;
        private String institution;
        private String classPlanUrl;

        public ListResponse(Program entity) {
            this.programId = entity.getProgramId();
            this.programName = entity.getProgramName();
            this.eduPlace = entity.getEduPlace();
            this.recruitStartDate = entity.getRecruitStartDate();
            this.recruitEndDate = entity.getRecruitEndDate();
            this.eduPrice = entity.getEduPrice();
            this.capacity = entity.getCapacity();
            this.institution = entity.getInstitution();
            this.classPlanUrl = entity.getClassPlanUrl();
        }
    }

    @Getter
    public static class DetailResponse {
        private Long programId;
        private String programName;
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
        private LocalDateTime createdAt;

        private Long instructorId;
        private String instructorName;

        public DetailResponse(Program entity) {
            this.programId = entity.getProgramId();
            this.programName = entity.getProgramName();
            this.eduTime = entity.getEduTime();
            this.quarter = entity.getQuarter();
            this.eduStartDate = entity.getEduStartDate();
            this.eduEndDate = entity.getEduEndDate();
            this.recruitStartDate = entity.getRecruitStartDate();
            this.recruitEndDate = entity.getRecruitEndDate();
            this.eduPlace = entity.getEduPlace();
            this.capacity = entity.getCapacity();
            this.targetAudience = entity.getTargetAudience();
            this.eduPrice = entity.getEduPrice();
            this.needs = entity.getNeeds();
            this.description = entity.getDescription();
            this.info = entity.getInfo();
            this.etc = entity.getEtc();
            this.classPlanUrl = entity.getClassPlanUrl();
            this.institution = entity.getInstitution();
            this.regionRestriction = entity.getRegionRestriction();
            this.createdAt = entity.getCreatedAt();

            if (entity.getInstructor() != null) {
                this.instructorId = entity.getInstructor().getInstructorId();
                this.instructorName = entity.getInstructor().getName();
            }
        }
    }
}