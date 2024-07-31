
package abate.abate.controladores;

import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.CamionServicio;
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
@RequestMapping("/camion")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class CamionControlador {
    
    @Autowired
    private CamionServicio camionServicio;
    
    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {

        return "camion_registrar.html";
        
    }
    
    @PostMapping("/registro")
    public String registro(@RequestParam String marca, @RequestParam String modelo, @RequestParam String dominio, ModelMap model){

        try {

            camionServicio.crearCamion(marca, modelo, dominio);
            
            Long id = camionServicio.buscarUltimo();
            
            model.put("camion", camionServicio.buscarCamion(id));
            model.put("exito", "Camión REGISTRADO con éxito");

            return "camion_mostrar.html";

        } catch (MiException ex) {

            model.put("marca", marca);
            model.put("modelo", modelo);
            model.put("dominio", dominio);
            model.put("error", ex.getMessage());

            return "camion_registrar.html";
        }
    }
    
    @GetMapping("/listar")
    public String listar(ModelMap modelo){
        
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc());
        
        return "camion_listar.html";
        
    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("camion", camionServicio.buscarCamion(id));

        return "camion_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String marca, @RequestParam String modelo, @RequestParam String dominio, ModelMap model) {

        camionServicio.modificarCamion(id, marca, modelo, dominio);

        model.put("camion", camionServicio.buscarCamion(id));
        model.put("exito", "Camión MODIFICADO con éxito");

        return "camion_mostrar.html";

    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("camion", camionServicio.buscarCamion(id));

        return "camion_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            camionServicio.eliminarCamion(id);
            String nombreMayuscula = logueado.getUsuario().toUpperCase();
        
            modelo.put("usuario", nombreMayuscula);
            modelo.put("id", logueado.getId());
            modelo.put("exito", "Camión ELIMINADO con éxito");

            return "index_admin.html";

        } catch (MiException ex) {

            modelo.put("camion", camionServicio.buscarCamion(id));
            modelo.put("error", ex.getMessage());

            return "camion_eliminar.html";
        }
    }
    
}
