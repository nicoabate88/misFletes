
package abate.abate.controladores;

import abate.abate.entidades.Caja;
import abate.abate.entidades.Transaccion;
import abate.abate.servicios.CajaServicio;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.DetalleServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.GastoServicio;
import abate.abate.servicios.IngresoServicio;
import abate.abate.servicios.TransaccionServicio;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("/caja")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class CajaControlador {
    
    @Autowired
    private CajaServicio cajaServicio;
    @Autowired
    private TransaccionServicio transaccionServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private ExcelServicio excelServicio;
    @Autowired
    private GastoServicio gastoServicio;
    @Autowired
    private DetalleServicio detalleServicio;
    @Autowired
    private IngresoServicio ingresoServicio;
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/mostrarAdmin/{id}")
    public String mostrarIdChoferAdmin(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Caja caja = cajaServicio.buscarCajaChofer(id);
        String saldo = convertirNumeroMiles(caja.getSaldo());
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(caja.getId(), desde, hasta);
        Boolean flag = true;
        if(lista.isEmpty()){
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("caja", caja);
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_mostrarAdmin.html";

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/mostrarFiltroAdmin")
    public String mostrarFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{
        
        String saldo = "0.0";
        Boolean flag = true;
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFechaFiltro(id, desde, hasta);
        /*
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(id, desde, hasta);
        if (!lista.isEmpty()) {
          Transaccion primeraTransaccion = lista.get(0);
          saldo = convertirNumeroMiles(primeraTransaccion.getSaldoAcumulado());
        }
        */
        if(lista.isEmpty()){
            flag = false;
        }
        
        modelo.put("flag", flag);
        modelo.put("caja", cajaServicio.buscarCaja(id));
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "caja_mostrarFiltroAdmin.html";
       
    }
    
    @GetMapping("/mostrarChofer/{id}")
    public String mostrarChofer(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Caja caja = cajaServicio.buscarCajaChofer(id);
        String saldo = convertirNumeroMiles(caja.getSaldo());
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFecha(caja.getId(), desde, hasta);
        Boolean flag = true;
        if(lista.isEmpty()){
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("caja", caja);
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "caja_mostrarChofer.html";

    }
    
    @PostMapping("/mostrarFiltroChofer")
    public String mostrarFiltroChofer(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{

        Boolean flag = true;
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFechaFiltro(id, desde, hasta);

        if(lista.isEmpty()){
            flag = false;
        }
        
        modelo.put("flag", flag);
        modelo.put("caja", cajaServicio.buscarCaja(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "caja_mostrarFiltroChofer.html";
        
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/habilitar/{id}")
    public String habilitarCaja(@PathVariable Long id, ModelMap modelo){
        
        modelo.put("caja", cajaServicio.buscarCajaChofer(id));
        
        return "caja_habilitar.html";
        
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/habilita/{id}")
    public String habilitaCaja(@PathVariable Long id, ModelMap modelo){
        
        Caja caja = cajaServicio.buscarCaja(id);
        
        choferServicio.habilitarCajaChofer(caja.getChofer().getId());
        
        modelo.put("caja", caja);
        modelo.put("exito", "Caja HABILITADA con éxito");
        
        return "caja_habilitada.html";
        
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/inhabilitar/{id}")
    public String inhabilitarCaja(@PathVariable Long id, ModelMap modelo){
        
        modelo.put("caja", cajaServicio.buscarCajaChofer(id));
        
        return "caja_inhabilitar.html";
        
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/inhabilita/{id}")
    public String inhabilitaCaja(@PathVariable Long id, ModelMap modelo){
        
        Caja caja = cajaServicio.buscarCaja(id);
        
        choferServicio.inhabilitarCajaChofer(caja.getChofer().getId());
        
        modelo.put("caja", caja);
        modelo.put("exito", "Caja INHABILITADA con éxito");
        
        return "caja_habilitada.html";
        
    }
    
    @GetMapping("/mostrarTransaccionChofer/{id}")
    public String mostrarTransaccionChofer(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);
        
        if (transaccion.getIngreso() != null) {
            
             modelo.put("ingreso", ingresoServicio.buscarIngreso(transaccion.getIngreso().getId()));

            return "transaccion_cajaIngreso.html";
            
        } if (transaccion.getGasto() != null && !transaccion.getObservacion().startsWith("GASTO FTE")){ 
            
            Double valorAbsoluto = Math.abs(transaccion.getImporte());
            modelo.put("importe", valorAbsoluto);
            modelo.put("gasto", gastoServicio.buscarGasto(transaccion.getGasto().getId()));
            modelo.addAttribute("detalles", detalleServicio.buscarDetallesGasto(transaccion.getGasto().getId()));

            return "transaccion_cajaGasto.html";

        } else {
            
            Double valorAbsoluto = Math.abs(transaccion.getImporte());
            modelo.put("importe", valorAbsoluto);
            modelo.put("gasto", gastoServicio.buscarGasto(transaccion.getGasto().getId()));
            modelo.addAttribute("detalles", detalleServicio.buscarDetallesFleteIdGasto(transaccion.getGasto().getId()));

            return "transaccion_cajaGasto.html"; 
            
        }
    }
    
    @GetMapping("/mostrarTransaccionAdmin/{id}")
    public String mostrarTransaccionAdmin(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getIngreso() != null) {
            
             modelo.put("ingreso", ingresoServicio.buscarIngreso(transaccion.getIngreso().getId()));

            return "transaccion_cajaIngresoAdmin.html";
            
        } if (transaccion.getGasto() != null && !transaccion.getObservacion().startsWith("GASTO FTE")){ 
            
            Double valorAbsoluto = Math.abs(transaccion.getImporte());
            modelo.put("importe", valorAbsoluto);
            modelo.put("gasto", gastoServicio.buscarGasto(transaccion.getGasto().getId()));
            modelo.addAttribute("detalles", detalleServicio.buscarDetallesGasto(transaccion.getGasto().getId()));

            return "transaccion_cajaGastoAdmin.html";

        } else {
            
            Double valorAbsoluto = Math.abs(transaccion.getImporte());
            modelo.put("importe", valorAbsoluto);
            modelo.put("gasto", gastoServicio.buscarGasto(transaccion.getGasto().getId()));
            modelo.addAttribute("detalles", detalleServicio.buscarDetallesFleteIdGasto(transaccion.getGasto().getId()));

            return "transaccion_cajaGastoAdmin.html"; 
            
        }

    }
    
    @PostMapping("/exportarAdmin")
    public String exportarAdmin(@RequestParam Long id, ModelMap modelo) throws ParseException {
            
        Caja caja = cajaServicio.buscarCaja(id);
        String saldo = convertirNumeroMiles(caja.getSaldo());
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.put("caja", caja);
        modelo.put("saldo", saldo);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCajaFecha(id, desde, hasta));

        return "caja_exportarAdmin.html";         
  
    }
    
    @PostMapping("/exportaAdmin")
    public void exportaAdmin(@RequestParam Long id, HttpServletResponse response) throws IOException, ParseException {
  
        Caja caja = cajaServicio.buscarCaja(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Transaccion> myObjects = transaccionServicio.buscarTransaccionIdCajaFecha(id, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCaja(htmlContent, response, caja.getChofer().getNombre(), caja.getSaldo() );

    }
    
    @PostMapping("/exportarFiltroChofer")
    public String exportarFiltroChofer(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {  
        
        String saldo = "0.0";
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFechaFiltro(id, desde, hasta);
        if (!lista.isEmpty()) {
          Transaccion primeraTransaccion = lista.get(0);
          saldo = convertirNumeroMiles(primeraTransaccion.getSaldoAcumulado());
        }
        
        modelo.put("caja", cajaServicio.buscarCaja(id));
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "caja_exportarFiltroChofer.html";
  
    }
    
    @PostMapping("/exportarFiltroAdmin")
    public String exportarFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {  
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFechaFiltro(id, desde, hasta);
        
        modelo.put("caja", cajaServicio.buscarCaja(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "caja_exportarFiltroAdmin.html";
  
    }
    
    @PostMapping("/exportaFiltroAdmin")
    public void exportaFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {
  
        Caja caja = cajaServicio.buscarCaja(id);
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCajaFechaFiltro(id, desde, hasta);
        
        ArrayList<Transaccion> myObjects = lista;
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCajaMovimiento(htmlContent, response, caja.getChofer().getNombre(), desde, hasta);

    }
    
    private String generateHtmlFromObjects(ArrayList<Transaccion> objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<thead><tr>"
                + "<th>Fecha</th>"
                + "<th>Concepto</th>"
                + "<th>Importe</th>"
                + "<th>Saldo</th>"
                + "</tr></thead>");
        sb.append("<tbody>");
        for (Transaccion transaccion : objects) {
            sb.append("<tr><td>").append(transaccion.getFecha()).append("</td>"
                    + "<td>").append(transaccion.getObservacion()).append("</td>"
                    + "<td>").append(transaccion.getImporte()).append("</td>"
                    + "<td>").append(transaccion.getSaldoAcumulado()).append("</td>"                           
                    + "</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }
    
    private String convertirNumeroMiles(Double num) {   

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }
    
     public String obtenerFechaDesde() {
    // Obtener la fecha actual
    LocalDate now = LocalDate.now();

    // Obtener el primer día del mes anterior
    LocalDate firstDayOfPreviousMonth = now.minusMonths(1).withDayOfMonth(1);

    // Formatear la fecha a 'yyyy-MM-dd'
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Convertir la fecha a String
    String formattedDate = firstDayOfPreviousMonth.format(formatter);

    return formattedDate;
}

    public String obtenerFechaHasta() {

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedToday = now.format(formatter);

        return formattedToday;

    }    
     
    
}
