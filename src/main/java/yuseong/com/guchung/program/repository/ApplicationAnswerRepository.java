package yuseong.com.guchung.program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuseong.com.guchung.program.model.Application;
import yuseong.com.guchung.program.model.ApplicationAnswer;

import java.util.List;

@Repository
public interface ApplicationAnswerRepository extends JpaRepository<ApplicationAnswer, Long> {
    List<ApplicationAnswer> findByApplication(Application application);
}