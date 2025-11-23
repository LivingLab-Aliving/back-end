package yuseong.com.guchung.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuseong.com.guchung.admin.model.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}