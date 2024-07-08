package abate.abate.repositorios;

import abate.abate.entidades.Recibo;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReciboRepositorio extends JpaRepository<Recibo, Long> {

    @Query("SELECT MAX(id) FROM Recibo")
    public Long ultimoRecibo();

    @Query("SELECT r FROM Recibo r WHERE cliente_id = :id AND r.observacion != 'ELIMINADO'")
    public ArrayList<Recibo> buscarRecibosIdCliente(@Param("id") Long id);

    @Query("SELECT r FROM Recibo r WHERE r.observacion != 'ELIMINADO'")
    public ArrayList<Recibo> buscarRecibos();

}
