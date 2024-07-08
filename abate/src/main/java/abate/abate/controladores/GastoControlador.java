package abate.abate.controladores;

import abate.abate.entidades.Detalle;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.DetalleServicio;
import abate.abate.servicios.FleteServicio;
import abate.abate.servicios.GastoServicio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
@RequestMapping("/gasto")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class GastoControlador {

    @Autowired
    private DetalleServicio detalleServicio;
    @Autowired
    private GastoServicio gastoServicio;
    @Autowired
    private FleteServicio fleteServicio;

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getGasto() != null && logueado.getRol().equalsIgnoreCase("ADMIN")) {

            modelo.put("idFlete", id);
            modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
            modelo.addAttribute("detalles", detalleServicio.buscarDetallesFlete(id));

            return "gasto_mostrarAdmin.html";

        }
        
        if (flete.getGasto() != null && logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("flete", flete);
            modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
            modelo.addAttribute("detalles", detalleServicio.buscarDetallesFlete(id));

            return "gasto_mostrarChofer.html";

        }
        
        if (flete.getGasto() == null && flete.getEstado().equalsIgnoreCase("PENDIENTE")) {

            modelo.put("flete", id);

            return "gasto_registrar.html";

        } else {

            modelo.put("idFlete", id);

            return "gasto_mensaje.html";

        }

    }

    @GetMapping("/registrar/{id}")
    public String registrarGasto(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", id);

        return "gasto_registrar.html";

    }

    @PostMapping("/registro")
    public String registro(@RequestParam Long idFlete, HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        gastoServicio.crearGasto(idFlete, logueado.getId());
        
        Flete flete = fleteServicio.buscarFlete(idFlete);

        modelo.put("id", flete.getId());
        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.addAttribute("detalles", detalleServicio.buscarDetallesFlete(idFlete));
        modelo.put("exito", "Gasto REGISTRADO con éxito");

        return "gasto_registrado.html";

    }

    @PostMapping("/agregarDetalle")
    public String agregarDetalle(@RequestParam Long idFlete, @RequestParam String concepto, @RequestParam Integer cantidad,
            @RequestParam Double precio, ModelMap modelo) throws ParseException {

        detalleServicio.crearDetalle(idFlete, concepto, cantidad, precio);

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFlete(idFlete);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        modelo.put("flete", idFlete);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_agregarDetalle.html";
    }

    @PostMapping("/borrarDetalle")
    public String borrarDetalle(@RequestParam Long idFlete, @RequestParam Long idDetalle, ModelMap modelo) {

        detalleServicio.eliminarDetalle(idDetalle);

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFlete(idFlete);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        modelo.put("flete", idFlete);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_agregarDetalle.html";

    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFlete(id);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        modelo.put("id", id);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_modificar.html";

    }

    @PostMapping("/agregarDetalleM")
    public String agregarDetalleM(@RequestParam Long idFlete, @RequestParam String concepto, @RequestParam Integer cantidad,
            @RequestParam Double precio, ModelMap modelo) throws ParseException {

        detalleServicio.crearDetalle(idFlete, concepto, cantidad, precio);

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFlete(idFlete);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        modelo.put("id", idFlete);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_modificar.html";
    }

    @PostMapping("/borrarDetalleM")
    public String borrarDetalleM(@RequestParam Long idFlete, @RequestParam Long idDetalle, ModelMap modelo) {

        detalleServicio.eliminarDetalle(idDetalle);

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFlete(idFlete);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        modelo.put("id", idFlete);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_modificar.html";

    }

    @GetMapping("/modifica/{id}")
    public String modifica(@PathVariable Long id, HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Flete flete = fleteServicio.buscarFlete(id);
        Long idGasto = flete.getGasto().getId();

        gastoServicio.modificarGasto(id, idGasto, logueado.getId());

        modelo.put("id", id);
        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.addAttribute("detalles", detalleServicio.buscarDetallesFlete(id));
        modelo.put("exito", "Gasto MODIFICADO con éxito");

        return "gasto_registrado.html";

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);
        Long idGasto = flete.getGasto().getId();

        modelo.put("gasto", gastoServicio.buscarGasto(idGasto));
        modelo.put("idFlete", id);
        modelo.addAttribute("detalles", detalleServicio.buscarDetallesFlete(id));

        return "gasto_eliminar.html";
    }

    @GetMapping("/elimina/{idFlete}")
    public String elimina(@PathVariable Long idFlete, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String nombreMayuscula = logueado.getUsuario().toUpperCase();
        Flete flete = fleteServicio.buscarFlete(idFlete);
        Long idGasto = flete.getGasto().getId();

        gastoServicio.eliminarGasto(idFlete, idGasto, logueado.getId());

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
            
            modelo.put("chofer", logueado);
            modelo.put("usuario", nombreMayuscula);
            modelo.put("exito", "Gasto ELIMINADO con éxito");

            return "index_chofer.html";

        } else {
        
            modelo.put("usuario", nombreMayuscula);
            modelo.put("id", logueado.getId());
            modelo.put("exito", "Gasto ELIMINADO con éxito");

            return "index_admin.html";
        }

    }

    public Date convertirFecha(String fecha) throws ParseException { 
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
