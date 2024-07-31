package abate.abate.controladores;

import abate.abate.entidades.Flete;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.ClienteServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.FleteServicio;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("/flete")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class FleteControlador {

    @Autowired
    private FleteServicio fleteServicio;
    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private ExcelServicio excelServicio;
    @Autowired
    private CamionServicio camionServicio;
    ArrayList<Flete> excel = new ArrayList();
    String fechaDesde;
    String fechaHasta;
    Long idChofer;

    @GetMapping("/registrar")
    public String registrarFlete(ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("chofer", logueado);
            modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc());

            return "flete_registrarChofer.html";

        } else {

            modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc());
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc());

            return "flete_registrarAdmin.html";
        }
    }

    @PostMapping("/registroChofer")
    public String registroFlete(@RequestParam Long idCliente, @RequestParam Long idCamion, @RequestParam String fechaCarga, @RequestParam String fechaFlete,
            @RequestParam String origen, @RequestParam String destino, @RequestParam Double km, @RequestParam String tipoCereal,
            @RequestParam String cPorte, @RequestParam String ctg, @RequestParam Double tarifa, @RequestParam Double kg,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        fleteServicio.crearFleteChofer(fechaCarga, idCliente, idCamion, origen, fechaFlete, destino, km, tipoCereal, tarifa, cPorte, ctg, kg, logueado.getId());
        Long id = fleteServicio.buscarUltimo();

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("fecha", fechaFlete);
        modelo.put("exito", "Flete REGISTRADO con éxito");

        return "flete_registradoChofer.html";

    }

    @PostMapping("/registroAdmin")
    public String registroFleteAdmin(@RequestParam Long idChofer, @RequestParam Long idCamion, @RequestParam Long idCliente, @RequestParam String fechaCarga, @RequestParam String fechaFlete,
            @RequestParam String origen, @RequestParam String destino, @RequestParam Double km, @RequestParam String tipoCereal, @RequestParam String cPorte, 
            @RequestParam String ctg, @RequestParam Double tarifa, @RequestParam Double kg, @RequestParam Double comisionTpte, @RequestParam String comisionTpteChofer,
            @RequestParam String iva, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        fleteServicio.crearFleteAdmin(idChofer, idCamion, fechaCarga, idCliente, origen, fechaFlete, destino, km, tipoCereal, tarifa, cPorte, ctg, kg, comisionTpte, comisionTpteChofer, iva, logueado.getId());
        
        Long id = fleteServicio.buscarUltimo();

        modelo.put("flete", fleteServicio.buscarFlete(id));
        modelo.put("fecha", fechaFlete);
        modelo.put("exito", "Flete REGISTRADO con éxito");

        return "flete_registradoAdmin.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            String desde = obtenerFechaDesde();
            String hasta = obtenerFechaHasta();

            modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferFecha(logueado.getId(), desde, hasta));
            modelo.put("chofer", choferServicio.buscarChofer(logueado.getId()));
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);

            return "flete_listarChofer.html";

        } else {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesPendiente());

            return "flete_listarPendiente.html";
        }

    }

    @PostMapping("/listarChoferFiltro")
    public String listarChoferFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta,
            ModelMap modelo) throws ParseException {

        modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferFecha(id, desde, hasta));
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "flete_listarChofer.html";
    }

    @GetMapping("/listarIdCliente/{id}")
    public String listarIdCliente(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.addAttribute("fletes", fleteServicio.buscarFletesIdClienteFecha(id, desde, hasta));
        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "flete_listarIdCliente";
    }

    @PostMapping("/listarIdClienteFiltro")
    public String listarIdClienteFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {

        modelo.addAttribute("fletes", fleteServicio.buscarFletesIdClienteFecha(id, desde, hasta));
        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "flete_listarIdCliente";
    }

    @GetMapping("/listarIdChofer/{id}")
    public String listarIdChofer(@PathVariable Long id, ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferFecha(id, desde, hasta));
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "flete_listarIdChofer";
    }

    @PostMapping("/listarIdChoferFiltro")
    public String listarIdChoferFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta,
            ModelMap modelo) throws ParseException {

        modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferFecha(id, desde, hasta));
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "flete_listarIdChofer";
    }

    @GetMapping("/listarTodo")
    public String listarMes(ModelMap modelo) throws ParseException {

        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.addAttribute("fletes", fleteServicio.buscarFletesRangoFecha(desde, hasta));
        modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc());
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "flete_listarTodo";
    }

    @PostMapping("/listarXfechaAdmin")
    public String listarXfechaAdmin(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long id,
            ModelMap modelo) throws ParseException {

        if (id == null) {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesRangoFecha(desde, hasta));
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc());
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("id", id);

            return "flete_listarTodoFiltrado";

        } else {

            modelo.addAttribute("fletes", fleteServicio.buscarFletesIdChoferFecha(id, desde, hasta));
            modelo.put("chofer", choferServicio.buscarChofer(id));
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);

            return "flete_listarTodoExporta.html";

        }
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("flete", fleteServicio.buscarFlete(id));

            return "flete_mostrarChofer.html";

        } else {

            modelo.put("flete", fleteServicio.buscarFlete(id));

            return "flete_mostrarAdmin.html";
        }

    }
    
    @GetMapping("/mostrarPendiente/{id}")
    public String mostrarPendiente(@PathVariable Long id, ModelMap modelo) {

            modelo.put("flete", fleteServicio.buscarFlete(id));

            return "flete_mostrarPendienteAdmin.html";
        }
    
    @GetMapping("/mostrarDesdeCtaChofer/{id}")
    public String mostrarDesdeCtaChofer(@PathVariable Long id, ModelMap modelo) {

            modelo.put("flete", fleteServicio.buscarFlete(id));

            return "flete_mostrarAdminCtaChofer.html";

    }
    
    @GetMapping("/mostrarDesdeCtaCliente/{id}")
    public String mostrarDesdeCtaCliente(@PathVariable Long id, ModelMap modelo) {

            modelo.put("flete", fleteServicio.buscarFlete(id));

            return "flete_mostrarAdminCtaCliente.html";

    }

    @GetMapping("/mostrarFiltrado/{id}")
    public String mostrarFiltrado(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "flete_mostrarFiltrado.html";

    }

    @GetMapping("/aceptar/{id}")
    public String aceptarFlete(@PathVariable Long id, ModelMap modelo) {

        fleteServicio.aceptarFlete(id);

        modelo.addAttribute("fletes", fleteServicio.buscarFletesPendiente());
        modelo.put("exito", "Flete ACEPTADO con éxito");

        return "flete_listarPendiente.html";

    }

    @GetMapping("/volverPendiente/{id}")
    public String volverPendiente(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "flete_volverPendiente.html";

    }

    @GetMapping("/pendiente/{id}")
    public String pendienteFlete(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String nombreMayuscula = logueado.getUsuario().toUpperCase();
        fleteServicio.pendienteFlete(id, logueado.getId());
        
        modelo.put("usuario", nombreMayuscula);
        modelo.put("id", logueado.getId());
        modelo.put("exito", "Flete RETORNADO a Pendiente");

        return "index_admin.html";

    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc());

            return "flete_modificarChofer.html";

        } else {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc());
            modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc());

            return "flete_modificarAdmin.html";
        }
    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam Long idChofer, @RequestParam Long idCamion, @RequestParam Long idCliente, @RequestParam String fechaCarga, 
            @RequestParam String fechaFlete, @RequestParam String origen, @RequestParam String destino, @RequestParam Double km, @RequestParam String tipoCereal,
            @RequestParam String cPorte, @RequestParam String ctg, @RequestParam Double tarifa, @RequestParam Double kg, @RequestParam Double comisionTpte, 
            @RequestParam String comisionTpteChofer, @RequestParam Double iva, @RequestParam Double porcentaje, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        Flete flete = fleteServicio.buscarFlete(id);
        
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            fleteServicio.modificarFlete(id, idChofer, idCamion, fechaCarga, idCliente, origen, fechaFlete, destino, km, tipoCereal, tarifa, cPorte, ctg, 
                    kg, iva, porcentaje, comisionTpte, comisionTpteChofer, logueado.getId());

            modelo.put("flete", flete);
            modelo.put("fecha", fechaFlete);
            modelo.put("exito", "Flete MODIFICADO con éxito");

            return "flete_modificadoChofer.html";

        } if (logueado.getRol().equalsIgnoreCase("ADMIN") && flete.getEstado().equalsIgnoreCase("PENDIENTE")){
            
            fleteServicio.modificarFlete(id, idChofer, idCamion, fechaCarga, idCliente, origen, fechaFlete, destino, km, tipoCereal, tarifa, cPorte, ctg, 
                    kg, iva, porcentaje, comisionTpte, comisionTpteChofer, logueado.getId());

            modelo.put("flete", flete);
            modelo.put("fecha", fechaFlete);
            modelo.put("exito", "Flete MODIFICADO con éxito");

            return "flete_modificadoAdminPendiente.html";
            
        }
        
        else {

            fleteServicio.modificarFlete(id, idChofer, idCamion, fechaCarga, idCliente, origen, fechaFlete, destino, km, tipoCereal, tarifa, cPorte, ctg, 
                    kg, iva, porcentaje, comisionTpte, comisionTpteChofer, logueado.getId());

            modelo.put("flete", flete);
            modelo.put("fecha", fechaFlete);
            modelo.put("exito", "Flete MODIFICADO con éxito");

            return "flete_modificadoAdmin.html";

        }
    }
    

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            return "flete_eliminarChofer.html";

        } else {

            modelo.put("flete", fleteServicio.buscarFlete(id));
            return "flete_eliminarAdmin.html";
        }
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        fleteServicio.eliminarFlete(id, logueado.getId());
        
        String nombreMayuscula = logueado.getUsuario().toUpperCase();

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {
            modelo.put("usuario", nombreMayuscula);
            modelo.put("chofer", logueado);
            modelo.put("exito", "Flete ELIMINADO con éxito");

            return "index_chofer.html";

        } else {

            modelo.put("usuario", nombreMayuscula);
            modelo.put("id", logueado.getId());
            modelo.put("exito", "Flete ELIMINADO con éxito");

            return "index_admin.html";
        }

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

    @PostMapping("/exportar")
    public String exportarFletes(@RequestParam String desde, @RequestParam String hasta, @RequestParam(required = false) Long id,
            ModelMap modelo) throws ParseException {

        if (id == null) {
            modelo.addAttribute("fletes", fleteServicio.buscarFletesRangoFecha(desde, hasta));
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc());
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("id", id);

            return "flete_exportar";

        } else {

            excel.clear();
            excel = fleteServicio.buscarFletesIdChoferFecha(id, desde, hasta);
            fechaDesde = desde;
            fechaHasta = hasta;
            idChofer = id;
            modelo.addAttribute("fletes", excel);
            modelo.addAttribute("choferes", choferServicio.bucarChoferesNombreAsc());
            modelo.put("desde", desde);
            modelo.put("hasta", hasta);
            modelo.put("id", id);

            return "flete_exportar";
        }
    }

    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response) throws IOException {

        Usuario chofer = choferServicio.buscarChofer(idChofer);
        ArrayList<Flete> myObjects = excel;
        String subtitle = "PERÍODO: " + fechaDesde + " a " + fechaHasta;
        String subtitle3 = "NOMBRE DEL CONDUCTOR: " + chofer.getNombre() + " - CUIT: " + chofer.getCuil();

        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcel(htmlContent, subtitle, subtitle3, response);

    }

    private String generateHtmlFromObjects(ArrayList<Flete> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Fecha día</th>"
                + "<th>Carga en</th>"
                + "<th>Sale de</th>"
                + "<th>Fecha día</th>"
                + "<th>Llega a</th>"
                + "<th>Camión</th>"
                + "<th>KM</th>"
                + "<th>Tipo Cereal</th>"
                + "<th>Tarifa</th>"
                + "<th>CP</th>"
                + "<th>CTG</th>"
                + "<th>Descarga KG</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Flete flete : objects) {
            sb.append("<tr><td>").append(flete.getFechaCarga()).append("</td>"
                    + "<td>").append(flete.getCliente().getNombre()).append("</td>"
                    + "<td>").append(flete.getOrigenFlete()).append("</td>"
                    + "<td>").append(flete.getFechaFlete()).append("</td>"
                    + "<td>").append(flete.getDestinoFlete()).append("</td>"
                    + "<td>").append(flete.getCamion().getDominio()).append("</td>"        
                    + "<td>").append(flete.getKmFlete()).append("</td>"
                    + "<td>").append(flete.getTipoCereal()).append("</td>"
                    + "<td>").append("$" + flete.getTarifa()).append("</td>"
                    + "<td>").append(flete.getCartaPorte()).append("</td>"
                    + "<td>").append(flete.getCtg()).append("</td>"
                    + "<td>").append(flete.getKgFlete()).append("</td>"
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

}
