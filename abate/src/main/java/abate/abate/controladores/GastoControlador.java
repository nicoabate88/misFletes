package abate.abate.controladores;

import abate.abate.entidades.Detalle;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
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
    
    @GetMapping("/registrarDesdeFlete/{id}")
    public String registrarGastoDesdeFlete(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", id);

        return "gasto_registrarDesdeFlete.html";

    }
    
    @GetMapping("/registrarDesdeCaja/{id}")
    public String registrarGastoDesdeCaja(@PathVariable Long id, ModelMap modelo) {

        modelo.put("idChofer", id);

        return "gasto_registrarDesdeCaja.html";

    }
    
    @PostMapping("/registraDesdeCaja")
    public String registraDesdeCaja(@RequestParam String fecha, @RequestParam String concepto, @RequestParam Integer cantidad,
            @RequestParam Double precio, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Long idGasto = gastoServicio.crearGastoCaja(logueado.getIdOrg(), fecha, logueado.getId());
        
        detalleServicio.crearDetalleGasto(idGasto, concepto, cantidad, precio);

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        modelo.put("gasto", idGasto);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_agregarDetalleCaja.html";
    }
    
    @PostMapping("/registraModificaDesdeCaja")
    public String registraModificaDesdeCaja(@RequestParam Long idGasto, ModelMap modelo) throws ParseException {
        
        gastoServicio.crearModificarGastoCaja(idGasto);
        
        return "redirect:/gasto/registradoDesdeCaja/" + idGasto;
        
    }
    
    @GetMapping("/registradoDesdeCaja/{id}")
    public String gastoRegistradoDesdeCaja(@PathVariable Long id, ModelMap modelo) {
        
        modelo.put("gasto", gastoServicio.buscarGasto(id));
        modelo.addAttribute("detalles", detalleServicio.buscarDetallesGasto(id));
        modelo.put("exito", "Gasto REGISTRADO con éxito");
        
        return "gasto_registradoCaja.html";
        
    }
    
    @PostMapping("/agregarDetalleCaja")
    public String agregarDetalleCaja(@RequestParam Long idGasto, @RequestParam String concepto, @RequestParam Integer cantidad,
            @RequestParam Double precio, ModelMap modelo) throws ParseException {

        detalleServicio.crearDetalleGasto(idGasto, concepto, cantidad, precio);

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        modelo.put("gasto", idGasto);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_agregarDetalleCaja.html";
    }
    
    @PostMapping("/borrarDetalleCaja")
    public String borrarDetalleCaja(@RequestParam Long idGasto, @RequestParam Long idDetalle, ModelMap modelo) {

        detalleServicio.eliminarDetalle(idDetalle);

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        modelo.put("gasto", idGasto);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_agregarDetalleCaja.html";

    }
    
    @GetMapping("/cancelarCaja/{id}")
    public String cancelarCaja(@PathVariable Long id, ModelMap modelo, HttpSession session){
 
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        gastoServicio.crearEliminarGastoCaja(id, logueado.getId());
        
        modelo.put("idChofer", logueado.getId());
        
        return "gasto_registrarDesdeCaja";
    }
    
    @GetMapping("/modificarCaja/{id}")
    public String modificarCaja(@PathVariable Long id, ModelMap modelo) {

        Gasto gasto = gastoServicio.buscarGasto(id);
        Double importe = 0.0;
        ArrayList<Detalle> lista = new ArrayList();

        if (!gasto.getNombre().startsWith("GASTO FTE")) {

            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(id);
            for (Detalle d : lista) {
                importe = importe + d.getTotal();

            }
        } else {

            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(id);
            for (Detalle d : lista) {
                importe = importe + d.getTotal();
            }
        }
        modelo.put("gasto", gasto);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_modificarCaja.html";

    }
    
    @GetMapping("/modificaCaja/{idGasto}")
    public String modificaCaja(@PathVariable Long idGasto, HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Gasto gasto = gastoServicio.buscarGasto(idGasto);
        ArrayList<Detalle> lista = new ArrayList();
        
        if(!gasto.getNombre().startsWith("GASTO FTE")){
            
            gastoServicio.modificarGastoCaja(idGasto, logueado.getId());
            
            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
            
        } else {
            
            Long idFlete = fleteServicio.buscarIdFleteIdGasto(idGasto);
            gastoServicio.modificarGastoFleteDesdeCaja(idFlete, idGasto, logueado.getId());
            
            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(idGasto);
            
        }
        
        modelo.put("gasto", gasto);
        modelo.addAttribute("detalles", lista);
        modelo.put("exito", "Gasto MODIFICADO con éxito");
        
        return "gasto_registradoCaja.html";

    }
    
    @PostMapping("/agregarDetalleMCaja")
    public String agregarDetalleMCaja(@RequestParam Long idGasto, @RequestParam String concepto, @RequestParam Integer cantidad,
            @RequestParam Double precio, ModelMap modelo) throws ParseException {

        Gasto gasto = gastoServicio.buscarGasto(idGasto);
        ArrayList<Detalle> lista = new ArrayList();
        Double importe = 0.0;
        
        if(!gasto.getNombre().startsWith("GASTO FTE")){
            
            detalleServicio.crearDetalleGasto(idGasto, concepto, cantidad, precio);
            
        lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();  
        } 
        } else {
            
            Long idFlete = fleteServicio.buscarIdFleteIdGasto(idGasto);
            
            detalleServicio.crearDetalle(idFlete, concepto, cantidad, precio);
            
        lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(idGasto);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();     
          
        }
        }

        modelo.put("gasto", gasto);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_modificarCaja.html";
    }
    
    @PostMapping("/borrarDetalleMCaja")
    public String borrarDetalleMCaja(@RequestParam Long idGasto, @RequestParam Long idDetalle, ModelMap modelo) {

        detalleServicio.eliminarDetalle(idDetalle);

        Gasto gasto = gastoServicio.buscarGasto(idGasto);
        ArrayList<Detalle> lista = new ArrayList();
        Double importe = 0.0;
        
        if(!gasto.getNombre().startsWith("GASTO FTE")){
            
        lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();  
        } 
        } else {
            
        lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(idGasto);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();     
          
        }
        }
        
        modelo.put("gasto", gasto);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_modificarCaja.html";

    }
    
    @GetMapping("/eliminarCaja/{idGasto}")
    public String eliminarCaja(@PathVariable Long idGasto, ModelMap modelo) {

        Gasto gasto = gastoServicio.buscarGasto(idGasto);
        ArrayList<Detalle> lista = new ArrayList();

        if (!gasto.getNombre().startsWith("GASTO FTE")) {
            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
        } else {
            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(idGasto);
        }

        modelo.put("gasto", gasto);
        modelo.addAttribute("detalles", lista);

        return "gasto_eliminarCaja.html";
    }

    @GetMapping("/eliminaCaja/{idGasto}")
    public String eliminaCaja(@PathVariable Long idGasto, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String nombreMayuscula = logueado.getUsuario().toUpperCase();

        gastoServicio.eliminarGastoCaja(idGasto, logueado.getId());

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
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/aceptar/{id}")
    public String aceptar(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        gastoServicio.aceptarGastoCaja(id, logueado);
        
        Gasto gasto = gastoServicio.buscarGasto(id);
        ArrayList<Detalle> lista = new ArrayList();

        if (!gasto.getNombre().startsWith("GASTO FTE")) {
            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(id);
            }
        else {

            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(id);

            }
        
            modelo.put("importe", gasto.getImporte());
            modelo.put("gasto", gasto);
            modelo.addAttribute("detalles", lista);

            return "transaccion_cajaGastoAdmin.html";
        
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/volverPendiente/{id}")
    public String volverPendiente(@PathVariable Long id, ModelMap modelo) {

        Gasto gasto = gastoServicio.buscarGasto(id);
        ArrayList<Detalle> lista = new ArrayList();

        if (!gasto.getNombre().startsWith("GASTO FTE")) {
            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(id);
            }
        else {

            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(id);

            }
        
        modelo.put("gasto", gasto);
        modelo.put("detalles", lista);

        return "gasto_volverPendiente.html";

    }
    
    @GetMapping("/vuelvePendiente/{id}")
    public String vuelvePendiente(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String nombreMayuscula = logueado.getUsuario().toUpperCase();
        
        gastoServicio.volverPendienteGasto(id, logueado);
        
        modelo.put("usuario", nombreMayuscula);
        modelo.put("id", logueado.getId());
        modelo.put("exito", "Gasto RETORNADO a Pendiente");

        return "index_admin.html";
        
    }

    @PostMapping("/registro")
    public String registro(@RequestParam Long idFlete, HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        gastoServicio.crearGasto(logueado.getIdOrg(), idFlete, logueado.getId());
        
        return "redirect:/gasto/registrado/" + idFlete;

    }
    
    @GetMapping("/registrado/{id}")
    public String gastoRegistrado(@PathVariable Long id, ModelMap modelo) {
        
        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("id", flete.getId());
        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.addAttribute("detalles", detalleServicio.buscarDetallesFlete(id));
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
    
    @GetMapping("/cancelar/{flete}")
    public String cancelar(@PathVariable Long flete, ModelMap modelo){
        
        detalleServicio.cancelarDetalle(flete);
        
        modelo.put("flete", flete);
        
        return "gasto_registrarDesdeCancela.html";
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

        return "gasto_modificarM.html";
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

        return "gasto_modificarM.html";

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
