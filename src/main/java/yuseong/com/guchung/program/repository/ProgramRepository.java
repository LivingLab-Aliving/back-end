package yuseong.com.guchung.program.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import yuseong.com.guchung.program.model.Program;
import yuseong.com.guchung.program.model.type.RegionRestriction;

import java.util.Collection;

public interface ProgramRepository extends JpaRepository<Program, Long> {

//    List<Program> findByEduPlaceContaining(String dongName);

    boolean existsByProgramName(String programName);

    Page<Program> findByAdmin_AdminId(Long adminId, Pageable pageable);

    Page<Program> findByRegionRestrictionIn(Collection<RegionRestriction> regions, Pageable pageable);
}