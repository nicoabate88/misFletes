package abate.abate.controladores;

import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.CamionServicio;
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
public class ChoferControlador {

    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private CamionServicio camionServicio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrar(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

        return "chofer_registrar.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam Long cuil, @RequestParam(required = false) Long idCamion,
            @RequestParam Double porcentaje, @RequestParam String nombreUsuario, @RequestParam String password,
            @RequestParam String password2, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            choferServicio.crearChofer(logueado.getIdOrg(), nombre, cuil, idCamion, nombreUsuario, porcentaje, password, password2);
            Long id = choferServicio.buscarUltimo();
            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.put("exito", "Chofer REGISTRADO con éxito");

            return "chofer_registrado.html";

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.put("cuil", cuil);
            modelo.put("nombreUsuario", nombreUsuario);
            modelo.put("porcentaje", porcentaje);
            modelo.put("error", ex.getMessage());

            return "chofer_registrar.html";
        }
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(logueado.getIdOrg()));

        return "chofer_listar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));

        return "chofer_mostrar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));

        return "chofer_modificar.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam Long cuil,
            @RequestParam(required = false) Long idCamion, @RequestParam Double porcentaje, @RequestParam String nombreUsuario, ModelMap modelo, HttpSession session) {

        try {
            choferServicio.modificarChofer(id, nombre, cuil, idCamion, nombreUsuario, porcentaje);

            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.put("exito", "Chofer MODIFICADO con éxito");

            return "chofer_registrado.html";

        } catch (MiException ex) {

            Usuario logueado = (Usuario) session.getAttribute("usuariosession");

            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
            modelo.put("error", ex.getMessage());
            
            return "chofer_modificar.html";

        }

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("chofer", choferServicio.buscarChofer(id));

        return "chofer_eliminar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
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
