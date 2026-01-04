package yuseong.com.guchung.program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuseong.com.guchung.program.model.ProgramFormItem;
import java.util.List;

public interface ProgramFormItemRepository extends JpaRepository<ProgramFormItem, Long> {
    List<ProgramFormItem> findByProgram_ProgramId(Long programId);
    void deleteByProgram_ProgramId(Long programId);
}