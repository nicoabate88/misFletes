package abate.abate.repositorios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Cliente;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FleteRepositorio extends JpaRepository<Flete, Long> {

    @Query("SELECT MAX(id) FROM Flete f WHERE f.idOrg = :id")
    public Long ultimoFlete(@Param("id") Long id);
    
    Optional<Flete> findTopByIdOrgAndEstadoNotOrderByIdDesc(Long idOrg, String estado);

    @Query("SELECT f FROM Flete f WHERE chofer_id = :id AND f.estado != 'ELIMINADO'")
    public ArrayList<Flete> buscarFletesIdChofer(@Param("id") Long id);

    @Query("SELECT f FROM Flete f WHERE cliente_id = :id AND f.estado != 'ELIMINADO'")
    public ArrayList<Flete> buscarFletesIdCliente(@Param("id") Long id);

    @Query("SELECT f FROM Flete f WHERE f.estado != 'ELIMINADO'")
    public ArrayList<Flete> buscarFletes();

    @Query("SELECT f FROM Flete f WHERE f.estado = 'PENDIENTE' AND f.idOrg = :id")
    public ArrayList<Flete> buscarFletePendiente(@Param("id") Long id);

    @Query("SELECT f FROM Flete f WHERE imagenCP_id = :id")
    public Flete buscarFleteIdImagenCP(@Param("id") Long id);

    @Query("SELECT f FROM Flete f WHERE imagen_descarga_id = :id")
    public Flete buscarFleteIdImagenDescarga(@Param("id") Long id);

    @Query("SELECT f FROM Flete f WHERE gasto_id = :id")
    public Flete buscarFleteIdGasto(@Param("id") Long id);

    ArrayList<Flete> findByFechaFleteBetweenAndEstadoNotAndIdOrg(Date desde, Date hasta, String estado, Long idOrg);  

    ArrayList<Flete> findByFechaFleteBetweenAndChoferAndEstadoNot(Date desde, Date hasta, Usuario chofer, String estado);
    
    ArrayList<Flete> findByFechaFleteBetweenAndCamionAndEstadoNot(Date desde, Date hasta, Camion camion, String estado);
                 
    ArrayList<Flete> findByFechaFleteBetweenAndClienteAndEstadoNot(Date desde, Date hasta, Cliente cliente, String estado);
    
    Flete findTopByCamionAndEstadoNotOrderByIdDesc(Camion camion, String estado);
    
    Flete findTopByChoferAndEstadoNotOrderByIdDesc(Usuario chofer, String estado);

}
