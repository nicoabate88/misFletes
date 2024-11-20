
package abate.abate.repositorios;

import abate.abate.entidades.Ingreso;
import abate.abate.entidades.Usuario;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IngresoRepositorio extends JpaRepository<Ingreso, Long> {
    
    @Query("SELECT MAX(id) FROM Ingreso i WHERE i.idOrg = :id")
    public Long ultimoIngreso(@Param("id") Long id);
    
    Optional<Ingreso> findTopByIdOrgAndObservacionNotOrderByIdDesc(Long idOrg, String estado);
    
    @Query("SELECT i FROM Ingreso i WHERE chofer_id = :id AND i.observacion != 'ELIMINADO'")
    public ArrayList<Ingreso> buscarIngresosIdChofer(@Param("id") Long id);
    
    Ingreso findTopByChoferAndObservacionNotOrderByIdDesc(Usuario chofer, String estado);
    
}
