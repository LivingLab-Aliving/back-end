package yuseong.com.guchung.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuseong.com.guchung.auth.model.Instructor; // Instructor 엔티티 경로

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
}