package yuseong.com.guchung.program.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import yuseong.com.guchung.auth.dto.GlobalResponseDto;
import yuseong.com.guchung.program.dto.ProgramRequestDto;
import yuseong.com.guchung.program.dto.ProgramResponseDto;
import yuseong.com.guchung.program.service.ProgramService;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/program")
public class ProgramController {

    private final ProgramService programService;

    // 프로그램명 중복체크
    @GetMapping("/check-name")
    public GlobalResponseDto<Boolean> checkProgramName(@RequestParam String name) {
        boolean isDuplicated = programService.checkProgramName(name);
        String message = isDuplicated ? "중복된 이름입니다." : "사용 가능한 이름입니다.";
        return GlobalResponseDto.success(message, isDuplicated);
    }

    // 프로그램 생성, 현재 인증 X
    @PostMapping
    public GlobalResponseDto<ProgramResponseDto.CreateResponse> createProgram(
            @RequestBody ProgramRequestDto.Create requestDto,
            @RequestParam Long adminId
    ) {
        Long programId = programService.createProgram(requestDto, adminId);

        // 프로그램 생성 시 도메인 생성, 도메인은 수정가능 대신, 새로운 URL로 생성하려면 로직 수정해야됨
        String applyUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/program/{programId}/apply")
                .buildAndExpand(programId)
                .toUriString();

        ProgramResponseDto.CreateResponse response =
                new ProgramResponseDto.CreateResponse(programId, applyUrl);

        return GlobalResponseDto.success("프로그램 생성 완료", response);
    }

    // 프로그램 수정
    @PutMapping("/{programId}")
    public GlobalResponseDto<Long> updateProgram(
            @PathVariable Long programId,
            @RequestBody ProgramRequestDto.Update requestDto,
            @RequestParam Long adminId
    ) {
        Long updatedProgramId = programService.updateProgram(programId, requestDto, adminId);
        return GlobalResponseDto.success("프로그램 수정", updatedProgramId);
    }


    @GetMapping
    public GlobalResponseDto<Page<ProgramResponseDto.ListResponse>> getProgramList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam Long userId
    ) {
        Page<ProgramResponseDto.ListResponse> programs = programService.getProgramList(pageable, userId);
        return GlobalResponseDto.success("전체 프로그램 목록 조회 성공", programs);
    }

    @GetMapping("/{programId}")
    public GlobalResponseDto<ProgramResponseDto.DetailResponse> getProgramDetail(
            @PathVariable Long programId
    ) {
        ProgramResponseDto.DetailResponse program = programService.getProgramDetail(programId);
        return GlobalResponseDto.success("프로그램 상세 조회 성공", program);
    }
}