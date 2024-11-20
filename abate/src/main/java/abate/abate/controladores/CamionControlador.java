
package abate.abate.controladores;

import abate.abate.entidades.Camion;
import abate.abate.entidades.CamionEstadistica;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.util.CamionEstadisticaComparador;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
@RequestMapping("/camion")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class CamionControlador {
    
    @Autowired
    private CamionServicio camionServicio;
    @Autowired
    private ExcelServicio excelServicio;
    
    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {

        return "camion_registrar.html";
        
    }
    
    @PostMapping("/registro")
    public String registro(@RequestParam String marca, @RequestParam String modelo, @RequestParam String dominio, ModelMap model, HttpSession session){
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            camionServicio.crearCamion(logueado.getIdOrg(), marca, modelo, dominio);
            
            return "redirect:/camion/registrado";

        } catch (MiException ex) {

            model.put("marca", marca);
            model.put("modelo", modelo);
            model.put("dominio", dominio);
            model.put("error", ex.getMessage());

            return "camion_registrar.html";
        }
    }
    
    @GetMapping("/registrado")
    public String camionRegistrado(HttpSession session, ModelMap modelo) {
    
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        Long id = camionServicio.buscarUltimo(logueado.getIdOrg());
            
            modelo.put("camion", camionServicio.buscarCamion(id));
            modelo.put("exito", "Camión REGISTRADO con éxito");

            return "camion_mostrar.html";
    }
    
    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session){
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc(logueado.getIdOrg()));
        
        return "camion_listar.html";
        
    }
    
    @GetMapping("/mostrarEstadistica/{id}")
    public String buscarEstadistica(@PathVariable Long id, ModelMap modelo) throws ParseException{
        
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        ArrayList<CamionEstadistica> lista = camionServicio.estadisticaCamion(desde, hasta, id);
            
            for(CamionEstadistica e : lista){
                e.setConsumo((double) Math.round((100 * e.getLitro())/ e.getKmRecorrido()));
                if(e.getKmRecorrido() != 0.0){
                e.setRentabilidad((double) Math.round(e.getNeto()/e.getKmRecorrido()));
                } else {
                    e.setRentabilidad(0.0);
                }
                String neto = convertirNumeroMiles(e.getNeto());
                e.setNetoS(neto);
            }
            
        Collections.sort(lista, CamionEstadisticaComparador.ordenarMes);      
        
        modelo.put("camion", camionServicio.buscarCamion(id));
        modelo.addAttribute("estadistica", lista);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        
        return "camion_estadistica.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {
        
        modelo.put("camion", camionServicio.buscarCamion(id));
        
        return "camion_modificar.html";

    }

    @PostMapping("/modifica")
    public String modifica(@RequestParam Long id, @RequestParam String marca, @RequestParam String modelo, @RequestParam String dominio, ModelMap model) {

        try {
            camionServicio.modificarCamion(id, marca, modelo, dominio);

            model.put("camion", camionServicio.buscarCamion(id));
            model.put("exito", "Camión MODIFICADO con éxito");

            return "camion_mostrar.html";

        } catch (MiException ex) {

            model.put("camion", camionServicio.buscarCamion(id));
            model.put("error", ex.getMessage());

            return "camion_modificar.html";
        }

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
    
     public String obtenerFechaDesde() {

        LocalDate now = LocalDate.now();

        LocalDate firstDayOfYear = now.withDayOfYear(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = firstDayOfYear.format(formatter);

        return formattedDate;

    }

    public String obtenerFechaHasta() {

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedToday = now.format(formatter);

        return formattedToday;

    }
    
    @PostMapping("/estadisticaExportar")
    public String estadisticaExportar(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long id, ModelMap modelo) throws ParseException {

         ArrayList<CamionEstadistica> lista = camionServicio.estadisticaCamion(desde, hasta, id);
            
            for(CamionEstadistica e : lista){
                e.setConsumo((double) Math.round((100 * e.getLitro())/ e.getKmRecorrido()));
                if(e.getKmRecorrido() != 0.0){
                e.setRentabilidad((double) Math.round(e.getNeto()/e.getKmRecorrido()));
                } else {
                    e.setRentabilidad(0.0);
                }
                String neto = convertirNumeroMiles(e.getNeto());
                e.setNetoS(neto);
            }
            
        Collections.sort(lista, CamionEstadisticaComparador.ordenarMes);
        
        modelo.addAttribute("estadistica", lista);
        modelo.put("id", id);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);

        return "camion_estadisticaExportar.html";
        
        }
    
    @PostMapping("/estadisticaExporta")
    public void exportToExcel(@RequestParam String desde, @RequestParam String hasta, @RequestParam Long id, HttpServletResponse response) throws IOException, ParseException {
        
        Camion camion = camionServicio.buscarCamion(id);
        ArrayList<CamionEstadistica> myObjects = camionServicio.estadisticaCamion(desde, hasta, id);
            for(CamionEstadistica e : myObjects){
                e.setConsumo((double) Math.round((100 * e.getLitro())/ e.getKmRecorrido()));
                if(e.getKmRecorrido() != 0.0){
                e.setRentabilidad((double) Math.round(e.getNeto()/e.getKmRecorrido()));
                } else {
                    e.setRentabilidad(0.0);
                }
            }
            
        Collections.sort(myObjects, CamionEstadisticaComparador.ordenarMes);
        
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelEstadistica(htmlContent, response, camion);

    }
    
       private String generateHtmlFromObjects(ArrayList<CamionEstadistica> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Año</th>"
                + "<th>Mes</th>"
                + "<th>Km</th>"
                + "<th>Litros</th>"
                + "<th>Consumo</th>"
                + "<th>Fletes</th>"
                + "<th>Neto</th>"
                + "<th>Rentabilidad</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (CamionEstadistica e : objects) {
            sb.append("<tr><td>").append(e.getYear()).append("</td>"
                    + "<td>").append(e.getMonth()).append("</td>"
                    + "<td>").append(e.getKmRecorrido()).append("</td>"
                    + "<td>").append(e.getLitro()).append("</td>"
                    + "<td>").append(e.getConsumo()).append("</td>"
                    + "<td>").append(e.getFlete()).append("</td>"        
                    + "<td>").append(e.getNeto()).append("</td>"        
                    + "<td>").append(e.getRentabilidad()).append("</td>"                           
                    + "</tr>");    
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }
       
       public String convertirNumeroMiles(Double num) {  

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }
    
}
