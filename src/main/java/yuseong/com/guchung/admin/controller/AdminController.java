package yuseong.com.guchung.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import yuseong.com.guchung.admin.dto.AdminRequestDto;
import yuseong.com.guchung.admin.service.AdminService;
import yuseong.com.guchung.auth.dto.GlobalResponseDto;
import yuseong.com.guchung.jwt.JwtToken;
import yuseong.com.guchung.program.dto.ProgramResponseDto;
import yuseong.com.guchung.program.service.ProgramService;

@Slf4j
@Tag(name = "Admin", description = "관리자 회원가입, 로그인 및 프로그램 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final ProgramService programService;

    @Operation(summary = "관리자 회원가입", description = "새로운 관리자 계정을 생성합니다.")
    @PostMapping("/signup")
    public GlobalResponseDto<Long> signUp(@RequestBody AdminRequestDto.SignUp requestDto) {
        Long adminId = adminService.signUp(requestDto);
        return GlobalResponseDto.success("관리자 회원가입 완료", adminId);
    }

    @Operation(summary = "관리자 로그인", description = "로그인 성공 시 JWT 토큰(AccessToken)을 반환합니다.")
    @PostMapping("/login")
    public GlobalResponseDto<JwtToken> login(@RequestBody AdminRequestDto.Login requestDto) {
        // [수정] Service가 JwtToken을 반환하므로 Controller도 JwtToken 반환
        JwtToken token = adminService.login(requestDto);
        return GlobalResponseDto.success("관리자 로그인 성공", token);
    }

    @Operation(summary = "내 프로그램 조회", description = "특정 관리자가 생성한 프로그램 목록을 조회합니다. (본인 확인 포함)")
    @GetMapping("/{adminId}/programs")
    public GlobalResponseDto<Page<ProgramResponseDto.ListResponse>> getMyPrograms(
            @Parameter(description = "관리자 ID") @PathVariable Long adminId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails // 현재 로그인한 사용자 정보 주입
    ) {
        // [보안 추가] 토큰에 있는 ID(loginId)와 요청한 adminId가 일치하는지 검증하는 로직이 필요하다면 여기서 추가
        // 예: adminRepository.findByLoginId(userDetails.getUsername()).getAdminId() == adminId
        log.info("요청한 관리자 LoginID: {}", userDetails.getUsername());

        Page<ProgramResponseDto.ListResponse> programs = programService.getProgramListByAdmin(adminId, pageable);
        return GlobalResponseDto.success("관리자별 프로그램 목록 조회 성공", programs);
    }
}