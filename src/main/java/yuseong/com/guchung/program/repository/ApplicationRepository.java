package yuseong.com.guchung.program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuseong.com.guchung.auth.model.User;
import yuseong.com.guchung.program.model.Application;
import yuseong.com.guchung.program.model.Program;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    int countByProgram_ProgramId(Long programId);
    boolean existsByUserAndProgram(User user, Program program);
    List<Application> findByUser(User user);
}