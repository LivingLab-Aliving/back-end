package yuseong.com.guchung.program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuseong.com.guchung.program.model.ProgramFormItem;

@Repository
public interface ProgramFormItemRepository extends JpaRepository<ProgramFormItem, Long> {
}