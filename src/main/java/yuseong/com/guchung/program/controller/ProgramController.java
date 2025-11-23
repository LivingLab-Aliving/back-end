package yuseong.com.guchung.program.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import yuseong.com.guchung.auth.dto.GlobalResponseDto;
import yuseong.com.guchung.program.dto.ProgramRequestDto;
import yuseong.com.guchung.program.dto.ProgramResponseDto;
import yuseong.com.guchung.program.model.Program;
import yuseong.com.guchung.program.service.ProgramService;

import java.io.IOException;

@Tag(name = "Education Program", description = "교육 프로그램 생성, 수정, 조회 및 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/program")
public class ProgramController {

    private final ProgramService programService;

    @Operation(summary = "프로그램명 중복 확인", description = "프로그램 생성 전 이름 중복 여부를 확인합니다.")
    @GetMapping("/check-name")
    public GlobalResponseDto<Boolean> checkProgramName(
            @Parameter(description = "중복 확인할 프로그램명") @RequestParam String name
    ) {
        boolean isDuplicated = programService.checkProgramName(name);
        String message = isDuplicated ? "중복된 이름입니다." : "사용 가능한 이름입니다.";
        return GlobalResponseDto.success(message, isDuplicated);
    }

    @Operation(summary = "프로그램 생성", description = "새로운 교육 프로그램을 생성하고 강의계획서 파일을 업로드합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalResponseDto<ProgramResponseDto.CreateResponse> createProgram(
            @Parameter(description = "프로그램 상세 정보 (JSON)", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart(value = "dto") ProgramRequestDto.Create requestDto,

            @Parameter(description = "강의계획서 파일 (선택)")
            @RequestPart(value = "file", required = false) MultipartFile file,

            @Parameter(description = "관리자 ID") @RequestParam Long adminId
    ) throws IOException {

        Program savedProgram = programService.createProgram(requestDto, file, adminId);
        Long programId = savedProgram.getProgramId();

        String applyUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/program/{programId}/apply")
                .buildAndExpand(programId)
                .toUriString();

        ProgramResponseDto.CreateResponse response =
                new ProgramResponseDto.CreateResponse(programId, applyUrl, savedProgram.getClassPlanUrl());

        return GlobalResponseDto.success("프로그램 생성 완료", response);
    }

    @Operation(summary = "프로그램 정보 수정", description = "기존 프로그램의 정보를 수정합니다. (파일 수정 미포함)")
    @PutMapping("/{programId}")
    public GlobalResponseDto<Long> updateProgram(
            @Parameter(description = "수정할 프로그램 ID") @PathVariable Long programId,
            @RequestBody ProgramRequestDto.Update requestDto,
            @Parameter(description = "관리자 ID") @RequestParam Long adminId
    ) {
        Long updatedProgramId = programService.updateProgram(programId, requestDto, adminId);
        return GlobalResponseDto.success("프로그램 수정", updatedProgramId);
    }

    @Operation(summary = "프로그램 전체 조회", description = "등록된 모든 프로그램을 페이징하여 조회합니다. 유저 주소에 따라 필터링될 수 있습니다.")
    @GetMapping
    public GlobalResponseDto<Page<ProgramResponseDto.ListResponse>> getProgramList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @Parameter(description = "사용자 ID (지역 필터링용)") @RequestParam Long userId
    ) {
        Page<ProgramResponseDto.ListResponse> programs = programService.getProgramList(pageable, userId);
        return GlobalResponseDto.success("전체 프로그램 목록 조회 성공", programs);
    }

    @Operation(summary = "프로그램 상세 조회", description = "특정 프로그램의 상세 정보를 조회합니다.")
    @GetMapping("/{programId}")
    public GlobalResponseDto<ProgramResponseDto.DetailResponse> getProgramDetail(
            @Parameter(description = "조회할 프로그램 ID") @PathVariable Long programId
    ) {
        ProgramResponseDto.DetailResponse program = programService.getProgramDetail(programId);
        return GlobalResponseDto.success("프로그램 상세 조회 성공", program);
    }
}