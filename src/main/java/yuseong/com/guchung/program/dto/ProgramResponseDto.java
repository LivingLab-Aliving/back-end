package yuseong.com.guchung.program.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuseong.com.guchung.program.model.Application;
import yuseong.com.guchung.program.model.Program;
import yuseong.com.guchung.program.model.type.ApplicationStatus;
import yuseong.com.guchung.program.model.type.ProgramType;

import java.time.LocalDateTime;
import java.util.List;

public class ProgramResponseDto {

    @Getter
    public static class ListResponse {
        private Long programId;
        private String programName;
        private String thumbnailUrl;
        private ProgramType programType;
        private String eduPlace;
        private int capacity;
        private int eduPrice;
        private LocalDateTime recruitStartDate;
        private LocalDateTime recruitEndDate;
        private LocalDateTime eduStartDate;
        private LocalDateTime eduEndDate;
        private String eduTime;
        private String dongName;
        private int likeCount;
        private boolean isLiked;

        public ListResponse(Program program) {
            this.programId = program.getProgramId();
            this.programName = program.getProgramName();
            this.thumbnailUrl = program.getThumbnailUrl();
            this.programType = program.getProgramType();
            this.eduPlace = program.getEduPlace();
            this.capacity = program.getCapacity();
            this.eduPrice = program.getEduPrice();
            this.recruitStartDate = program.getRecruitStartDate();
            this.recruitEndDate = program.getRecruitEndDate();
            this.eduStartDate = program.getEduStartDate();
            this.eduEndDate = program.getEduEndDate();
            this.eduTime = program.getEduTime();
            this.dongName = program.getDongName();
        }

        public void setLikeInfo(int likeCount, boolean isLiked) {
            this.likeCount = likeCount;
            this.isLiked = isLiked;
        }
    }

    @Getter
    public static class DetailResponse {
        private Long programId;
        private String programName;
        private String thumbnailUrl;
        private ProgramType programType;
        private String eduTime;
        private Integer quarter;
        private LocalDateTime eduStartDate;
        private LocalDateTime eduEndDate;
        private LocalDateTime recruitStartDate;
        private LocalDateTime recruitEndDate;
        private String eduDate;
        private String eduPlace;
        private int capacity;
        private String targetAudience;
        private int eduPrice;
        private String needs;
        private String description;
        private String info;
        private String etc;
        private String classPlanUrl;
        private String classPlanOriginalName;
        private String institution;
        private String regionRestriction;
        private String dongName;
        private String instructorName;
        private int likeCount;
        private boolean isLiked;
        private boolean applied;

        public DetailResponse(Program program, int likeCount, boolean isLiked, boolean applied) {
            this.programId = program.getProgramId();
            this.programName = program.getProgramName();
            this.thumbnailUrl = program.getThumbnailUrl();
            this.programType = program.getProgramType();
            this.eduTime = program.getEduTime();
            this.quarter = program.getQuarter();
            this.eduStartDate = program.getEduStartDate();
            this.eduEndDate = program.getEduEndDate();
            this.recruitStartDate = program.getRecruitStartDate();
            this.recruitEndDate = program.getRecruitEndDate();
            this.eduDate = program.getEduDate();
            this.eduPlace = program.getEduPlace();
            this.capacity = program.getCapacity();
            this.targetAudience = program.getTargetAudience() != null ? program.getTargetAudience().getDescription() : null;
            this.eduPrice = program.getEduPrice();
            this.needs = program.getNeeds();
            this.description = program.getDescription();
            this.info = program.getInfo();
            this.etc = program.getEtc();
            this.classPlanUrl = program.getClassPlanUrl();
            this.classPlanOriginalName = program.getClassPlanOriginalName();
            this.institution = program.getInstitution();
            this.regionRestriction = program.getRegionRestriction() != null ? program.getRegionRestriction().getDescription() : null;
            this.dongName = program.getDongName();
            this.instructorName = program.getInstructor() != null ? program.getInstructor().getName() : null;
            this.likeCount = likeCount;
            this.isLiked = isLiked;
            this.applied = applied;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FormItemResponse {
        private Long id;
        private String label;
        private String type;
        private boolean required;
        private List<String> options;
    }

    @Getter
    @Builder
    public static class CreateResponse {
        private Long programId;
        private String applyUrl;
        private String thumbnailUrl;
        private String classPlanUrl;
        private List<String> proofFileUrls; // ✨ 증빙 파일 URL 목록 추가

        public CreateResponse(Long programId, String applyUrl,
                              String thumbnailUrl, String classPlanUrl,
                              List<String> proofFileUrls) {
            this.programId = programId;
            this.applyUrl = applyUrl;
            this.thumbnailUrl = thumbnailUrl;
            this.classPlanUrl = classPlanUrl;
            this.proofFileUrls = proofFileUrls;
        }
    }
}