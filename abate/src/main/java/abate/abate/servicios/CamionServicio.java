
package abate.abate.servicios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.CombustibleRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.CamionComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CamionServicio {
    
    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private CombustibleRepositorio combustibleRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    
    @Transactional
    public void crearCamion(Long idOrg, String marca, String modelo, String dominio) throws MiException {

        validarDatos(idOrg, dominio);
        
        Camion camion = new Camion();

        String marcaMayusculas = marca.toUpperCase();
        String modeloMayusculas = modelo.toUpperCase();
        String dominioMayusculas = dominio.toUpperCase();

        camion.setIdOrg(idOrg);
        camion.setMarca(marcaMayusculas);
        camion.setModelo(modeloMayusculas);
        camion.setDominio(dominioMayusculas);

        camionRepositorio.save(camion);

    }
    
    @Transactional
    public void modificarCamion(Long id, String marca, String modelo, String dominio) throws MiException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(id);
        if (cam.isPresent()) {
            camion = cam.get();
        }
        validarDatosModificar(camion, dominio);
        
        String marcaMayusculas = marca.toUpperCase();
        String modeloMayusculas = modelo.toUpperCase();
        String dominioMayusculas = dominio.toUpperCase();

        camion.setMarca(marcaMayusculas);
        camion.setModelo(modeloMayusculas);
        camion.setDominio(dominioMayusculas);

        camionRepositorio.save(camion);

    }
    
    @Transactional
    public void eliminarCamion(Long id) throws MiException {
        
        Camion camion = camionRepositorio.getById(id);
        
        Combustible combustible = combustibleRepositorio.findTopByCamionOrderByIdDesc(camion);
        Flete flete = fleteRepositorio.findTopByCamionOrderByIdDesc(camion);
        Usuario usuario = usuarioRepositorio.findTopByCamionOrderByIdDesc(camion);

        if (combustible == null && flete == null && usuario == null) {

            camionRepositorio.deleteById(id);

        } else {

            throw new MiException("El Camión no puede ser eliminado, tiene Flete, Combustible y/o Chofer asociado");
        }

    }
    
     public ArrayList<Camion> buscarCamionesAsc(Long idOrg) {

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(idOrg);

        Collections.sort(lista, CamionComparador.ordenarDominioAsc); 

        return lista;

    }
     
    public Camion buscarCamion(Long id) {

        return camionRepositorio.getById(id);
    } 
     
    public Long buscarUltimo() {

        return camionRepositorio.ultimoCamion();

    } 
     
    public void validarDatos(Long idOrg, String dominio) throws MiException {

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(idOrg);

        for (Camion c : lista) {
            if (c.getDominio().equalsIgnoreCase(dominio)) {
                throw new MiException("El DOMINIO de Camión ya está registrado");
            }
        }
    }    
    
    
    public void validarDatosModificar(Camion camion, String dominio) throws MiException {

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(camion.getIdOrg());

        if(!camion.getDominio().equalsIgnoreCase(dominio)){
        for (Camion c : lista) {
            if (c.getDominio().equalsIgnoreCase(dominio)) {
                throw new MiException("El DOMINIO de Camión ya está registrado");
            }
        }
    } 
    }
    

 
}
