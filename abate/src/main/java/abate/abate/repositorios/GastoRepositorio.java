package abate.abate.repositorios;

import abate.abate.entidades.Gasto;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GastoRepositorio extends JpaRepository<Gasto, Long> {

    @Query("SELECT g FROM Gasto g WHERE chofer_id = :id")
    public ArrayList<Gasto> buscarGastoIdChofer(@Param("id") Long id);

    @Query("SELECT g FROM Gasto g WHERE imagen_id = :id")
    public Gasto buscarGastoIdImagen(@Param("id") Long id);

    @Query("SELECT MAX(id) FROM Gasto")
    public Long ultimoGasto();

}
