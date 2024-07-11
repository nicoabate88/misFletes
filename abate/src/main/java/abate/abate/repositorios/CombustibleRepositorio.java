
package abate.abate.repositorios;

import abate.abate.entidades.Combustible;
import abate.abate.entidades.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CombustibleRepositorio extends JpaRepository<Combustible, Long> {
    
    @Query("SELECT MAX(id) FROM Combustible")
    public Long ultimaCarga();       //devuelve id de ultimo combustible registrado
    
    @Query(value = "SELECT * FROM Combustible c WHERE usuario_id = :id ORDER BY c.id ASC LIMIT 1", nativeQuery = true)
    Optional<Combustible> findFirstByUsuarioOrderByIdAsc(@Param("id") Long id);  //devuelve un registro de un chofer en particular para verificar si existe km iniciales
    
    Combustible findTopByUsuarioOrderByIdDesc(Usuario chofer); //devuelve ultimo Combustible registrado de chofer especifico
    
    ArrayList<Combustible> findAllByOrderByIdDesc();  //devuelve la lista de combustibles de forma descendente
    
    ArrayList<Combustible> findAllByUsuarioOrderByIdDesc(Usuario chofer);  // Método para obtener el último registro
    
    @Query("SELECT c FROM Combustible c WHERE usuario_id = :id ORDER BY c.id DESC")  // Método para obtener el anteúltimo registro
    ArrayList<Combustible> findTop2ByUsuarioOrderByIdDesc(@Param("id") Long id);
                           
     ArrayList<Combustible> findByFechaCargaBetweenAndUsuario(Date desde, Date hasta, Usuario chofer);
    
}
