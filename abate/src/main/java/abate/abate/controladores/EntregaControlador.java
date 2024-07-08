package abate.abate.controladores;

import abate.abate.entidades.Entrega;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.EntregaServicio;
import abate.abate.servicios.UsuarioServicio;
import java.text.DecimalFormat;
import java.text.ParseException;
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
@RequestMapping("/entrega")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class EntregaControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private EntregaServicio entregaServicio;
    @Autowired
    private ChoferServicio choferServicio;

    @GetMapping("/registrar")
    public String registrarEntrega(ModelMap modelo) {

        modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc());

        return "entrega_registrar.html";
    }

    @GetMapping("/registrarId/{id}")
    public String registrarEntrega(@PathVariable Long id, ModelMap modelo) {

        modelo.addAttribute("chofer", usuarioServicio.buscarUsuario(id));

        return "entrega_registrarId.html";
    }

    @PostMapping("/registro")
    public String registroEntrega(@RequestParam Long idChofer, @RequestParam String fecha,
            @RequestParam Double importe, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        entregaServicio.crearEntrega(idChofer, fecha, importe, observacion, logueado.getId());
        Long id = entregaServicio.buscarUltimo();
        Entrega entrega = entregaServicio.buscarEntrega(id);
        String total = convertirNumeroMiles(entrega.getImporte());

        modelo.put("entrega", entrega);
        modelo.put("importe", total);
        modelo.put("fecha", fecha);
        modelo.put("exito", "Entrega REGISTRADA con éxito");

        return "entrega_registrado.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("entregas", entregaServicio.buscarEntregas());

        return "entrega_listar.html";
    }

    @GetMapping("/listarIdChofer/{id}")
    public String listarIdChofer(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.addAttribute("entregas", entregaServicio.buscarEntregasIdChofer(id));
            modelo.put("id", id);

            return "entrega_listarIdChofer.html";
            
        } else {

            modelo.addAttribute("entregas", entregaServicio.buscarEntregasIdChofer(id));
            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.put("id", id);

            return "entrega_listarIdChoferAdmin.html";

        }
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("entrega", entregaServicio.buscarEntrega(id));
        modelo.addAttribute("clientes", choferServicio.bucarChoferesNombreAsc());

        return "entrega_modificar.html";

    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam Long idChofer, @RequestParam String fecha,
            @RequestParam Double importe, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        entregaServicio.modificarEntrega(id, idChofer, fecha, importe, observacion, logueado.getId());

        Entrega entrega = entregaServicio.buscarEntrega(id);
        String total = convertirNumeroMiles(entrega.getImporte());

        modelo.put("entrega", entrega);
        modelo.put("importe", total);
        modelo.put("fecha", fecha);
        modelo.put("exito", "Entrega MODIFICADA con éxito");

        return "entrega_registrado.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("entrega", entregaServicio.buscarEntrega(id));

        return "entrega_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        entregaServicio.eliminarEntrega(id);
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String nombreMayuscula = logueado.getUsuario().toUpperCase();
        
        modelo.put("usuario", nombreMayuscula);
        modelo.put("id", logueado.getId());
        modelo.put("exito", "Entrega ELIMINADA con éxito");
        
        return "index_admin.html";

    }

    private String convertirNumeroMiles(Double num) {   

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }

}
