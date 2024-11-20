package abate.abate.servicios;

import abate.abate.entidades.Combustible;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Imagen;
import abate.abate.repositorios.CombustibleRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.GastoRepositorio;
import abate.abate.repositorios.ImagenRepositorio;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImagenServicio {

    @Autowired
    private ImagenRepositorio imagenRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private GastoRepositorio gastoRepositorio;
    @Autowired
    private CombustibleRepositorio combustibleRepositorio;

    @Transactional
    public void crearImagenGasto(Long id, Imagen imagen) {

        Flete flete = fleteRepositorio.getById(id);
        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(flete.getGasto().getId());
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        imagenRepositorio.save(imagen);

        Long idImg = buscarUltimo();

        Imagen img = imagenRepositorio.getById(idImg);

        gasto.setImagen(img);
        gastoRepositorio.save(gasto);

    }
    
    @Transactional
    public void crearImagenGastoCaja(Long id, Imagen imagen) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(id);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        imagenRepositorio.save(imagen);

        Long idImg = buscarUltimo();

        Imagen img = imagenRepositorio.getById(idImg);

        gasto.setImagen(img);
        gastoRepositorio.save(gasto);

    }

    @Transactional
    public void crearImagenCP(Long id, Imagen imagen) {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(id);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        imagenRepositorio.save(imagen);
        Long idImg = buscarUltimo();

        Imagen img = imagenRepositorio.getById(idImg);

        flete.setImagenCP(img);

        fleteRepositorio.save(flete);

    }

    @Transactional
    public void crearImagenDescarga(Long id, Imagen imagen) {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(id);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        imagenRepositorio.save(imagen);
        Long idImg = buscarUltimo();

        Imagen img = imagenRepositorio.getById(idImg);

        flete.setImagenDescarga(img);

        fleteRepositorio.save(flete);

    }
    
    @Transactional
    public void crearImagenCombustible(Long id, Imagen imagen) {

        Combustible carga = new Combustible();
        Optional<Combustible> cga = combustibleRepositorio.findById(id);
        if (cga.isPresent()) {
            carga = cga.get();
        }

        imagenRepositorio.save(imagen);
        Long idImg = buscarUltimo();

        Imagen img = imagenRepositorio.getById(idImg);

        carga.setImagen(img);

        combustibleRepositorio.save(carga);

    }

    @Transactional
    public void modificarImagen(Long id, Imagen imagen) {

        Imagen img = new Imagen();
        Optional<Imagen> im = imagenRepositorio.findById(id);
        if (im.isPresent()) {
            img = im.get();
        }

        img.setTipo(imagen.getTipo());
        img.setDatos(imagen.getDatos());
        img.setNombre(imagen.getNombre());

        imagenRepositorio.save(imagen);

    }
    
    @Transactional
    public void eliminarImagenCombustible(Long id){
        
        Combustible carga = combustibleRepositorio.buscarCombustibleIdImagen(id);
        
        carga.setImagen(null);
        
        combustibleRepositorio.save(carga);
        
        imagenRepositorio.deleteById(id);
        
    }
    
    @Transactional
    public void eliminarImagenCP(Long id){
        
        Flete flete = fleteRepositorio.buscarFleteIdImagenCP(id);
        
        flete.setImagenCP(null);
        
        fleteRepositorio.save(flete);
        
        imagenRepositorio.deleteById(id);
        
    }
    
    @Transactional
    public void eliminarImagenDescarga(Long id){
        
        Flete flete = fleteRepositorio.buscarFleteIdImagenDescarga(id);
        
        flete.setImagenDescarga(null);
        
        fleteRepositorio.save(flete);
        
        imagenRepositorio.deleteById(id);
        
    }
    
    @Transactional
    public void eliminarImagenGasto(Long id){
        
        Gasto gasto = gastoRepositorio.buscarGastoIdImagen(id);
        
        gasto.setImagen(null);
        
        gastoRepositorio.save(gasto);
        
        imagenRepositorio.deleteById(id);
        
    }

    public Imagen obtenerImagenPorId(Long id) {
        return imagenRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
    }

    public Long buscarUltimo() {

        return imagenRepositorio.ultimoImagen();
    }

}
