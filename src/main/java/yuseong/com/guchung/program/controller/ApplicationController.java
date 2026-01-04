package yuseong.com.guchung.program.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuseong.com.guchung.auth.dto.GlobalResponseDto;
import yuseong.com.guchung.program.dto.ApplicationRequestDto;
import yuseong.com.guchung.program.dto.ApplicationResponseDto;
import yuseong.com.guchung.program.service.ApplicationService;

import java.util.List;

@Tag(name = "Program Application", description = "교육 프로그램 신청 및 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/program")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "관리자용: 프로그램별 신청 내역 조회", description = "특정 프로그램에 신청한 모든 사용자의 상세 정보와 설문 답변을 조회합니다.")
    @GetMapping("/{programId}/applications/admin")
    public GlobalResponseDto<List<ApplicationResponseDto.AdminListResponse>> getProgramApplicationsForAdmin(
            @PathVariable Long programId
    ) {
        List<ApplicationResponseDto.AdminListResponse> responses = applicationService.getApplicationsByProgram(programId);
        return GlobalResponseDto.success("신청자 상세 목록 조회 성공", responses);
    }

    @Operation(summary = "프로그램 신청", description = "사용자가 특정 프로그램에 신청서를 제출합니다.")
    @PostMapping("/{programId}/apply")
    public GlobalResponseDto<Long> applyProgram(
            @PathVariable Long programId,
            @RequestParam Long userId,
            @RequestBody ApplicationRequestDto.Apply requestDto
    ) {
        Long applicationId = applicationService.applyProgram(programId, userId, requestDto);
        return GlobalResponseDto.success("프로그램 신청 완료", applicationId);
    }

    @Operation(summary = "프로그램 신청 취소")
    @DeleteMapping("/application/{applicationId}")
    public ResponseEntity<Void> cancelApplication(
            @PathVariable Long applicationId,
            @RequestParam Long userId
    ) {
        applicationService.cancelApplication(applicationId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "사용자용: 내 신청 목록 조회")
    @GetMapping("/applications")
    public GlobalResponseDto<List<ApplicationResponseDto.ListResponse>> getUserApplications(
            @RequestParam Long userId
    ) {
        List<ApplicationResponseDto.ListResponse> applications = applicationService.getUserApplications(userId);
        return GlobalResponseDto.success("신청 목록 조회 성공", applications);
    }

    @Operation(summary = "프로그램 현재 신청 인원 수 조회")
    @GetMapping("/{programId}/applicants/count")
    public GlobalResponseDto<Integer> getApplicantCount(@PathVariable Long programId) {
        int count = applicationService.getApplicationCount(programId);
        return GlobalResponseDto.success("신청 인원 조회 성공", count);
    }
}