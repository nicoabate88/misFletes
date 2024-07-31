
package abate.abate.repositorios;

import abate.abate.entidades.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CamionRepositorio extends JpaRepository<Camion, Long> {
    
    @Query("SELECT MAX(id) FROM Camion")
    public Long ultimoCamion();
    
}
