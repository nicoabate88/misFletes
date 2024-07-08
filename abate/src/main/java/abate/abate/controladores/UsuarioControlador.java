package abate.abate.controladores;

import abate.abate.excepciones.MiException;
import abate.abate.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuario")
//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/registrar")
    public String registrarUsuario() {

        return "usuario_registrar.html";
    }

    @PostMapping("/registro")
    public String registroUsuario(@RequestParam String nombre, @RequestParam String nombreUsuario,
            @RequestParam String password, @RequestParam String password2, ModelMap modelo) {

        try {

            usuarioServicio.crearUsuario(nombre, nombreUsuario, password, password2);

            Long id = usuarioServicio.buscarUltimoUsuario();

            modelo.put("usuario", usuarioServicio.buscarUsuario(id));
            modelo.put("exito", "Usuario REGISTRADO con éxito");

            return "usuario_mostrar.html";

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.put("nombreUsuario", nombreUsuario);
            modelo.put("error", ex.getMessage());

            return "usuario_registrar.html";
        }
    }

    @GetMapping("/listar")
    private String listar(ModelMap modelo) {

        modelo.addAttribute("usuarios", usuarioServicio.buscarUsuarios());

        return "usuario_listar.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));

        return "usuario_mostrar.html";
    }

    @GetMapping("/mostrarChofer/{id}")
    public String mostrarChofer(@PathVariable Long id, ModelMap modelo) {

        modelo.put("usuario", usuarioServicio.buscarUsuario(id));

        return "usuario_mostrarChofer.html";
    }

}
