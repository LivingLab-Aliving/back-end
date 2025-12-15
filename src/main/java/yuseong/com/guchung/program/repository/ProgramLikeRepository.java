package yuseong.com.guchung.program.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuseong.com.guchung.auth.model.User;
import yuseong.com.guchung.program.model.Program;
import yuseong.com.guchung.program.model.ProgramLike;

import java.util.Optional;
import java.util.List;

@Repository
public interface ProgramLikeRepository extends JpaRepository<ProgramLike, Long> {

    Optional<ProgramLike> findByUserAndProgram(User user, Program program);
    boolean existsByUserAndProgram(User user, Program program);
    int countByProgram(Program program);
    Page<ProgramLike> findByUser(User user, Pageable pageable);}
