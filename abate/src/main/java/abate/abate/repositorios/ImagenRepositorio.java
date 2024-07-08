package abate.abate.repositorios;

import abate.abate.entidades.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagenRepositorio extends JpaRepository<Imagen, Long> {

    @Query("SELECT MAX(id) FROM Imagen")
    public Long ultimoImagen();

}
