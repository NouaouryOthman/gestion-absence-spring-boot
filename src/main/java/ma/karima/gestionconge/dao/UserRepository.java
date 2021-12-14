package ma.karima.gestionconge.dao;

import ma.karima.gestionconge.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    Page<User> findByMatriculeContains(String matricule, Pageable pageable);
}
