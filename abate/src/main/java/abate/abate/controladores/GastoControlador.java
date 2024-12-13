package abate.abate.controladores;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Detalle;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.DetalleServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.FleteServicio;
import abate.abate.servicios.GastoServicio;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private ExcelServicio excelServicio;

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getGasto() != null && !flete.getGasto().getEstado().equalsIgnoreCase("ELIMINADO") && logueado.getRol().equalsIgnoreCase("ADMIN")) {

            Gasto gasto = gastoServicio.buscarGasto(flete.getGasto().getId());

            String importe = convertirNumeroMiles(gasto.getImporte());

            modelo.put("idFlete", id);
            modelo.put("gasto", gasto);
            modelo.addAttribute("detalles", detalleServicio.buscarDetallesFlete(id));
            modelo.put("importe", importe);

            return "gasto_mostrarAdmin.html";

        }

        if (flete.getGasto() != null && !flete.getGasto().getEstado().equalsIgnoreCase("ELIMINADO") && logueado.getRol().equalsIgnoreCase("CHOFER")) {

            Gasto gasto = gastoServicio.buscarGasto(flete.getGasto().getId());

            modelo.put("flete", flete);
            modelo.put("gasto", gasto);
            modelo.addAttribute("detalles", detalleServicio.buscarDetallesFlete(id));
            modelo.put("importe", convertirNumeroMiles(gasto.getImporte()));

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
    
    @GetMapping("/registrarDesdeCajaAdmin/{id}")
    public String registrarGastoDesdeCajaAdmin(@PathVariable Long id, ModelMap modelo) {

        Usuario chofer = choferServicio.buscarChofer(id);

        modelo.put("chofer", chofer);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(chofer.getIdOrg()));

        return "gasto_registrarDesdeCajaAdmin.html";

    }

    @GetMapping("/registrarDesdeCaja/{id}")
    public String registrarGastoDesdeCaja(@PathVariable Long id, ModelMap modelo) {

        Usuario chofer = choferServicio.buscarChofer(id);

        modelo.put("chofer", chofer);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(chofer.getIdOrg()));

        return "gasto_registrarDesdeCaja.html";

    }

    @PostMapping("/registraDesdeCaja")
    public String registraDesdeCaja(@RequestParam Long idChofer, @RequestParam String fecha, @RequestParam Long idCamion,
            @RequestParam String concepto, @RequestParam Integer cantidad,
            @RequestParam Double precio, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Long idGasto = gastoServicio.crearGastoCaja(logueado.getIdOrg(), fecha, idCamion, idChofer, logueado.getId());

        detalleServicio.crearDetalleGasto(idGasto, concepto, cantidad, precio);

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        modelo.put("idGasto", idGasto);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_agregarDetalleCaja.html";
    }

    @PostMapping("/registraModificaDesdeCaja")
    public String registraModificaDesdeCaja(@RequestParam Long idGasto, ModelMap modelo) throws ParseException {

        gastoServicio.crearModificarGastoCaja(idGasto);

        return "redirect:/gasto/registradoDesdeCaja/" + idGasto;

    }

    @GetMapping("/registradoDesdeCaja/{idGasto}")
    public String gastoRegistradoDesdeCaja(@PathVariable Long idGasto, ModelMap modelo) {

        Gasto gasto = gastoServicio.buscarGasto(idGasto);
        
        modelo.put("gasto", gasto);
        modelo.put("importe", convertirNumeroMiles(gasto.getImporte()));
        modelo.addAttribute("detalles", detalleServicio.buscarDetallesGasto(idGasto));
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

        modelo.put("idGasto", idGasto);
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

        modelo.put("idGasto", idGasto);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);

        return "gasto_agregarDetalleCaja.html";

    }

    @GetMapping("/cancelarCaja/{idGasto}")
    public String cancelarCaja(@PathVariable Long idGasto, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        gastoServicio.crearEliminarGastoCaja(idGasto, logueado.getId());

        Gasto gasto = gastoServicio.buscarGasto(idGasto);
        
        Usuario chofer = choferServicio.buscarChofer(gasto.getChofer().getId());
        
        if(logueado.getRol().equalsIgnoreCase("CHOFER")){

        modelo.put("chofer", chofer);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(chofer.getIdOrg()));

        return "gasto_registrarDesdeCaja";
        
        } else {
            
        modelo.put("chofer", chofer);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(chofer.getIdOrg()));

        return "gasto_registrarDesdeCajaAdmin";
                
        }
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
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(gasto.getChofer().getIdOrg()));

        return "gasto_modificarCaja.html";

    }

    @PostMapping("/modificaCaja")
    public String modificaCaja(@RequestBody Map<String, Object> datos, HttpSession session, ModelMap modelo) throws ParseException {
        // Extraer los datos enviados desde el cliente
        String fecha = (String) datos.get("fecha");
        Long idCamion = Long.parseLong(datos.get("idCamion").toString());
        Long idGasto = Long.parseLong(datos.get("idGasto").toString());

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Gasto gasto = gastoServicio.buscarGasto(idGasto);

        if (!gasto.getNombre().startsWith("GASTO FTE")) {

            gastoServicio.modificarGastoCaja(idGasto, logueado.getId(), fecha, idCamion);

        } else {

            Long idFlete = fleteServicio.buscarIdFleteIdGasto(idGasto);
            gastoServicio.modificarGastoFleteDesdeCaja(idFlete, idGasto, logueado.getId(), fecha, idCamion);

        }

        return "redirect:/gasto/gasto_modificadoCaja?idGasto=" + idGasto;

    }

    @GetMapping("/gasto_modificadoCaja")
    public String mostrarGastoModificado(@RequestParam Long idGasto, ModelMap modelo) {

        Gasto gasto = gastoServicio.buscarGasto(idGasto);
        ArrayList<Detalle> lista = new ArrayList();

        if (!gasto.getNombre().startsWith("GASTO FTE")) {

            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);

        } else {

            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(idGasto);

        }
        
        modelo.put("gasto", gasto);
        modelo.put("importe", convertirNumeroMiles(gasto.getImporte()));
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

        if (!gasto.getNombre().startsWith("GASTO FTE")) {

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
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(gasto.getChofer().getIdOrg()));

        return "gasto_modificarCaja.html";
    }

    @PostMapping("/borrarDetalleMCaja")
    public String borrarDetalleMCaja(@RequestParam Long idGasto, @RequestParam Long idDetalle, ModelMap modelo) {

        detalleServicio.eliminarDetalle(idDetalle);

        Gasto gasto = gastoServicio.buscarGasto(idGasto);
        ArrayList<Detalle> lista = new ArrayList();
        Double importe = 0.0;

        if (!gasto.getNombre().startsWith("GASTO FTE")) {

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
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(gasto.getChofer().getIdOrg()));

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

        gastoServicio.eliminarGastoCaja(idGasto, logueado.getId());

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("chofer", logueado);
            modelo.put("exito", "Gasto ELIMINADO con éxito");

            return "index_chofer.html";

        } else {

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
        } else {

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
        } else {

            lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(id);

        }

        modelo.put("gasto", gasto);
        modelo.put("detalles", lista);

        return "gasto_volverPendiente.html";

    }

    @GetMapping("/vuelvePendiente/{id}")
    public String vuelvePendiente(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        gastoServicio.volverPendienteGasto(id, logueado);

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

        Gasto gasto = gastoServicio.buscarGasto(flete.getGasto().getId());
        
        modelo.put("id", flete.getId());
        modelo.put("gasto", gasto);
        modelo.put("importe", convertirNumeroMiles(gasto.getImporte()));
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
    public String cancelar(@PathVariable Long flete, ModelMap modelo) {

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
        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.put("id", id);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(flete.getIdOrg()));

        return "gasto_modificar.html";

    }

    @GetMapping("/modificarDesdeCamion/{id}")
    public String modificarDesdeCamion(@PathVariable Long id, ModelMap modelo) {

        Long idFlete = fleteServicio.buscarIdFleteIdGasto(id);
        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFlete(idFlete);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }
        Gasto gasto = gastoServicio.buscarGasto(id);

        modelo.put("id", idFlete);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);
        modelo.put("gasto", gasto);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(gasto.getIdOrg()));

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

        Flete flete = fleteServicio.buscarFlete(idFlete);

        modelo.put("id", idFlete);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(flete.getIdOrg()));
        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));

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

        Flete flete = fleteServicio.buscarFlete(idFlete);

        modelo.put("id", idFlete);
        modelo.put("total", importe);
        modelo.addAttribute("detalles", lista);
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(flete.getIdOrg()));
        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));

        return "gasto_modificarM.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestBody Map<String, Object> datos, HttpSession session, ModelMap modelo) throws ParseException {

        String fecha = (String) datos.get("fecha");
        Long idCamion = Long.parseLong(datos.get("idCamion").toString());
        Long idFlete = Long.parseLong(datos.get("idFlete").toString());

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Flete flete = fleteServicio.buscarFlete(idFlete);
        Long idGasto = flete.getGasto().getId();

        gastoServicio.modificarGasto(idFlete, idGasto, logueado.getId(), fecha, idCamion);

        return "redirect:/gasto/modificado?idFlete=" + idFlete;
    }

    @GetMapping("/modificado")
    public String gastoModificado(@RequestParam Long idFlete, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(idFlete);
        Gasto gasto = gastoServicio.buscarGasto(flete.getGasto().getId());
        
        modelo.put("id", idFlete);
        modelo.put("gasto", gasto);
        modelo.put("importe", convertirNumeroMiles(gasto.getImporte()));
        modelo.addAttribute("detalles", detalleServicio.buscarDetallesFlete(idFlete));
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
        Flete flete = fleteServicio.buscarFlete(idFlete);
        Long idGasto = flete.getGasto().getId();

        gastoServicio.eliminarGasto(idFlete, idGasto, logueado.getId());

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("chofer", logueado);
            modelo.put("exito", "Gasto ELIMINADO con éxito");

            return "index_chofer.html";

        } else {

            modelo.put("id", logueado.getId());
            modelo.put("exito", "Gasto ELIMINADO con éxito");

            return "index_admin.html";
        }

    }

    @GetMapping("/listarCamion/{idCamion}")
    public String listarCamion(@PathVariable Long idCamion, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        ArrayList<Gasto> lista = gastoServicio.buscarGastosCamion(idCamion, desde, hasta);
        Double total = 0.0;
        for (Gasto g : lista) {
            total = total + g.getImporte();
            g.setImporteS(convertirNumeroMiles(g.getImporte()));
        }

        String totalS = convertirNumeroMiles(total);

        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.addAttribute("gastos", lista);
        modelo.put("total", totalS);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "gasto_listarCamion.html";
    }

    @PostMapping("/listarCamionFiltro")
    public String listarCamionFiltro(@RequestParam Long idCamion, @RequestParam String desde,
            @RequestParam String hasta, ModelMap modelo) throws ParseException {

        ArrayList<Gasto> lista = gastoServicio.buscarGastosCamion(idCamion, desde, hasta);
        Double total = 0.0;
        for (Gasto g : lista) {
            total = total + g.getImporte();
            g.setImporteS(convertirNumeroMiles(g.getImporte()));
        }

        String totalS = convertirNumeroMiles(total);

        Boolean flag = true;
        if (lista.isEmpty()) {
            flag = false;
        }

        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.addAttribute("gastos", lista);
        modelo.put("total", totalS);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("flag", flag);

        return "gasto_listarCamionFiltro.html";
    }

    @GetMapping("/detalleCamion/{id}")
    public String detalleCamion(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Gasto gasto = gastoServicio.buscarGasto(id);

        if (gasto.getNombre().startsWith("GASTO FTE")) {

            modelo.put("gasto", gastoServicio.buscarGasto(id));
            modelo.addAttribute("detalles", detalleServicio.buscarDetallesFleteIdGasto(id));
            modelo.put("importe", convertirNumeroMiles(gasto.getImporte()));

            return "gasto_mostrarDesdeCamion.html";

        } else {

            modelo.put("gasto", gastoServicio.buscarGasto(id));
            modelo.addAttribute("detalles", detalleServicio.buscarDetallesGasto(id));
            modelo.put("importe", convertirNumeroMiles(gasto.getImporte()));

            return "gasto_mostrarDesdeCamion.html";

        }

    }

    @PostMapping("/exportar")
    public String exportar(@RequestParam Long idCamion, @RequestParam String desde,
            @RequestParam String hasta, ModelMap modelo) throws ParseException {

        modelo.addAttribute("gastos", gastoServicio.buscarGastosCamion(idCamion, desde, hasta));
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "gasto_exportar.html";

    }

    @PostMapping("/exporta")
    public void exporta(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long idCamion,
            HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Gasto> myObjects = gastoServicio.buscarGastosCamion(idCamion, desde, hasta);
        Camion camion = camionServicio.buscarCamion(idCamion);

        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelGasto(htmlContent, response, camion);

    }

    @PostMapping("/exportarFiltro")
    public String exportarFiltro(@RequestParam Long idCamion, @RequestParam String desde,
            @RequestParam String hasta, ModelMap modelo) throws ParseException {

        modelo.addAttribute("gastos", gastoServicio.buscarGastosCamion(idCamion, desde, hasta));
        modelo.put("camion", camionServicio.buscarCamion(idCamion));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "gasto_exportarFiltro.html";

    }

    @PostMapping("/exportaFiltro")
    public void exportaFiltro(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long idCamion,
            HttpServletResponse response) throws IOException, ParseException {

        ArrayList<Gasto> myObjects = gastoServicio.buscarGastosCamion(idCamion, desde, hasta);
        Camion camion = camionServicio.buscarCamion(idCamion);

        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelGasto(htmlContent, response, camion);

    }

    private String generateHtmlFromObjects(ArrayList<Gasto> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Fecha</th>"
                + "<th>Concepto</th>"
                + "<th>Chofer</th>"
                + "<th>Importe</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Gasto g : objects) {
            sb.append("<tr><td>").append(g.getFecha()).append("</td>"
                    + "<td>").append(g.getNombre()).append("</td>"
                    + "<td>").append(g.getChofer().getNombre()).append("</td>"
                    + "<td>").append(g.getImporte()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
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

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

    private String convertirNumeroMiles(Double num) {

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "AR"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat formato = new DecimalFormat("#,##0.00", symbols);
        String numeroFormateado = formato.format(num);

        return numeroFormateado;
    }

}
