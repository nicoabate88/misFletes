
package abate.abate.controladores;

import abate.abate.entidades.Usuario;
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
  
    
    @PostMapping("/registroPrimerCarga")
    public String registroPrimerCarga(@RequestParam Long idChofer, @RequestParam String fecha, 
            @RequestParam Double km, ModelMap modelo) throws ParseException{
        
        combustibleServicio.crearPrimerCarga(fecha, km, idChofer);
        modelo.put("exito", "KM de Camión REGISTRADO con éxito");
        
        return "combustible_registradoPrimerCarga";
    }
    
    @GetMapping("/registrar")
    public String registrarCarga(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        boolean flag = combustibleServicio.kmIniciales(logueado);
        
        if(flag == true){
            
        modelo.put("chofer", logueado);
        modelo.put("kmAnterior", combustibleServicio.kmAnterior(logueado));
        
        return "combustible_registrar.html";
            
         } else {
         
        modelo.put("chofer", logueado);
        
        return "combustible_registrarPrimerCarga";
            
        }
        
    }
    
    @GetMapping("/registrarAdmin/{id}")
    public String registrarCargaAdmin(@PathVariable Long id,ModelMap modelo) {
        
        Usuario chofer = choferServicio.buscarChofer(id);
        
        modelo.put("chofer", chofer );
        modelo.put("kmAnterior", combustibleServicio.kmAnterior(chofer));
        
        return "combustible_registrar.html";
        
    }
    
    @PostMapping("/registro")
    public String registroCarga(@RequestParam Long idChofer, @RequestParam String fecha, @RequestParam Double km,
            @RequestParam Double litro, @RequestParam String completo, ModelMap modelo) throws ParseException {
            
            combustibleServicio.crearCarga(fecha, km, litro, completo, idChofer);
            
            Long id = combustibleServicio.buscarUltimo();
            
            modelo.put("carga", combustibleServicio.buscarCombustible(id));
            modelo.put("fecha", fecha);
            modelo.put("exito", "Carga de Combustible REGISTRADA con éxito");

            return "combustible_registrado.html";
    
    }
    
    @GetMapping("/listar/{id}")
    public String listarCargas(@PathVariable Long id, ModelMap modelo) throws ParseException{
        
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasIdChofer(id, desde, hasta));
        
        return "combustible_listarCargas.html";
    }
    
    @PostMapping("/listarIdChoferFiltro")
    public String listarCargas(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, 
            ModelMap modelo) throws ParseException{
        
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasIdChofer(id, desde, hasta));
        
        return "combustible_listarCargas.html";
    }
    
    
    @GetMapping("/listarAdmin")
    public String listarAdmin(ModelMap modelo){
        
        modelo.addAttribute("choferes", combustibleServicio.buscarConsumoCamiones());
        
        return "combustible_listarAdmin.html";
    }
    
    @GetMapping("/mostrarConsumoAdmin/{id}")
    public String mostrarConsumoAdmin(@PathVariable Long id, ModelMap modelo) {        

        modelo.put("consumo", combustibleServicio.consumoPromedioCamion(id));
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasChofer(id));

        return "combustible_mostrarConsumoAdmin.html";

    }
    
    @GetMapping("/listarCargasAdmin/{id}")
    public String listarCargasAdmin(@PathVariable Long id, ModelMap modelo) throws ParseException{
        
        Usuario chofer = choferServicio.buscarChofer(id);
        
        boolean flag = combustibleServicio.kmIniciales(chofer);
        
        if(flag == true){
            
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        modelo.put("chofer", chofer);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasIdChofer(id, desde, hasta));
        
        return "combustible_listarCargasAdmin.html";
            
        } else {
         
        modelo.put("chofer", chofer);
        
        return "combustible_registrarPrimerCarga";
            
        }
        
    }
    
    @PostMapping("/listarCargasIdFiltroAdmin")
    public String listarCargasFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, 
            ModelMap modelo) throws ParseException{
        
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasIdChofer(id, desde, hasta));
        
        return "combustible_listarCargasAdmin.html";
    }
    
    @GetMapping("/mostrarConsumo/{id}")
    public String mostrarConsumo(@PathVariable Long id, ModelMap modelo) {        

        modelo.put("consumo", combustibleServicio.consumoPromedioCamion(id));
        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.addAttribute("cargas", combustibleServicio.buscarCargasChofer(id));

        return "combustible_mostrarConsumo.html";

    }
    
     @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));

        return "combustible_modificar.html";

    }

    @PostMapping("/modifica/{id}")   
    public String modifica(@RequestParam Long id, @RequestParam Long idChofer, @RequestParam String fecha,
            @RequestParam Double km, @RequestParam Double litro, @RequestParam String completo,
            ModelMap modelo) throws ParseException {
        
        combustibleServicio.modificarCarga(id, fecha, km, litro, completo, idChofer);

        modelo.put("carga", combustibleServicio.buscarCombustible(id));
        modelo.put("fecha", fecha);
        modelo.put("exito", "Carga de Combustible MODIFICADA con éxito");

        return "combustible_registrado.html";
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
