package yuseong.com.guchung.admin.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
// 지금 아무런 인증 방식 없음
public class AdminController {

    private final AdminService adminService;
    private final ProgramService programService;

    @PostMapping("/signup")
    public GlobalResponseDto<Long> signUp(@RequestBody AdminRequestDto.SignUp requestDto) {
        Long adminId = adminService.signUp(requestDto);
        return GlobalResponseDto.success("관리자 회원가입 완료", adminId);
    }

    @PostMapping("/login")
    public GlobalResponseDto<Long> login(@RequestBody AdminRequestDto.Login requestDto) {
        Long adminId = adminService.login(requestDto);
        return GlobalResponseDto.success("관리자 로그인 성공", adminId);
    }

    @GetMapping("/{adminId}/programs")
    public GlobalResponseDto<Page<ProgramResponseDto.ListResponse>> getMyPrograms(
            @PathVariable Long adminId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<ProgramResponseDto.ListResponse> programs = programService.getProgramListByAdmin(adminId, pageable);
        return GlobalResponseDto.success("관리자별 프로그램 목록 조회 성공", programs);
    }
}