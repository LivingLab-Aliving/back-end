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

@Tag(name = "Program Application", description = "교육 프로그램 신청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/program")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "프로그램 신청", description = "사용자가 특정 프로그램에 신청서를 제출합니다. (정원/기간/중복 신청 검증)")
    @PostMapping("/{programId}/apply")
    public GlobalResponseDto<Long> applyProgram(
            @Parameter(description = "신청 대상 프로그램 ID") @PathVariable Long programId,
            @Parameter(description = "신청하는 사용자 ID", required = true) @RequestParam Long userId,
            @RequestBody ApplicationRequestDto.Apply requestDto
    ) {
        Long applicationId = applicationService.applyProgram(programId, userId, requestDto);
        return GlobalResponseDto.success("프로그램 신청 완료", applicationId);
    }

    @Operation(summary = "프로그램 신청 취소", description = "사용자가 자신의 신청을 취소합니다.")
    @DeleteMapping("/application/{applicationId}")
    public ResponseEntity<Void> cancelApplication(
            @Parameter(description = "취소할 신청 정보 ID") @PathVariable Long applicationId,
            @Parameter(description = "신청하는 사용자 ID", required = true) @RequestParam Long userId
    ) {
        applicationService.cancelApplication(applicationId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }

    @Operation(summary = "사용자 신청 목록 조회", description = "특정 사용자가 신청한 모든 프로그램 목록을 조회합니다.")
    @GetMapping("/applications")
    public GlobalResponseDto<List<ApplicationResponseDto.ListResponse>> getUserApplications(
            @Parameter(description = "신청 목록을 조회할 사용자 ID", required = true) @RequestParam Long userId
    ) {
        List<ApplicationResponseDto.ListResponse> applications = applicationService.getUserApplications(userId);
        return GlobalResponseDto.success("신청 목록 조회 성공", applications);
    }

    @Operation(summary = "프로그램 현재 신청 인원 조회", description = "특정 프로그램에 현재 신청한 인원 수를 조회합니다.")
    @GetMapping("/{programId}/applicants/count")
    public GlobalResponseDto<Integer> getApplicantCount(
            @Parameter(description = "조회 대상 프로그램 ID") @PathVariable Long programId
    ) {
        int count = applicationService.getApplicationCount(programId);
        return GlobalResponseDto.success("신청 인원 조회 성공", count);
    }
}