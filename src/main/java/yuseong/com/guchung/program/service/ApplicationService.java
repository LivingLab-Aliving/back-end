package yuseong.com.guchung.program.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuseong.com.guchung.auth.model.User;
import yuseong.com.guchung.auth.repository.UserRepository;
import yuseong.com.guchung.program.dto.ApplicationRequestDto;
import yuseong.com.guchung.program.dto.ApplicationResponseDto;
import yuseong.com.guchung.program.model.Application;
import yuseong.com.guchung.program.model.ApplicationAnswer;
import yuseong.com.guchung.program.model.Program;
import yuseong.com.guchung.program.model.ProgramFormItem;
import yuseong.com.guchung.program.model.type.ApplicationStatus;
import yuseong.com.guchung.program.repository.ApplicationAnswerRepository;
import yuseong.com.guchung.program.repository.ApplicationRepository;
import yuseong.com.guchung.program.repository.ProgramFormItemRepository;
import yuseong.com.guchung.program.repository.ProgramRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final ProgramFormItemRepository formItemRepository;
    private final ApplicationAnswerRepository answerRepository;

    /**
     * 관리자용: 특정 프로그램의 전체 신청자 목록 조회
     */
    public List<ApplicationResponseDto.AdminListResponse> getApplicationsByProgram(Long programId) {
        // 1. 해당 프로그램의 모든 신청서 조회
        List<Application> applications = applicationRepository.findByProgram_ProgramId(programId);

        // 2. 각 신청서별로 답변 정보를 매핑하여 반환
        return applications.stream()
                .map(app -> {
                    // 이 신청서(app)에 작성된 답변들 가져오기
                    List<ApplicationAnswer> answers = answerRepository.findByApplication(app);
                    return ApplicationResponseDto.AdminListResponse.from(app, answers);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Long applyProgram(Long programId, Long userId, ApplicationRequestDto.Apply requestDto) {
        User user = userRepository.findById(userId) // Integer userId 대응
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("프로그램을 찾을 수 없습니다. ID: " + programId));

        if (LocalDateTime.now().isBefore(program.getRecruitStartDate()) || LocalDateTime.now().isAfter(program.getRecruitEndDate())) {
            throw new IllegalArgumentException("현재 신청 기간이 아닙니다.");
        }

        if (applicationRepository.existsByUserAndProgram(user, program)) {
            throw new IllegalArgumentException("이미 신청한 프로그램입니다.");
        }

        if (applicationRepository.countByProgram_ProgramId(programId) >= program.getCapacity()) {
            throw new IllegalStateException("신청 정원이 마감되었습니다.");
        }

        Application application = Application.builder()
                .user(user).program(program).status(ApplicationStatus.PENDING).build();

        Application savedApplication = applicationRepository.save(application);

        if (requestDto.getAnswers() != null) {
            List<ApplicationAnswer> answers = requestDto.getAnswers().stream()
                    .map(dto -> ApplicationAnswer.builder()
                            .application(savedApplication)
                            .formItem(formItemRepository.findById(dto.getFormItemId()).orElse(null))
                            .answer(dto.getAnswer()).build())
                    .collect(Collectors.toList());
            answerRepository.saveAll(answers);
        }
        return savedApplication.getApplicationId();
    }

    public int getApplicationCount(Long programId) {
        return applicationRepository.countByProgram_ProgramId(programId);
    }

    @Transactional
    public void cancelApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청 정보 없음"));
        if (!application.getUser().getUserId().equals(userId.intValue())) {
            throw new IllegalArgumentException("권한 없음");
        }
        application.updateStatus(ApplicationStatus.CANCELED);
    }

    public List<ApplicationResponseDto.ListResponse> getUserApplications(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return applicationRepository.findByUser(user).stream()
                .map(ApplicationResponseDto.ListResponse::from)
                .collect(Collectors.toList());
    }
}