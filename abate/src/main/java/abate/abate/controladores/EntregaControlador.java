package abate.abate.controladores;

import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Entrega;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.CuentaServicio;
import abate.abate.servicios.EntregaServicio;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private EntregaServicio entregaServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private CuentaServicio cuentaServicio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarEntrega(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("cuentas", cuentaServicio.buscarCuentasChofer(logueado.getIdOrg()));

        return "entrega_registrar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registrarId/{id}")
    public String registrarEntrega(@PathVariable Long id, ModelMap modelo) {
        
        Cuenta cuenta = cuentaServicio.buscarCuentaChofer(id);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        
        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);

        return "entrega_registrarId.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registro")
    public String registroEntrega(@RequestParam Long idChofer, @RequestParam String fecha,
            @RequestParam Double importe, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        entregaServicio.crearEntrega(logueado.getIdOrg(), idChofer, fecha, importe, observacion, logueado.getId());

        return "redirect:/entrega/registrado";

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registrado")
    public String entregaRegistrado(HttpSession session, ModelMap modelo) {
    
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Long id = entregaServicio.buscarUltimo(logueado.getIdOrg());
        Entrega entrega = entregaServicio.buscarEntrega(id);
        String total = convertirNumeroMiles(entrega.getImporte());  
        
        modelo.put("entrega", entrega);
        modelo.put("importe", total);
        modelo.put("exito", "Entrega REGISTRADA con éxito");
        
        return "entrega_registrado.html";
    }
    
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.addAttribute("entregas", entregaServicio.buscarEntregas(logueado.getIdOrg(), desde, hasta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", logueado.getIdOrg());

        return "entrega_listar.html";
    }
    
    @PostMapping("/listarFiltro")
    public String listarFiltro(Long id, String desde, String hasta, ModelMap modelo, HttpSession session) throws ParseException {

        modelo.addAttribute("entregas", entregaServicio.buscarEntregas(id, desde, hasta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("id", id);

        return "entrega_listar.html";
    }

    @GetMapping("/listarIdChofer/{id}")
    public String listarIdChofer(@PathVariable Long id, HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.addAttribute("entregas", entregaServicio.buscarEntregasIdChofer(id, desde, hasta));
            modelo.put("id", id);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);

            return "entrega_listarIdChofer.html";
            
        } else {

            modelo.addAttribute("entregas", entregaServicio.buscarEntregasIdChofer(id, desde, hasta));
            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.put("id", id);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);

            return "entrega_listarIdChoferAdmin.html";

        }
    }
    
    @PostMapping("/listarIdChoferFiltro")
    public String listarIdChoferFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.addAttribute("entregas", entregaServicio.buscarEntregasIdChofer(id, desde, hasta));
            modelo.put("id", id);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);

            return "entrega_listarIdChofer.html";
            
        } else {

            modelo.addAttribute("entregas", entregaServicio.buscarEntregasIdChofer(id, desde, hasta));
            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.put("id", id);
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);

            return "entrega_listarIdChoferAdmin.html";

        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("entrega", entregaServicio.buscarEntrega(id));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc(logueado.getIdOrg()));

        return "entrega_modificar.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
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
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("entrega", entregaServicio.buscarEntrega(id));

        return "entrega_eliminar.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
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
    
    public String obtenerFechaDesde() {

        LocalDate now = LocalDate.now();

        LocalDate firstDayOfMonth = now.withDayOfMonth(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = firstDayOfMonth.format(formatter);

        return formattedDate;

    }

    public String obtenerFechaHasta() {

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedToday = now.format(formatter);

        return formattedToday;

    }

}
