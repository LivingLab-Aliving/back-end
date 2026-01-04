package yuseong.com.guchung.program.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yuseong.com.guchung.admin.model.Admin;
import yuseong.com.guchung.admin.repository.AdminRepository;
import yuseong.com.guchung.auth.model.Instructor;
import yuseong.com.guchung.auth.model.User;
import yuseong.com.guchung.auth.repository.InstructorRepository;
import yuseong.com.guchung.auth.repository.UserRepository;
import yuseong.com.guchung.client.S3Uploader;
import yuseong.com.guchung.program.dto.ProgramRequestDto;
import yuseong.com.guchung.program.dto.ProgramResponseDto;
import yuseong.com.guchung.program.model.*;
import yuseong.com.guchung.program.model.type.ProgramFormType;
import yuseong.com.guchung.program.repository.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramService {

    private final ProgramRepository programRepository;
    private final AdminRepository adminRepository;
    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final ProgramFileRepository programFileRepository;
    private final ProgramFormItemRepository formItemRepository;
    private final ApplicationRepository applicationRepository;
    private final ProgramLikeRepository programLikeRepository;

    /**
     * 프로그램 생성 및 신청폼 항목 저장
     */
    @Transactional
    public Program createProgram(ProgramRequestDto.Create dto, MultipartFile thumb, MultipartFile plan, List<MultipartFile> proofs, Long adminId) throws IOException {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new IllegalArgumentException("관리자 없음"));

        Instructor instructor = null;
        if (dto.getInstructorId() != null) instructor = instructorRepository.findById(dto.getInstructorId()).orElse(null);

        String thumbUrl = (thumb != null) ? s3Uploader.uploadFile(thumb, "program/thumb") : null;
        String planUrl = (plan != null) ? s3Uploader.uploadFile(plan, "program/plan") : null;

        Program program = Program.builder()
                .programName(dto.getProgramName()).thumbnailUrl(thumbUrl)
                .eduTime(dto.getEduTime()).quarter(dto.getQuarter())
                .eduStartDate(dto.getEduStartDate()).eduEndDate(dto.getEduEndDate())
                .recruitStartDate(dto.getRecruitStartDate()).recruitEndDate(dto.getRecruitEndDate())
                .eduPlace(dto.getEduPlace()).capacity(dto.getCapacity())
                .targetAudience(dto.getTargetAudience()).eduPrice(dto.getEduPrice())
                .description(dto.getDescription()).institution(dto.getInstitution())
                .regionRestriction(dto.getRegionRestriction()).programType(dto.getProgramType())
                .classPlanUrl(planUrl).admin(admin).instructor(instructor).build();

        Program savedProgram = programRepository.save(program);

        // 신청폼 항목 저장
        saveFormItems(savedProgram, dto.getAdditionalFields());

        // 증빙 파일 저장
        if (proofs != null) {
            for (MultipartFile file : proofs) {
                String url = s3Uploader.uploadFile(file, "program/proof");
                programFileRepository.save(ProgramFile.builder().fileUrl(url).originalName(file.getOriginalFilename()).program(savedProgram).build());
            }
        }
        return savedProgram;
    }

    /**
     * 프로그램 정보 수정 및 신청폼 재등록
     */
    @Transactional
    public Long updateProgram(Long id, ProgramRequestDto.Update dto, MultipartFile thumb, MultipartFile plan, Long adminId) throws IOException {
        Program program = programRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("프로그램 없음"));

        Instructor instructor = null;
        if (dto.getInstructorId() != null) {
            instructor = instructorRepository.findById(dto.getInstructorId()).orElse(null);
        }

        program.update(dto, instructor);

        return program.getProgramId();
    }

    /**
     * 신청폼 항목만 수정 (ApplicationEdit.js에서 호출 시 에러 방지용)
     * 🌟 이 메서드는 Program 엔티티를 직접 update 하지 않아 programType 유실 에러를 방지합니다.
     */
    @Transactional
    public void updateFormItems(Long programId, List<ProgramRequestDto.FormItemRequest> fields, Long adminId) {
        // 1. 단순 존재 여부와 권한만 확인 (엔티티를 수정하지 않음)
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("프로그램 없음"));

        if (!program.getAdmin().getAdminId().equals(adminId)) {
            throw new IllegalArgumentException("권한 없음");
        }

        // 2. 🌟 기존 항목 삭제
        // 더티 체킹에 의한 Program 테이블 업데이트를 방지하기 위해
        // 하위 항목들만 깔끔하게 지웁니다.
        formItemRepository.deleteByProgram_ProgramId(programId);

        // 3. 🌟 세션(영속성 컨텍스트)을 강제로 비우거나,
        // 혹은 단순히 새 질문들만 저장하여 Program 엔티티의 상태 변화가 영향을 주지 않게 합니다.
        if (fields != null && !fields.isEmpty()) {
            List<ProgramFormItem> items = fields.stream().map(f -> ProgramFormItem.builder()
                    .program(program)
                    .label(f.getLabel())
                    .type(ProgramFormType.valueOf(f.getType()))
                    .required(f.isRequired())
                    .options(f.getOptions())
                    .build()).collect(Collectors.toList());
            formItemRepository.saveAll(items);
        }

        // 🌟 메서드 종료 시 영속성 컨텍스트가 flush 되는데,
        // 이때 program 엔티티가 변경되었다고 판단되지 않도록 주의해야 합니다.
    }

    private void saveFormItems(Program program, List<ProgramRequestDto.FormItemRequest> fields) {
        if (fields != null) {
            List<ProgramFormItem> items = fields.stream().map(f -> ProgramFormItem.builder()
                    .program(program).label(f.getLabel()).type(ProgramFormType.valueOf(f.getType()))
                    .required(f.isRequired()).options(f.getOptions()).build()).collect(Collectors.toList());
            formItemRepository.saveAll(items);
        }
    }

    /**
     * 신청폼 항목 조회
     */
    public List<ProgramResponseDto.FormItemResponse> getFormItems(Long programId) {
        return formItemRepository.findByProgram_ProgramId(programId).stream()
                .map(i -> new ProgramResponseDto.FormItemResponse(i.getId(), i.getLabel(), i.getType().name(), i.isRequired(), i.getOptions()))
                .collect(Collectors.toList());
    }

    /**
     * 관리자용: 본인이 등록한 프로그램 목록 조회
     */
    public Page<ProgramResponseDto.ListResponse> getProgramListByAdmin(Long adminId, Pageable pageable) {
        if (!adminRepository.existsById(adminId)) {
            throw new IllegalArgumentException("관리자 정보를 찾을 수 없습니다. ID: " + adminId);
        }

        Page<Program> programsPage = programRepository.findByAdmin_AdminId(adminId, pageable);

        return programsPage.map(program -> {
            ProgramResponseDto.ListResponse dto = new ProgramResponseDto.ListResponse(program);
            int likeCount = (int) programLikeRepository.countByProgram(program);
            dto.setLikeInfo(likeCount, false);
            return dto;
        });
    }

    /**
     * 프로그램 상세 조회
     */
    public ProgramResponseDto.DetailResponse getProgramDetail(Long id, Long userId) {
        Program p = programRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("없음"));
        boolean applied = (userId != null) && applicationRepository.existsByUserAndProgram(userRepository.findById(userId).orElse(null), p);
        return new ProgramResponseDto.DetailResponse(p, programLikeRepository.countByProgram(p), false, applied);
    }

    /**
     * 프로그램 전체 목록 조회
     */
    public Page<ProgramResponseDto.ListResponse> getProgramList(Pageable pageable, Long userId, String dongName) {
        Page<Program> page = (dongName != null) ? programRepository.findByEduPlaceContaining(dongName, pageable) : programRepository.findAll(pageable);
        return page.map(ProgramResponseDto.ListResponse::new);
    }

    @Transactional
    public void deleteProgram(Long id, Long adminId) {
        Program p = programRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("없음"));
        if (!p.getAdmin().getAdminId().equals(adminId)) throw new IllegalArgumentException("권한 없음");
        programRepository.delete(p);
    }

    public boolean checkProgramName(String name) { return programRepository.existsByProgramName(name); }

    public List<String> extractProofFileUrls(Program p) {
        return p.getAttachedFiles().stream().map(ProgramFile::getFileUrl).collect(Collectors.toList());
    }

    public FileDownloadInfo downloadFile(String url, String name) {
        return new FileDownloadInfo(null, name, "application/octet-stream");
    }

    public static class FileDownloadInfo {
        private final Resource resource; private final String originalFileName; private final String contentType;
        public FileDownloadInfo(Resource r, String n, String t) { this.resource = r; this.originalFileName = n; this.contentType = t; }
        public Resource getResource() { return resource; }
        public String getOriginalFileName() { return originalFileName; }
        public String getContentType() { return contentType; }
    }
}