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
import yuseong.com.guchung.program.model.type.ProgramType;
import yuseong.com.guchung.program.service.ProgramService;

import java.io.IOException;
import java.util.List;

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

    @Operation(summary = "프로그램 생성", description = "새로운 교육 프로그램을 생성하고 강의계획서 파일 및 증빙 파일을 업로드합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalResponseDto<ProgramResponseDto.CreateResponse> createProgram(
            @Parameter(description = "프로그램 상세 정보 (JSON)", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart(value = "dto") ProgramRequestDto.Create requestDto,

            @Parameter(description = "강의계획서 파일 (선택)")
            @RequestPart(value = "classPlanFile", required = false) MultipartFile classPlanFile,

            @Parameter(description = "추가 증빙 파일 목록 (선택)")
            @RequestPart(value = "proofFiles", required = false) List<MultipartFile> proofFiles,

            @Parameter(description = "관리자 ID") @RequestParam Long adminId
    ) throws IOException {

        Program savedProgram = programService.createProgram(requestDto, classPlanFile, proofFiles, adminId);
        Long programId = savedProgram.getProgramId();

        String applyUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/program/{programId}/apply")
                .buildAndExpand(programId)
                .toUriString();

        ProgramResponseDto.CreateResponse response =
                new ProgramResponseDto.CreateResponse(programId, applyUrl, savedProgram.getClassPlanUrl());

        return GlobalResponseDto.success("프로그램 생성 완료", response);
    }

    @Operation(summary = "프로그램 정보 수정", description = "기존 프로그램의 정보를 수정합니다. (강의계획서 파일 포함)")
    @PutMapping(value = "/{programId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalResponseDto<Long> updateProgram(
            @Parameter(description = "수정할 프로그램 ID") @PathVariable Long programId,

            @Parameter(description = "프로그램 상세 정보 (JSON)", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart(value = "dto") ProgramRequestDto.Update requestDto,

            @Parameter(description = "새 강의계획서 파일 (선택)")
            @RequestPart(value = "newClassPlanFile", required = false) MultipartFile newClassPlanFile,

            @Parameter(description = "관리자 ID") @RequestParam Long adminId
    ) {
        Long updatedProgramId = programService.updateProgram(programId, requestDto, newClassPlanFile, adminId);
        return GlobalResponseDto.success("프로그램 수정", updatedProgramId);
    }

    @Operation(summary = "프로그램 증빙 파일 삭제", description = "특정 증빙 파일을 DB와 S3에서 모두 삭제합니다.")
    @DeleteMapping("/file/{programFileId}")
    public GlobalResponseDto<Void> deleteProofFile(
            @Parameter(description = "삭제할 ProgramFile ID") @PathVariable Long programFileId,
            @Parameter(description = "관리자 ID", required = true) @RequestParam Long adminId
    ) {
        programService.deleteProofFile(programFileId, adminId);
        return GlobalResponseDto.success("증빙 파일 삭제 완료", null);
    }

    @Operation(summary = "프로그램 전체 조회", description = "등록된 모든 프로그램을 페이징하여 조회합니다. 유저 주소에 따라 필터링되고, 좋아요 정보가 포함됩니다. (비로그인 가능)")
    @GetMapping
    public GlobalResponseDto<Page<ProgramResponseDto.ListResponse>> getProgramList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @Parameter(description = "사용자 ID (지역 필터링 및 좋아요 여부 확인용)", required = false)
            @RequestParam(required = false) Long userId
    ) {
        Page<ProgramResponseDto.ListResponse> programs = programService.getProgramList(pageable, userId);
        return GlobalResponseDto.success("전체 프로그램 목록 조회 성공", programs);
    }

    @Operation(summary = "유형별 프로그램 목록 조회",
            description = "프로그램 유형(유성형/자치형)과 사용자 주소에 따라 필터링된 목록을 조회합니다. (비로그인 가능)")
    @GetMapping("/type/{type}")
    public GlobalResponseDto<Page<ProgramResponseDto.ListResponse>> getProgramListByType(
            @Parameter(description = "프로그램 유형 (YUSEONG 또는 AUTONOMOUS)")
            @PathVariable ProgramType type,

            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,

            @Parameter(description = "사용자 ID (지역 필터링 및 좋아요 여부 확인용)", required = false)
            @RequestParam(required = false) Long userId
    ) {
        Page<ProgramResponseDto.ListResponse> programs =
                programService.getProgramListByType(type, pageable, userId);

        String typeName = type.name().equals("YUSEONG") ? "유성형" : "자치형";
        return GlobalResponseDto.success(typeName + " 프로그램 목록 조회 성공", programs);
    }

    @Operation(summary = "프로그램 상세 조회", description = "특정 프로그램의 상세 정보를 조회합니다. 현재 사용자의 좋아요 여부가 포함됩니다. (비로그인 가능)")
    @GetMapping("/{programId}")
    public GlobalResponseDto<ProgramResponseDto.DetailResponse> getProgramDetail(
            @Parameter(description = "조회할 프로그램 ID") @PathVariable Long programId,
            @Parameter(description = "사용자 ID (좋아요 여부 확인용)", required = false)
            @RequestParam(required = false) Long userId
    ) {
        ProgramResponseDto.DetailResponse program = programService.getProgramDetail(programId, userId);
        return GlobalResponseDto.success("프로그램 상세 조회 성공", program);
    }

    @Operation(summary = "프로그램 좋아요", description = "특정 프로그램에 대한 좋아요를 등록하거나 취소합니다.")
    @PostMapping("/{programId}/like")
    public GlobalResponseDto<Boolean> toggleProgramLike(
            @Parameter(description = "좋아요 대상 프로그램 ID") @PathVariable Long programId,
            @Parameter(description = "좋아요를 실행하는 사용자 ID", required = true) @RequestParam Long userId
    ) {
        boolean isLiked = programService.toggleProgramLike(userId, programId);
        String message = isLiked ? "프로그램을 찜했습니다." : "프로그램 찜을 취소했습니다.";
        return GlobalResponseDto.success(message, isLiked);
    }
}