package yuseong.com.guchung.program.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import yuseong.com.guchung.auth.dto.GlobalResponseDto;
import yuseong.com.guchung.program.dto.ProgramRequestDto;
import yuseong.com.guchung.program.dto.ProgramResponseDto;
import yuseong.com.guchung.program.model.Program;
import yuseong.com.guchung.program.model.type.ProgramType;
import yuseong.com.guchung.program.service.ProgramService;
import yuseong.com.guchung.program.service.ProgramService.FileDownloadInfo;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "Education Program", description = "교육 프로그램 및 신청폼 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/program")
public class ProgramController {

    private final ProgramService programService;

    @Operation(summary = "프로그램명 중복 확인")
    @GetMapping("/check-name")
    public GlobalResponseDto<Boolean> checkProgramName(@RequestParam String name) {
        boolean isDuplicated = programService.checkProgramName(name);
        String message = isDuplicated ? "중복된 이름입니다." : "사용 가능한 이름입니다.";
        return GlobalResponseDto.success(message, isDuplicated);
    }

    @Operation(summary = "프로그램 및 신청폼 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalResponseDto<ProgramResponseDto.CreateResponse> createProgram(
            @RequestPart(value = "dto") ProgramRequestDto.Create requestDto,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "classPlanFile", required = false) MultipartFile classPlanFile,
            @RequestPart(value = "proofFiles", required = false) List<MultipartFile> proofFiles,
            @RequestParam Long adminId
    ) throws IOException {

        Program savedProgram = programService.createProgram(requestDto, thumbnailFile, classPlanFile, proofFiles, adminId);
        Long programId = savedProgram.getProgramId();

        String applyUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/program/{programId}/apply")
                .buildAndExpand(programId).toUriString();

        ProgramResponseDto.CreateResponse response = new ProgramResponseDto.CreateResponse(
                programId, applyUrl, savedProgram.getThumbnailUrl(),
                savedProgram.getClassPlanUrl(), programService.extractProofFileUrls(savedProgram)
        );

        return GlobalResponseDto.success("프로그램 및 신청폼 생성 완료", response);
    }

    @Operation(summary = "프로그램 정보 수정")
    @PutMapping(value = "/{programId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalResponseDto<Long> updateProgram(
            @PathVariable Long programId,
            @RequestPart(value = "dto") ProgramRequestDto.Update requestDto,
            @RequestPart(value = "newThumbnailFile", required = false) MultipartFile newThumbnailFile,
            @RequestPart(value = "newClassPlanFile", required = false) MultipartFile newClassPlanFile,
            @RequestParam Long adminId
    ) throws IOException {
        Long updatedId = programService.updateProgram(programId, requestDto, newThumbnailFile, newClassPlanFile, adminId);
        return GlobalResponseDto.success("프로그램 수정 완료", updatedId);
    }

    @Operation(summary = "신청폼 항목 조회")
    @GetMapping("/{programId}/form")
    public GlobalResponseDto<List<ProgramResponseDto.FormItemResponse>> getProgramForm(@PathVariable Long programId) {
        return GlobalResponseDto.success("신청폼 조회 성공", programService.getFormItems(programId));
    }

    @Operation(summary = "신청폼 항목만 개별 수정")
    @PutMapping("/{programId}/form")
    public GlobalResponseDto<Void> updateProgramForm(
            @PathVariable Long programId,
            @RequestBody List<ProgramRequestDto.FormItemRequest> additionalFields,
            @RequestParam Long adminId
    ) {
        programService.updateFormItems(programId, additionalFields, adminId);
        return GlobalResponseDto.success("신청폼 수정 완료", null);
    }

    @Operation(summary = "프로그램 상세 조회")
    @GetMapping("/{programId}")
    public GlobalResponseDto<ProgramResponseDto.DetailResponse> getProgramDetail(
            @PathVariable Long programId,
            @RequestParam(required = false) Long userId
    ) {
        return GlobalResponseDto.success("조회 성공", programService.getProgramDetail(programId, userId));
    }

    @Operation(summary = "전체 프로그램 목록 조회")
    @GetMapping
    public GlobalResponseDto<Page<ProgramResponseDto.ListResponse>> getProgramList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String dongName,
            @RequestParam(required = false) ProgramType programType,
            @RequestParam(required = false) Long adminId
    ) {
        if (adminId != null) {
            return GlobalResponseDto.success("관리자용 목록 조회 성공",
                    programService.getProgramListByAdmin(adminId, pageable));
        }

        return GlobalResponseDto.success("목록 조회 성공",
                programService.getProgramList(pageable, userId, dongName, programType));
    }

    @Operation(summary = "첨부 파일 다운로드")
    @GetMapping("/file/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileUrl, @RequestParam(required = false) String originalFileName) throws IOException {
        FileDownloadInfo info = programService.downloadFile(fileUrl, originalFileName);
        String encodedFileName = URLEncoder.encode(info.getOriginalFileName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(info.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .body(info.getResource());
    }

    @Operation(summary = "프로그램 삭제")
    @DeleteMapping("/{programId}")
    public GlobalResponseDto<Void> deleteProgram(@PathVariable Long programId, @RequestParam Long adminId) {
        programService.deleteProgram(programId, adminId);
        return GlobalResponseDto.success("프로그램 삭제 완료", null);
    }

}