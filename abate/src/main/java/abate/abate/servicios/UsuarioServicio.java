package abate.abate.servicios;

import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public void crearUsuario(String nombre, String nombreUsuario, String password, String password2) throws MiException {

        validarDatos(nombre, nombreUsuario, password, password2);
        String nombreM = nombre.toUpperCase();

        Usuario user = new Usuario();

        user.setNombre(nombreM);
        user.setUsuario(nombreUsuario);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setRol("ADMIN");

        usuarioRepositorio.save(user);

    }

    public Usuario buscarUsuario(Long idUsuario) {

        return usuarioRepositorio.getById(idUsuario);
    }

    public ArrayList<Usuario> buscarUsuarios() {

        ArrayList<Usuario> lista = usuarioRepositorio.buscarUsuariosAdmin();

        return lista;

    }

    public Long buscarUltimoUsuario() {

        return usuarioRepositorio.ultimoUsuario();
    }

    public void validarDatos(String nombre, String nombreUsuario, String password, String password2) throws MiException {

        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }

        ArrayList<Usuario> lista = new ArrayList();

        lista = buscarUsuarios();

        for (Usuario u : lista) {

            if (u.getUsuario().equals(nombreUsuario)) {
                throw new MiException("El nombre de Usuario no está disponible, por favor ingrese otro");
            }
            if (u.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El Usuario ya se encuentra registrado");
            }
        }

    }

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarUsuarioPorUsuario(nombreUsuario);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());

            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", usuario);

            return new User(usuario.getUsuario(), usuario.getPassword(), permisos);

        } else {

            return null;

        }

    }

}
