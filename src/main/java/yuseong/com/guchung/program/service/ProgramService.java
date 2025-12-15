package yuseong.com.guchung.program.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import yuseong.com.guchung.program.model.Program;
import yuseong.com.guchung.program.model.ProgramFile;
import yuseong.com.guchung.program.model.ProgramLike;
import yuseong.com.guchung.program.model.type.ProgramType;
import yuseong.com.guchung.program.model.type.RegionRestriction;
import yuseong.com.guchung.program.repository.ProgramFileRepository;
import yuseong.com.guchung.program.repository.ProgramLikeRepository;
import yuseong.com.guchung.program.repository.ProgramRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final ProgramLikeRepository programLikeRepository;
    private final ProgramFileRepository programFileRepository;

    @Transactional
    public Program createProgram(ProgramRequestDto.Create requestDto,
                                 MultipartFile classPlanFile,
                                 List<MultipartFile> proofFiles,
                                 Long adminId) throws IOException {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다."));

        Instructor instructor = null;
        if (requestDto.getInstructorId() != null) {
            instructor = instructorRepository.findById(requestDto.getInstructorId())
                    .orElse(null);
        }

        if (checkProgramName(requestDto.getProgramName())) {
            throw new IllegalArgumentException("이미 존재하는 프로그램명입니다.");
        }

        String classPlanUrl = requestDto.getClassPlanUrl();
        String classPlanOriginalName = null;

        if (classPlanFile != null && !classPlanFile.isEmpty()) {
            try {
                classPlanUrl = s3Uploader.uploadFile(classPlanFile, "program/classplan");
                classPlanOriginalName = classPlanFile.getOriginalFilename();
            } catch (IOException e) {
                log.error("S3 강의계획서 파일 업로드 실패", e);
                throw new RuntimeException("S3 강의계획서 파일 업로드에 실패했습니다.", e);
            }
        }

        Program program = Program.builder()
                .programName(requestDto.getProgramName())
                .programType(requestDto.getProgramType())
                .eduTime(requestDto.getEduTime())
                .quarter(requestDto.getQuarter())
                .eduStartDate(requestDto.getEduStartDate())
                .eduEndDate(requestDto.getEduEndDate())
                .recruitStartDate(requestDto.getRecruitStartDate())
                .recruitEndDate(requestDto.getRecruitEndDate())
                .eduPlace(requestDto.getEduPlace())
                .capacity(requestDto.getCapacity())
                .targetAudience(requestDto.getTargetAudience())
                .eduPrice(requestDto.getEduPrice())
                .needs(requestDto.getNeeds())
                .description(requestDto.getDescription())
                .info(requestDto.getInfo())
                .etc(requestDto.getEtc())
                .classPlanUrl(classPlanUrl)
                .classPlanOriginalName(classPlanOriginalName)
                .institution(requestDto.getInstitution())
                .regionRestriction(requestDto.getRegionRestriction())
                .admin(admin)
                .instructor(instructor)
                .build();

        Program savedProgram = programRepository.save(program);

        if (proofFiles != null && !proofFiles.isEmpty()) {
            for (MultipartFile file : proofFiles) {
                if (!file.isEmpty()) {
                    try {
                        String fileUrl = s3Uploader.uploadFile(file, "program/proof");

                        ProgramFile programFile = ProgramFile.builder()
                                .originalName(file.getOriginalFilename())
                                .fileUrl(fileUrl)
                                .program(savedProgram)
                                .build();

                        programFileRepository.save(programFile);

                    } catch (IOException e) {
                        log.error("S3 증빙 파일 업로드 실패: {}", file.getOriginalFilename(), e);
                        throw new RuntimeException("S3 증빙 파일 업로드에 실패했습니다.", e);
                    }
                }
            }
        }

        return savedProgram;
    }

    public boolean checkProgramName(String programName) {
        return programRepository.existsByProgramName(programName);
    }

    @Transactional
    public Long updateProgram(Long programId, ProgramRequestDto.Update requestDto,
                              MultipartFile newClassPlanFile, Long adminId) {

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 프로그램을 찾을 수 없습니다. ID: " + programId));

        if (!program.getAdmin().getAdminId().equals(adminId)) {
            throw new IllegalArgumentException("프로그램을 수정할 권한이 없습니다.");
        }

        String updatedClassPlanUrl = requestDto.getClassPlanUrl();
        String updatedClassPlanOriginalName = program.getClassPlanOriginalName();

        if (newClassPlanFile != null && !newClassPlanFile.isEmpty()) {
            try {
                if (program.getClassPlanUrl() != null) {
                    s3Uploader.deleteFile(program.getClassPlanUrl());
                }

                updatedClassPlanUrl = s3Uploader.uploadFile(newClassPlanFile, "program/classplan");
                updatedClassPlanOriginalName = newClassPlanFile.getOriginalFilename();

            } catch (IOException e) {
                log.error("S3 강의계획서 파일 업데이트 실패", e);
                throw new RuntimeException("S3 파일 업데이트에 실패했습니다.", e);
            }
        } else if (updatedClassPlanUrl == null || updatedClassPlanUrl.isEmpty()) {
            if (program.getClassPlanUrl() != null) {
                log.info("기존 강의계획서 파일 삭제 요청: {}", program.getClassPlanUrl());
                s3Uploader.deleteFile(program.getClassPlanUrl());
            }
            updatedClassPlanOriginalName = null;
        }

        program.setClassPlanUrl(updatedClassPlanUrl);
        program.setClassPlanOriginalName(updatedClassPlanOriginalName);


        Instructor instructor = null;
        if (requestDto.getInstructorId() != null) {
            instructor = instructorRepository.findById(requestDto.getInstructorId())
                    .orElse(null);
        }

        program.update(requestDto, instructor);

        return program.getProgramId();
    }

    @Transactional
    public void deleteProofFile(Long programFileId, Long adminId) {
        ProgramFile file = programFileRepository.findById(programFileId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 파일 정보를 찾을 수 없습니다. ID: " + programFileId));

        if (!file.getProgram().getAdmin().getAdminId().equals(adminId)) {
            throw new IllegalArgumentException("파일을 삭제할 권한이 없습니다.");
        }

        s3Uploader.deleteFile(file.getFileUrl());

        programFileRepository.delete(file);
    }

    @Transactional
    public boolean toggleProgramLike(Long userId, Long programId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다. ID: " + userId));

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("프로그램을 찾을 수 없습니다. ID: " + programId));

        Optional<ProgramLike> existingLike = programLikeRepository.findByUserAndProgram(user, program);

        if (existingLike.isPresent()) {
            programLikeRepository.delete(existingLike.get());
            return false;
        } else {
            ProgramLike newLike = ProgramLike.builder()
                    .user(user)
                    .program(program)
                    .build();
            programLikeRepository.save(newLike);
            return true;
        }
    }

    private boolean isProgramLikedByUser(User user, Program program) {
        return programLikeRepository.existsByUserAndProgram(user, program);
    }

    private int getProgramLikeCount(Program program) {
        return programLikeRepository.countByProgram(program);
    }

    public Page<ProgramResponseDto.ListResponse> getProgramList(Pageable pageable, Long userId) {

        User user = null;
        List<RegionRestriction> allowedRegions = new ArrayList<>();
        allowedRegions.add(RegionRestriction.NONE);

        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElse(null);

            if (user != null) {
                String userAddress = user.getAddress();
                if (userAddress != null && !userAddress.isEmpty()) {
                    if (userAddress.contains("유성구")) {
                        allowedRegions.add(RegionRestriction.YUSEONG);
                    } else if (userAddress.contains("동구")) {
                        allowedRegions.add(RegionRestriction.DONGGU);
                    } else if (userAddress.contains("서구")) {
                        allowedRegions.add(RegionRestriction.SEOGU);
                    } else if (userAddress.contains("중구")) {
                        allowedRegions.add(RegionRestriction.JUNGGU);
                    } else if (userAddress.contains("대덕구")) {
                        allowedRegions.add(RegionRestriction.DAEDEOK);
                    }
                }
            }
        }

        Page<Program> programsPage = programRepository.findByRegionRestrictionIn(allowedRegions, pageable);

        final User finalUser = user;
        return programsPage.map(program -> {
            ProgramResponseDto.ListResponse dto = new ProgramResponseDto.ListResponse(program);

            int likeCount = getProgramLikeCount(program);
            boolean isLiked = false;

            if (finalUser != null) {
                isLiked = isProgramLikedByUser(finalUser, program);
            }

            dto.setLikeInfo(likeCount, isLiked);
            return dto;
        });
    }

    public Page<ProgramResponseDto.ListResponse> getProgramListByType(ProgramType programType, Pageable pageable, Long userId) {

        User user = null;
        List<RegionRestriction> allowedRegions = new ArrayList<>();
        allowedRegions.add(RegionRestriction.NONE);

        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElse(null);
        }

        if (user != null) {
            String userAddress = user.getAddress();
            if (userAddress != null && !userAddress.isEmpty()) {
                if (userAddress.contains("유성구")) {
                    allowedRegions.add(RegionRestriction.YUSEONG);
                } else if (userAddress.contains("동구")) {
                    allowedRegions.add(RegionRestriction.DONGGU);
                } else if (userAddress.contains("서구")) {
                    allowedRegions.add(RegionRestriction.SEOGU);
                } else if (userAddress.contains("중구")) {
                    allowedRegions.add(RegionRestriction.JUNGGU);
                } else if (userAddress.contains("대덕구")) {
                    allowedRegions.add(RegionRestriction.DAEDEOK);
                }
            }
        }

        Page<Program> programsPage = programRepository.findByProgramTypeAndRegionRestrictionIn(programType, allowedRegions, pageable);

        final User finalUser = user;
        return programsPage.map(program -> {
            ProgramResponseDto.ListResponse dto = new ProgramResponseDto.ListResponse(program);

            int likeCount = getProgramLikeCount(program);
            boolean isLiked = false;

            if (finalUser != null) {
                isLiked = isProgramLikedByUser(finalUser, program);
            }

            dto.setLikeInfo(likeCount, isLiked);
            return dto;
        });
    }

    public ProgramResponseDto.DetailResponse getProgramDetail(Long programId, Long userId) {

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로그램을 찾을 수 없습니다. ID: " + programId));

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElse(null);
        }

        int likeCount = getProgramLikeCount(program);
        boolean isLiked = false;

        if (user != null) {
            isLiked = isProgramLikedByUser(user, program);
        }

        return new ProgramResponseDto.DetailResponse(program, likeCount, isLiked);
    }

    public Page<ProgramResponseDto.ListResponse> getProgramListByAdmin(Long adminId, Pageable pageable) {
        if (!adminRepository.existsById(adminId)) {
            throw new IllegalArgumentException("관리자 정보를 찾을 수 없습니다. ID: " + adminId);
        }

        Page<Program> programsPage = programRepository.findByAdmin_AdminId(adminId, pageable);

        return programsPage.map(program -> {
            ProgramResponseDto.ListResponse dto = new ProgramResponseDto.ListResponse(program);

            int likeCount = getProgramLikeCount(program);

            dto.setLikeInfo(likeCount, false);
            return dto;
        });
    }
}