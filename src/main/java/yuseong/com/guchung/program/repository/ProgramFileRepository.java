package yuseong.com.guchung.program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuseong.com.guchung.program.model.ProgramFile;

@Repository
public interface ProgramFileRepository extends JpaRepository<ProgramFile, Long> {
}