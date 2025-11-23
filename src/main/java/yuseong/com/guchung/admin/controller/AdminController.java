package yuseong.com.guchung.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import yuseong.com.guchung.admin.dto.AdminRequestDto;
import yuseong.com.guchung.auth.dto.GlobalResponseDto;
import yuseong.com.guchung.program.dto.ProgramResponseDto;
import yuseong.com.guchung.admin.service.AdminService;
import yuseong.com.guchung.program.service.ProgramService;

@Tag(name = "Admin", description = "관리자 회원가입, 로그인 및 프로그램 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
// 지금 아무런 인증 방식 없음
public class AdminController {

    private final AdminService adminService;
    private final ProgramService programService;

    @Operation(summary = "관리자 회원가입", description = "새로운 관리자 계정을 생성합니다.")
    @PostMapping("/signup")
    public GlobalResponseDto<Long> signUp(@RequestBody AdminRequestDto.SignUp requestDto) {
        Long adminId = adminService.signUp(requestDto);
        return GlobalResponseDto.success("관리자 회원가입 완료", adminId);
    }

    @Operation(summary = "관리자 로그인", description = "관리자 계정으로 로그인합니다.")
    @PostMapping("/login")
    public GlobalResponseDto<Long> login(@RequestBody AdminRequestDto.Login requestDto) {
        Long adminId = adminService.login(requestDto);
        return GlobalResponseDto.success("관리자 로그인 성공", adminId);
    }

    @Operation(summary = "내 프로그램 조회", description = "특정 관리자가 생성한 프로그램 목록을 조회합니다.")
    @GetMapping("/{adminId}/programs")
    public GlobalResponseDto<Page<ProgramResponseDto.ListResponse>> getMyPrograms(
            @Parameter(description = "관리자 ID") @PathVariable Long adminId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<ProgramResponseDto.ListResponse> programs = programService.getProgramListByAdmin(adminId, pageable);
        return GlobalResponseDto.success("관리자별 프로그램 목록 조회 성공", programs);
    }
}