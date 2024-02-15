package madstodolist.repository;

import madstodolist.model.Equipo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EquipoRepository extends CrudRepository<Equipo, Long> {
    public List<Equipo> findAll();
}
