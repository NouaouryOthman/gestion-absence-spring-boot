package ma.karima.gestionconge.dao;

import ma.karima.gestionconge.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
}
