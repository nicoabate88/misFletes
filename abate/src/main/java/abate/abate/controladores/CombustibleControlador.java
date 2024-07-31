
package abate.abate.controladores;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CamionServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.CombustibleServicio;
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
@RequestMapping("/combustible")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class CombustibleControlador {
    
    @Autowired
    private CombustibleServicio combustibleServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private CamionServicio camionServicio;
    
    @GetMapping("/registrar")
    public String registrar(ModelMap modelo, HttpSession session){
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        
        modelo.put("chofer", choferServicio.buscarChofer(logueado.getId()));
        modelo.addAttribute("camiones", camionServicio.buscarCamionesAsc());
        
        return "combustible_registrar.html";
    }

    @PostMapping("/registrarChofer")
    public String registrarCarga(@RequestParam("idCamion") Long idCamion, ModelMap modelo) {
        
        Camion camion = camionServicio.buscarCamion(idCamion);
        
        boolean flag = combustibleServicio.kmIniciales(camion);
        
        if(flag == true){
        
        modelo.put("kmAnterior", combustibleServicio.kmAnterior(camion));    
        modelo.put("camion", camion);
        
        return "combustible_registrarChofer.html";
            
         } else {
         
        modelo.put("camion", camion);    
        modelo.put("exito", "Comunicarse con su Administrador");
        
        return "combustible_mensajeKm.html";
            
        }
        
    }
    
    @GetMapping("/registrarAdmin/{id}")
    public String registrarCargaAdmin(@PathVariable Long id, ModelMap modelo) {
        
        Camion camion = camionServicio.buscarCamion(id);

        boolean flag = combustibleServicio.kmIniciales(camion);

        if (flag == true) {

            modelo.put("kmAnterior", combustibleServicio.kmAnterior(camion));
            modelo.put("camion", camion);

            return "combustible_registrarAdmin.html";

        } else {

            modelo.put("camion", camion);

            return "combustible_registrarPrimerCarga";

        }

    }
    
    @PostMapping("/registroPrimerCarga")
    public String registroPrimerCarga(@RequestParam Long idCamion, @RequestParam String fecha, 
            @RequestParam Double km, ModelMap modelo, HttpSession session) throws ParseException{
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        combustibleServicio.crearPrimerCarga(fecha, km, idCamion, logueado);
        
        Long id = combustibleServicio.buscarUltimo();
        
        modelo.put("carga", combustibleServicio.buscarCombustible(id));
        modelo.put("fecha", fecha);
        modelo.put("exito", "KM de Camión REGISTRADO con éxito");
        
        return "combustible_registradoPrimerCarga";
    }
    
    @PostMapping("/registro")
    public String registroCarga(@RequestParam Long idCamion, @RequestParam Double kmAnterior, @RequestParam String fecha, @RequestParam Double km,
            @RequestParam Double litro, @RequestParam String completo, ModelMap modelo, HttpSession session) throws ParseException {
        
            Usuario logueado = (Usuario) session.getAttribute("usuariosession");
            
            combustibleServicio.crearCarga(idCamion, fecha, kmAnterior, km, litro, completo, logueado);
            
            Long id = combustibleServicio.buscarUltimo();
            
            modelo.put("carga", combustibleServicio.buscarCombustible(id));
            modelo.put("fecha", fecha);
            modelo.put("exito", "Carga de Combustible REGISTRADA con éxito");

            return "combustible_registrado.html";
    
    }
    
    @GetMapping("/listarChofer/{id}")
    public String listarCargas(@PathVariable Long id, ModelMap modelo) throws ParseException{
        
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasIdChofer(id, desde, hasta));
        
        return "combustible_listarCargasChofer.html";
    }
    
    @PostMapping("/listarChoferFiltro")
    public String listarCargas(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{
        
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasIdChofer(id, desde, hasta));
        
        return "combustible_listarCargasChofer.html";
    }
    
    @GetMapping("/listarAdmin/{id}")
    public String listarAdmin(@PathVariable Long id, ModelMap modelo) throws ParseException{
        
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasIdCamion(id, desde, hasta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", id);
        
        return "combustible_listarCargasAdmin.html";
    }
    
    @PostMapping("/listarAdminFiltro")
    public String listarAdminFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{
        
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasIdCamion(id, desde, hasta));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("idCamion", id);
        
        return "combustible_listarCargasAdmin.html";
    }
    
    @GetMapping("/mostrarConsumoAdmin/{id}")
    public String mostrarConsumoAdmin(@PathVariable Long id, ModelMap modelo) {        

        modelo.put("consumo", combustibleServicio.consumoPromedioCamion(id));
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasCamion(id));
        modelo.put("camion", camionServicio.buscarCamion(id));

        return "combustible_mostrarConsumoAdmin.html";

    }
    
    @GetMapping("/mostrarConsumoChofer/{id}")
    public String mostrarConsumo(@PathVariable Long id, ModelMap modelo) {        

        modelo.put("consumo", combustibleServicio.consumoPromedioCamion(id));
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasCamion(id));
        modelo.put("camion", camionServicio.buscarCamion(id));

        return "combustible_mostrarConsumoChofer.html";

    }
    
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));

        return "combustible_modificar.html";

    }

    @PostMapping("/modifica/{id}")   
    public String modifica(@RequestParam Long id, @RequestParam String fecha, @RequestParam Double km, 
            @RequestParam Double litro, @RequestParam String completo, ModelMap modelo, HttpSession session) throws ParseException {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        combustibleServicio.modificarCarga(id, fecha, km, litro, completo, logueado);

        modelo.put("carga", combustibleServicio.buscarCombustible(id));
        modelo.put("fecha", fecha);
        modelo.put("exito", "Carga de Combustible MODIFICADA con éxito");

        return "combustible_modificado.html";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));

        return "combustible_eliminar.html";

    }

    @GetMapping("/elimina/{id}")   
    public String elimina(@PathVariable Long id, ModelMap modelo, HttpSession session) throws ParseException {
        
        combustibleServicio.eliminarCarga(id);

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String nombreMayuscula = logueado.getUsuario().toUpperCase();

        if (logueado.getRol().equalsIgnoreCase("CHOFER")) {

            modelo.put("chofer", logueado);
            modelo.put("exito", "Carga de Combustible ELIMINADA con éxito");
            modelo.put("usuario", nombreMayuscula);

            return "index_chofer.html";

        } else {

            modelo.put("usuario", nombreMayuscula);
            modelo.put("exito", "Carga de Combustible ELIMINADA con éxito");
            modelo.put("id", logueado.getId());

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
}
