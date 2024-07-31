package abate.abate.repositorios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Usuario;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.usuario = :usuario")
    public Usuario buscarUsuarioPorUsuario(@Param("usuario") String usuario);

    @Query("SELECT MAX(id) FROM Usuario")
    public Long ultimoUsuario();

    @Query("SELECT u FROM Usuario u WHERE u.rol = 'CHOFER'")
    public ArrayList<Usuario> buscarUsuariosChofer();

    @Query("SELECT u FROM Usuario u WHERE u.rol = 'ADMIN'")
    public ArrayList<Usuario> buscarUsuariosAdmin();
    
    Usuario findTopByCamionOrderByIdDesc(Camion camion);

}
