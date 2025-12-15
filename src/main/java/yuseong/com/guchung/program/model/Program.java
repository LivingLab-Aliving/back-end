package yuseong.com.guchung.program.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import yuseong.com.guchung.admin.model.Admin;
import yuseong.com.guchung.program.dto.ProgramRequestDto;
import yuseong.com.guchung.auth.model.Instructor;
import yuseong.com.guchung.program.model.type.RegionRestriction;
import yuseong.com.guchung.program.model.type.TargetAudience;
import yuseong.com.guchung.program.model.type.ProgramType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Setter
@Table(name = "program")
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private Long programId;

    @Column(name = "program_name", nullable = false, unique = true)
    private String programName;

    @Column(name = "edu_time")
    private String eduTime;

    private Integer quarter;

    private LocalDateTime eduStartDate;
    private LocalDateTime eduEndDate;

    private LocalDateTime recruitStartDate;
    private LocalDateTime recruitEndDate;

    @Column(name = "edu_date")
    private String eduDate;

    @Column(name = "edu_place")
    private String eduPlace;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience")
    private TargetAudience targetAudience;

    @Column(name = "edu_price", nullable = false)
    private int eduPrice;

    @Lob
    private String needs;

    @Lob
    private String description;

    @Lob
    private String info;

    @Lob
    private String etc;

    @Column(name = "class_plan_url", length = 512)
    private String classPlanUrl;

    @Column(name = "class_plan_original_name", length = 255)
    private String classPlanOriginalName;

    private String institution;

    @Enumerated(EnumType.STRING)
    @Column(name = "region_restriction")
    private RegionRestriction regionRestriction;

    @Enumerated(EnumType.STRING)
    @Column(name = "program_type", nullable = false)
    private ProgramType programType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @OneToMany(mappedBy = "program")
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramFile> attachedFiles = new ArrayList<>();

    public void update(ProgramRequestDto.Update dto, Instructor instructor) {
        this.programName = dto.getProgramName();
        this.eduTime = dto.getEduTime();
        this.quarter = dto.getQuarter();
        this.eduStartDate = dto.getEduStartDate();
        this.eduEndDate = dto.getEduEndDate();
        this.recruitStartDate = dto.getRecruitStartDate();
        this.recruitEndDate = dto.getRecruitEndDate();
        this.eduPlace = dto.getEduPlace();
        this.capacity = dto.getCapacity();
        this.eduPrice = dto.getEduPrice();
        this.needs = dto.getNeeds();
        this.institution = dto.getInstitution();
        this.regionRestriction = dto.getRegionRestriction();
        this.targetAudience = dto.getTargetAudience();
        this.instructor = instructor;
        this.classPlanUrl = dto.getClassPlanUrl();
        this.description = dto.getDescription();
        this.info = dto.getInfo();
        this.etc = dto.getEtc();
        this.programType = dto.getProgramType();
    }

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProgramFormItem> formItems = new ArrayList<>();

    @Builder
    public Program(String programName, ProgramType programType, String eduTime, Integer quarter, LocalDateTime eduStartDate,
                   LocalDateTime eduEndDate, LocalDateTime recruitStartDate, LocalDateTime recruitEndDate,
                   String eduPlace, int capacity, TargetAudience targetAudience, int eduPrice,
                   String needs, String description, String info, String etc, String classPlanUrl,
                   String institution, RegionRestriction regionRestriction, Admin admin, Instructor instructor, String classPlanOriginalName) {
        this.programName = programName;
        this.programType = programType;
        this.eduTime = eduTime;
        this.quarter = quarter;
        this.eduStartDate = eduStartDate;
        this.eduEndDate = eduEndDate;
        this.recruitStartDate = recruitStartDate;
        this.recruitEndDate = recruitEndDate;
        this.eduPlace = eduPlace;
        this.capacity = capacity;
        this.targetAudience = targetAudience;
        this.eduPrice = eduPrice;
        this.needs = needs;
        this.description = description;
        this.info = info;
        this.etc = etc;
        this.classPlanUrl = classPlanUrl;
        this.institution = institution;
        this.regionRestriction = regionRestriction;
        this.admin = admin;
        this.instructor = instructor;
        this.classPlanOriginalName = classPlanOriginalName;
    }
}