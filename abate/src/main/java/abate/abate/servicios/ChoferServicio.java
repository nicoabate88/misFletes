package abate.abate.servicios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.CombustibleRepositorio;
import abate.abate.repositorios.TransaccionRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.ChoferComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChoferServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private CuentaServicio cuentaServicio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;
    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private CombustibleRepositorio combustibleRepositorio;

    @Transactional
    public void crearChofer(String nombre, Long cuil, Long idCamion, String nombreUsuario, Double porcentaje, String password, String password2) throws MiException {

        validarDatos(nombre, nombreUsuario, cuil, password, password2);
        String nombreM = nombre.toUpperCase();
        
        Usuario user = new Usuario();
        
        if(idCamion != null){
        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }
        user.setCamion(camion);
        }

        user.setNombre(nombreM);
        user.setCuil(cuil);
        user.setUsuario(nombreUsuario);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setRol("CHOFER");
        user.setPorcentaje(porcentaje);

        usuarioRepositorio.save(user);

        cuentaServicio.crearCuentaChofer(buscarUltimo());

    }

    @Transactional
    public void modificarChofer(Long id, String nombre, Long cuil, Long  idCamion, String nombreUsuario, Double porcentaje) {

        String nombreM = nombre.toUpperCase();

        Usuario user = new Usuario();
        Optional<Usuario> u = usuarioRepositorio.findById(id);
        if (u.isPresent()) {
            user = u.get();
        }
        
        if(idCamion != 0){
        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }
        user.setCamion(camion);
        } else {
            user.setCamion(null);
        }

        user.setNombre(nombreM);
        user.setCuil(cuil);
        user.setUsuario(nombreUsuario);
        user.setPorcentaje(porcentaje);

        usuarioRepositorio.save(user);

    }

    @Transactional
    public void eliminarChofer(Long id) throws MiException {

        Usuario chofer = usuarioRepositorio.getById(id);
        
        Transaccion transaccion = transaccionRepositorio.findTopByChoferOrderByIdDesc(chofer);
        Combustible combustible = combustibleRepositorio.findTopByUsuarioOrderByIdDesc(chofer);
        
        if (transaccion == null && combustible == null) {
      
            usuarioRepositorio.deleteById(id);
            cuentaServicio.eliminarCuentaChofer(id);

        } else {

            throw new MiException("El Chofer no puede ser eliminado, tiene Flete, Entrega y/o Combustible asociado");
        }

    }

    public Usuario buscarChofer(Long id) {

        return usuarioRepositorio.getById(id);

    }

    public ArrayList<Usuario> bucarChoferesNombreAsc() {

        ArrayList<Usuario> lista = usuarioRepositorio.buscarUsuariosChofer();

        Collections.sort(lista, ChoferComparador.ordenarNombreAsc);

        return lista;
    }

    public Long buscarUltimo() {

        return usuarioRepositorio.ultimoUsuario();
    }

    public void validarDatos(String nombre, String nombreUsuario, Long cuil, String password, String password2) throws MiException {

        ArrayList<Usuario> lista = (ArrayList<Usuario>) usuarioRepositorio.findAll();

        for (Usuario u : lista) {
            if (u.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El nombre de Chofer ya se encuentra registrado");
            }
            if (u.getUsuario().equalsIgnoreCase(nombreUsuario)) {
                throw new MiException("El nombre de Usuario ya se encuentra registrado");
            }
            if (u.getCuil() == cuil) {
                throw new MiException("El CUIL de Usuario ya se encuentra registrado");
            }
        }
        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }

    }

}
