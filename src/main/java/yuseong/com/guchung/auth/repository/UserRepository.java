package yuseong.com.guchung.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuseong.com.guchung.auth.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}