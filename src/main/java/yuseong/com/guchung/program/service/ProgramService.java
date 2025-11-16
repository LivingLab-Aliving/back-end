package yuseong.com.guchung.program.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuseong.com.guchung.admin.model.Admin;
import yuseong.com.guchung.auth.model.Instructor;
import yuseong.com.guchung.program.model.Program;
import yuseong.com.guchung.auth.model.User;
import yuseong.com.guchung.program.dto.ProgramRequestDto;
import yuseong.com.guchung.program.dto.ProgramResponseDto;
import yuseong.com.guchung.admin.repository.AdminRepository;
import yuseong.com.guchung.auth.repository.InstructorRepository;
import yuseong.com.guchung.program.repository.ProgramRepository;
import yuseong.com.guchung.auth.repository.UserRepository;
import yuseong.com.guchung.program.model.type.RegionRestriction;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramService {

    private final ProgramRepository programRepository;
    private final AdminRepository adminRepository;
    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;

    // 프로그램 생성
    @Transactional
    public Long createProgram(ProgramRequestDto.Create requestDto, Long adminId) {

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

        Program program = Program.builder()
                .programName(requestDto.getProgramName())
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
                .classPlanUrl(requestDto.getClassPlanUrl())
                .institution(requestDto.getInstitution())
                .regionRestriction(requestDto.getRegionRestriction())
                .admin(admin)
                .instructor(instructor)
                .build();

        Program savedProgram = programRepository.save(program);
        return savedProgram.getProgramId();
    }

    public boolean checkProgramName(String programName) {
        return programRepository.existsByProgramName(programName);
    }

    @Transactional
    public Long updateProgram(Long programId, ProgramRequestDto.Update requestDto, Long adminId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 프로그램을 찾을 수 없습니다. ID: " + programId));

        if (!program.getAdmin().getAdminId().equals(adminId)) {
            throw new IllegalArgumentException("프로그램을 수정할 권한이 없습니다.");
        }

        Instructor instructor = null;
        if (requestDto.getInstructorId() != null) {
            instructor = instructorRepository.findById(requestDto.getInstructorId())
                    .orElse(null);
        }

        program.update(requestDto, instructor);

        return program.getProgramId();
    }

    public Page<ProgramResponseDto.ListResponse> getProgramList(Pageable pageable, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다. ID: " + userId));

        String userAddress = user.getAddress();

        List<RegionRestriction> allowedRegions = new ArrayList<>();
        allowedRegions.add(RegionRestriction.NONE);

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
            // 향후 "대전광역시" 전체 등 더 넓은 범위의 로직 추가 해야됨
        }

        Page<Program> programsPage = programRepository.findByRegionRestrictionIn(allowedRegions, pageable);

        return programsPage.map(ProgramResponseDto.ListResponse::new);
    }

    public ProgramResponseDto.DetailResponse getProgramDetail(Long programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로그램을 찾을 수 없습니다. ID: " + programId));

        return new ProgramResponseDto.DetailResponse(program);
    }

    public Page<ProgramResponseDto.ListResponse> getProgramListByAdmin(Long adminId, Pageable pageable) {
        if (!adminRepository.existsById(adminId)) {
            throw new IllegalArgumentException("관리자 정보를 찾을 수 없습니다. ID: " + adminId);
        }

        Page<Program> programsPage = programRepository.findByAdmin_AdminId(adminId, pageable);
        return programsPage.map(ProgramResponseDto.ListResponse::new);
    }
}