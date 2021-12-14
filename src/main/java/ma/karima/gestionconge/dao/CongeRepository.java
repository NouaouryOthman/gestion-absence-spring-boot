package ma.karima.gestionconge.dao;

import ma.karima.gestionconge.entity.Conge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CongeRepository extends JpaRepository<Conge, Long> {
}
