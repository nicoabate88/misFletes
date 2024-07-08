package abate.abate.servicios;

import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
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

    @Transactional
    public void crearChofer(String nombre, String cuil, String dominio, String nombreUsuario, Double porcentaje, String password, String password2) throws MiException {

        validarDatos(nombre, nombreUsuario, password, password2);
        String nombreM = nombre.toUpperCase();
        String dominioM = dominio.toUpperCase();

        Usuario user = new Usuario();

        user.setNombre(nombreM);
        user.setCuil(cuil);
        user.setDominio(dominioM);
        user.setUsuario(nombreUsuario);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setRol("CHOFER");
        user.setPorcentaje(porcentaje);

        usuarioRepositorio.save(user);

        cuentaServicio.crearCuentaChofer(buscarUltimo());

    }

    @Transactional
    public void modificarChofer(Long id, String nombre, String cuil, String dominio, String nombreUsuario, Double porcentaje) {

        String nombreM = nombre.toUpperCase();
        String dominioM = dominio.toUpperCase();

        Usuario user = new Usuario();
        Optional<Usuario> u = usuarioRepositorio.findById(id);
        if (u.isPresent()) {
            user = u.get();
        }

        user.setNombre(nombreM);
        user.setCuil(cuil);
        user.setDominio(dominioM);
        user.setUsuario(nombreUsuario);
        user.setPorcentaje(porcentaje);

        usuarioRepositorio.save(user);

    }

    @Transactional
    public void eliminarChofer(Long id) throws MiException {

        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionIdChofer(id);
        if (lista.isEmpty()) {

            usuarioRepositorio.deleteById(id);
            cuentaServicio.eliminarCuentaChofer(id);

        } else {

            throw new MiException("El Chofer no puede ser eliminado, tiene Flete o Entrega asociado");
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

    public void validarDatos(String nombre, String nombreUsuario, String password, String password2) throws MiException {

        ArrayList<Usuario> lista = (ArrayList<Usuario>) usuarioRepositorio.findAll();

        for (Usuario u : lista) {
            if (u.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El nombre de Chofer ya se encuentra registrado");
            }
            if (u.getUsuario().equalsIgnoreCase(nombreUsuario)) {
                throw new MiException("El nombre de Usuario ya se encuentra registrado");
            }
        }
        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }

    }

}
