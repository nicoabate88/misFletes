package abate.abate.repositorios;

import abate.abate.entidades.Entrega;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntregaRepositorio extends JpaRepository<Entrega, Long> {

    @Query("SELECT MAX(id) FROM Entrega")
    public Long ultimoEntrega();

    @Query("SELECT e FROM Entrega e WHERE chofer_id = :id AND e.observacion != 'ELIMINADO'")
    public ArrayList<Entrega> buscarEntregasIdChofer(@Param("id") Long id);

    @Query("SELECT e FROM Entrega e WHERE e.observacion != 'ELIMINADO'")
    public ArrayList<Entrega> buscarEntregas();

}
