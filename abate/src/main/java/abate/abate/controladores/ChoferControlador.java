package abate.abate.controladores;

import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.ChoferServicio;
import javax.servlet.http.HttpSession;
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
@RequestMapping("/chofer")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class ChoferControlador {

    @Autowired
    private ChoferServicio choferServicio;

    @GetMapping("/registrar")
    public String registrar() {

        return "chofer_registrar.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam String cuil, @RequestParam String dominio,
            @RequestParam Double porcentaje, @RequestParam String nombreUsuario, @RequestParam String password,
            @RequestParam String password2, ModelMap modelo) {

        try {

            choferServicio.crearChofer(nombre, cuil, dominio, nombreUsuario, porcentaje, password, password2);
            Long id = choferServicio.buscarUltimo();
            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.put("exito", "Chofer REGISTRADO con éxito");

            return "chofer_mostrar.html";

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.put("cuil", cuil);
            modelo.put("dominio", dominio);
            modelo.put("nombreUsuario", nombreUsuario);
            modelo.put("porcentaje", porcentaje);
            modelo.put("error", ex.getMessage());

            return "chofer_registrar.html";
        }
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc());

        return "chofer_listar.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));

        return "chofer_mostrar.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));

        return "chofer_modificar.html";

    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam String cuil, @RequestParam String dominio,
            @RequestParam Double porcentaje, @RequestParam String nombreUsuario, ModelMap modelo) {

        choferServicio.modificarChofer(id, nombre, cuil, dominio, nombreUsuario, porcentaje);

        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("exito", "Chofer MODIFICADO con éxito");

        return "chofer_mostrar.html";

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));

        return "chofer_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            choferServicio.eliminarChofer(id);
            String nombreMayuscula = logueado.getUsuario().toUpperCase();
        
            modelo.put("usuario", nombreMayuscula);
            modelo.put("id", logueado.getId());
            modelo.put("exito", "Chofer ELIMINADO con éxito");

            return "index_admin.html";

        } catch (MiException ex) {

            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.put("error", ex.getMessage());

            return "chofer_eliminar.html";
        }
    }

}
