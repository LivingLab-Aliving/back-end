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

    @Transactional
    public Long applyProgram(Long programId, Long userId, ApplicationRequestDto.Apply requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("프로그램을 찾을 수 없습니다. ID: " + programId));

        LocalDateTime now = LocalDateTime.now();

        log.info("현재 시간: {}", now);
        log.info("모집 시작: {}", program.getRecruitStartDate());
        log.info("모집 종료: {}", program.getRecruitEndDate());

        if (now.isBefore(program.getRecruitStartDate()) || now.isAfter(program.getRecruitEndDate())) {
            throw new IllegalArgumentException("현재 신청 기간이 아닙니다.");
        }

        if (applicationRepository.existsByUserAndProgram(user, program)) {
            throw new IllegalArgumentException("이미 신청한 프로그램입니다.");
        }

        int currentApplicants = applicationRepository.countByProgram_ProgramId(programId);
        if (currentApplicants >= program.getCapacity()) {
            throw new IllegalStateException("신청 정원이 마감되었습니다.");
        }

        Application application = Application.builder()
                .user(user)
                .program(program)
                .status(ApplicationStatus.PENDING)
                .build();

        Application savedApplication = applicationRepository.save(application);

        if (requestDto.getAnswers() != null && !requestDto.getAnswers().isEmpty()) {
            List<Long> itemIds = requestDto.getAnswers().stream()
                    .map(ApplicationRequestDto.AnswerDto::getFormItemId)
                    .collect(Collectors.toList());

            Map<Long, ProgramFormItem> formItemsMap = formItemRepository.findAllById(itemIds)
                    .stream()
                    .collect(Collectors.toMap(ProgramFormItem::getId, item -> item));

            List<ApplicationAnswer> answers = requestDto.getAnswers().stream()
                    .map(answerDto -> {
                        ProgramFormItem formItem = formItemsMap.get(answerDto.getFormItemId());

                        if (formItem == null) {
                            log.warn("Form Item ID {} not found for program {}", answerDto.getFormItemId(), programId);
                            // throw new IllegalArgumentException("유효하지 않은 양식 항목 ID가 포함되어 있습니다.");
                            return null;
                        }

                        return ApplicationAnswer.builder()
                                .application(savedApplication)
                                .formItem(formItem)
                                .answer(answerDto.getAnswer())
                                .build();
                    })
                    .filter(java.util.Objects::nonNull)
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
                .orElseThrow(() -> new IllegalArgumentException("신청 정보를 찾을 수 없습니다. ID: " + applicationId));

        if (!application.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("신청 취소 권한이 없습니다.");
        }

        ApplicationStatus currentStatus = application.getStatus();
        if (currentStatus == ApplicationStatus.APPROVED) {
            throw new IllegalStateException("이미 승인된 신청은 취소할 수 없습니다.");
        }
        if (currentStatus == ApplicationStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 신청입니다.");
        }

        application.updateStatus(ApplicationStatus.CANCELED);
        log.info("Application ID {} canceled by User ID {}", applicationId, userId);
    }

    public List<ApplicationResponseDto.ListResponse> getUserApplications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        List<Application> applications = applicationRepository.findByUser(user);

        return applications.stream()
                .map(ApplicationResponseDto.ListResponse::from)
                .collect(Collectors.toList());
    }
}