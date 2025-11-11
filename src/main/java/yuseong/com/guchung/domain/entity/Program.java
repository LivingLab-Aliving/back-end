package yuseong.com.guchung.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import yuseong.com.guchung.domain.type.RegionRestriction;
import yuseong.com.guchung.domain.type.TargetAudience;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "program")
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private Integer programId;

    @Column(name = "program_name", nullable = false)
    private String programName;

    @Column(name = "edu_time")
    private String eduTime;

    @Column(name = "edu_date")
    private String eduDate;

    @Column(name = "edu_place")
    private String eduPlace;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience")
    private TargetAudience targetAudience; // ENUM

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

    @Column(name = "class_plan_url")
    private String classPlanUrl; // 강의계획서

    private String institution; // 교육기관

    @Enumerated(EnumType.STRING)
    @Column(name = "region_restriction")
    private RegionRestriction regionRestriction; // ENUM

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Program(N) <-> Admin(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    // Program(N) <-> Instructor(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    // Program(1) <-> Application(N)
    @OneToMany(mappedBy = "program")
    private List<Application> applications = new ArrayList<>();
}