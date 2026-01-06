package yuseong.com.guchung.program.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import yuseong.com.guchung.program.model.Program;
import yuseong.com.guchung.program.model.type.ProgramType;
import yuseong.com.guchung.program.model.type.RegionRestriction;

import java.util.Collection;

public interface ProgramRepository extends JpaRepository<Program, Long> {

    boolean existsByProgramName(String programName);

    Page<Program> findByAdmin_AdminId(Long adminId, Pageable pageable);

    Page<Program> findByRegionRestrictionIn(Collection<RegionRestriction> regions, Pageable pageable);

    Page<Program> findByProgramTypeAndRegionRestrictionIn(ProgramType programType, Collection<RegionRestriction> regions, Pageable pageable);

    Page<Program> findByEduPlaceContainingAndRegionRestrictionIn(
            String dongName, Collection<RegionRestriction> regions, Pageable pageable);

    Page<Program> findByProgramTypeAndEduPlaceContainingAndRegionRestrictionIn(
            ProgramType programType, String dongName, Collection<RegionRestriction> regions, Pageable pageable);

    Page<Program> findByEduPlaceContaining(String eduPlace, Pageable pageable);

    Page<Program> findByDongName(String dongName, Pageable pageable);

    Page<Program> findByProgramType(ProgramType programType, Pageable pageable);

    Page<Program> findByProgramTypeAndDongName(ProgramType programType, String dongName, Pageable pageable);
}