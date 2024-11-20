package abate.abate.controladores;

import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Detalle;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.ChoferServicio;
import abate.abate.servicios.CuentaServicio;
import abate.abate.servicios.DetalleServicio;
import abate.abate.servicios.EntregaServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.FleteServicio;
import abate.abate.servicios.GastoServicio;
import abate.abate.servicios.ReciboServicio;
import abate.abate.servicios.TransaccionServicio;
import java.io.IOException;
import java.text.DecimalFormat;
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
@RequestMapping("/cuenta")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class CuentaControlador {

    @Autowired
    private CuentaServicio cuentaServicio;
    @Autowired
    private TransaccionServicio transaccionServicio;
    @Autowired
    private FleteServicio fleteServicio;
    @Autowired
    private ReciboServicio reciboServicio;
    @Autowired
    private GastoServicio gastoServicio;
    @Autowired
    private EntregaServicio entregaServicio;
    @Autowired
    private ExcelServicio excelServicio;
    @Autowired
    private ChoferServicio choferServicio;
    @Autowired
    private DetalleServicio detalleServicio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listarChofer")
    public String listarChofer(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Double total = 0.0;
        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasChofer(logueado.getIdOrg());
        for (Cuenta c : cuentas) {
            total = total + c.getSaldo();
            String saldoN = convertirNumeroMiles(c.getSaldo());
            c.setSaldoN(saldoN);
        }

        String saldo = convertirNumeroMiles(total);

        modelo.addAttribute("cuentas", cuentas);
        modelo.put("saldo", saldo);

        return "cuenta_listarChofer.html";
    }

    @GetMapping("/mostrarChofer/{id}")
    public String mostrarChofer(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        Boolean flag = true;
        if(lista.isEmpty()){
            flag = false;
        }
        
        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.put("saldo", saldo);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarChoferAdmin.html";

    }
    
    @PostMapping("/mostrarChoferFiltroAdmin")
    public String mostrarChoferFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{
       
        Boolean flag = true;
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);

        if(lista.isEmpty()){
            flag = false;
        }
        
        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_mostrarChoferFiltroAdmin.html";
        
    }
    
     @PostMapping("/mostrarIdChoferFiltroAdmin")
    public String mostrarIdChoferFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{

        Boolean flag = true;
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);

        if(lista.isEmpty()){
            flag = false;
        }
        
        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_mostrarIdChoferFiltroAdmin.html";
        
    }
    
    @PostMapping("/mostrarChoferFiltro")
    public String mostrarChoferFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{
        
        Boolean flag = true;
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);

        if(lista.isEmpty()){
            flag = false;
        }
        
        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_mostrarChoferFiltro.html";
    }   

    @GetMapping("/mostrarIdChofer/{id}")
    public String mostrarIdChofer(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Long idCuenta = cuentaServicio.buscarIdCuentaChofer(id);
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        Boolean flag = true;
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(idCuenta, desde, hasta);
        if(lista.isEmpty()){
            flag = false;
        }

        modelo.put("chofer", choferServicio.buscarChofer(id));
        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarChofer.html";

    }
    
    @GetMapping("/mostrarIdChoferAdmin/{id}")
    public String mostrarIdChoferAdmin(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Long idCuenta = cuentaServicio.buscarIdCuentaChofer(id);
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(idCuenta, desde, hasta);
        Boolean flag = true;
        if(lista.isEmpty()){
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarIdChoferAdmin.html";

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/listarCliente")
    public String listarCliente(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Double total = 0.0;
        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentasCliente(logueado.getIdOrg());
        for (Cuenta c : cuentas) {
            total = total + c.getSaldo();
            String saldoN = convertirNumeroMiles(c.getSaldo());
            c.setSaldoN(saldoN);
        }

        String saldo = convertirNumeroMiles(total);

        modelo.addAttribute("cuentas", cuentas);
        modelo.put("saldo", saldo);

        return "cuenta_listarCliente.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/mostrarCliente/{id}")
    public String mostrarCliente(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        Boolean flag = true;
        if(lista.isEmpty()){
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarCliente.html";

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/mostrarIdCliente/{id}")
    public String mostrarIdCliente(@PathVariable Long id, ModelMap modelo) throws ParseException {

        Long idCuenta = cuentaServicio.buscarIdCuentaCliente(id);
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(idCuenta, desde, hasta);
        Boolean flag = true;
        if(lista.isEmpty()){
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarIdCliente.html";

    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/mostrarClienteFiltro")
    public String mostrarClienteFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{

        Boolean flag = true;
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);

        if(lista.isEmpty()){
            flag = false;
        }
        
        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_mostrarClienteFiltro.html";
        
    }
    
    @PostMapping("/mostrarIdClienteFiltro")
    public String mostrarIdClienteFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{

        Boolean flag = true;
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);

        if(lista.isEmpty()){
            flag = false;
        }
        
        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_mostrarIdClienteFiltro.html";
        
    }

    @GetMapping("/mostrarTransaccionCliente/{id}")
    public String mostrarTransaccion(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getFlete() != null) {
            
            modelo.put("flete", fleteServicio.buscarFlete(transaccion.getFlete().getId()));

            return "transaccion_fleteCliente.html";

        } else {

            Double valorAbsoluto = Math.abs(transaccion.getImporte());

            modelo.put("importe", valorAbsoluto);
            modelo.put("recibo", reciboServicio.buscarRecibo(transaccion.getRecibo().getId()));

            return "transaccion_recibo.html";
        }

    }
    
    @GetMapping("/mostrarTransaccionClienteDesdeCuenta/{id}")
    public String mostrarTransaccionCliente(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getFlete() != null) {

            modelo.put("idCuenta", cuentaServicio.buscarIdCuentaCliente(transaccion.getCliente().getId()));
            modelo.put("flete", fleteServicio.buscarFlete(transaccion.getFlete().getId()));

            return "transaccion_fleteClienteCuenta.html";

        } else {

            Double valorAbsoluto = Math.abs(transaccion.getImporte());
            modelo.put("idCuenta", cuentaServicio.buscarIdCuentaCliente(transaccion.getCliente().getId()));
            modelo.put("importe", valorAbsoluto);
            modelo.put("recibo", reciboServicio.buscarRecibo(transaccion.getRecibo().getId()));

            return "transaccion_reciboCuenta.html";
        }

    }
    
    @GetMapping("/mostrarTransaccionChoferAdmin/{id}")
    public String mostrarTransaccionChoferAdmin(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getFlete() != null) {

            modelo.put("flete", fleteServicio.buscarFlete(transaccion.getFlete().getId()));

            return "transaccion_fleteChoferAdmin.html";

        }
        
        if (transaccion.getEntrega() != null) {

             Double valorAbsoluto = Math.abs(transaccion.getImporte());

            modelo.put("entrega", entregaServicio.buscarEntrega(transaccion.getEntrega().getId()));
            modelo.put("importe", valorAbsoluto);

            return "transaccion_entregaAdmin.html";

        }
        
        else {

            Gasto gasto = gastoServicio.buscarGasto(transaccion.getGasto().getId());
            ArrayList<Detalle> lista = new ArrayList();
            Flete flete = fleteServicio.buscarFleteIdGasto(gasto.getId());

            if (!gasto.getNombre().startsWith("GASTO FTE")) {
                lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(gasto.getId());
            } else {
                lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(gasto.getId());
            }

            modelo.put("idFlete", flete.getId());
            modelo.put("gasto", gasto);
            modelo.addAttribute("detalles", lista);

            return "transaccion_gastoAdmin.html";

        } 

    }
    
    @GetMapping("/mostrarTransaccionDesdeCuenta/{id}")
    public String mostrarTransaccionChoferAdminDesdeCuenta(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);
        Long idCuenta = cuentaServicio.buscarIdCuentaChofer(transaccion.getChofer().getId());

        if (transaccion.getFlete() != null) {

            modelo.put("flete", fleteServicio.buscarFlete(transaccion.getFlete().getId()));
            modelo.put("idCuenta", idCuenta);

            return "transaccion_fleteCuenta.html";

        }
        
        if (transaccion.getEntrega() != null) {

             Double valorAbsoluto = Math.abs(transaccion.getImporte());

            modelo.put("entrega", entregaServicio.buscarEntrega(transaccion.getEntrega().getId()));
            modelo.put("importe", valorAbsoluto);
            modelo.put("idCuenta", idCuenta);

            return "transaccion_entregaCuenta.html";

        }
        
        else {

            Gasto gasto = gastoServicio.buscarGasto(transaccion.getGasto().getId());
            ArrayList<Detalle> lista = new ArrayList();
            Flete flete = fleteServicio.buscarFleteIdGasto(gasto.getId());

            if (!gasto.getNombre().startsWith("GASTO FTE")) {
                lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(gasto.getId());
            } else {
                lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(gasto.getId());
            }

            modelo.put("idFlete", flete.getId());
            modelo.put("gasto", gasto);
            modelo.addAttribute("detalles", lista);
            modelo.put("idCuenta", idCuenta);

            return "transaccion_gastoCuenta.html";

        } 

    }

    @GetMapping("/mostrarTransaccionChofer/{id}")
    public String mostrarTransaccionChofer(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getFlete() != null) {

            modelo.put("flete", fleteServicio.buscarFlete(transaccion.getFlete().getId()));

            return "transaccion_fleteChofer.html";

        }
        
        if (transaccion.getEntrega() != null) {

             Double valorAbsoluto = Math.abs(transaccion.getImporte());

            modelo.put("entrega", entregaServicio.buscarEntrega(transaccion.getEntrega().getId()));
            modelo.put("importe", valorAbsoluto);

            return "transaccion_entrega.html";

        }
        
        else {

            Gasto gasto = gastoServicio.buscarGasto(transaccion.getGasto().getId());
            ArrayList<Detalle> lista = new ArrayList();

            if (!gasto.getNombre().startsWith("GASTO FTE")) {
                lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(gasto.getId());
            } else {
                lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFleteIdGasto(gasto.getId());
            }

            modelo.put("gasto", gasto);
            modelo.addAttribute("detalles", lista);

            return "transaccion_gasto.html";

        } 

    }
    
    @PostMapping("/exportarFiltro")
    public String exportarFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {  
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);
        
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_exportarFiltro.html";
  
    }
    
    @PostMapping("/exportarTodoAdmin") 
    public String exportarTodoAdmin(@RequestParam Long id, ModelMap modelo) throws ParseException {
            
        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta));

        return "cuenta_exportarTodoAdmin.html";         
  
    }
    
    @PostMapping("/exportaTodoAdmin")
    public void exportaTodoAdmin(@RequestParam Long id, HttpServletResponse response) throws IOException, ParseException {
  
        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Transaccion> myObjects = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCuenta(htmlContent, response, cuenta.getChofer().getNombre(), cuenta.getSaldo() );

    }
    
    @PostMapping("/exportarFiltroAdmin")
    public String exportarFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {  
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);
        
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_exportarFiltroAdmin.html";
  
    }
    
    @PostMapping("/exportarIdFiltroAdmin")
    public String exportarIdFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {  
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);
        
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_exportarIdFiltroAdmin.html";
  
    }
    
    @PostMapping("/exportaFiltroAdmin")
    public void exportaFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {
  
        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);
        
        ArrayList<Transaccion> myObjects = lista;
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCuentaMovimiento(htmlContent, response, cuenta.getChofer().getNombre(), desde, hasta);

    }
    
    @PostMapping("/exportarTodoCliente")
    public String exportarTodoCliente(@RequestParam Long id, ModelMap modelo) throws ParseException {
            
        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();

        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta));

        return "cuenta_exportarTodoCliente.html";         
  
    }
    
    @PostMapping("/exportaTodoCliente")
    public void exportaTodoCliente(@RequestParam Long id, HttpServletResponse response) throws IOException, ParseException {
  
        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String desde = obtenerFechaDesde();
        String hasta = obtenerFechaHasta();
        
        ArrayList<Transaccion> myObjects = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCuenta(htmlContent, response, cuenta.getCliente().getNombre(), cuenta.getSaldo() );

    }
    
    @PostMapping("/exportarFiltroCliente")
    public String exportarFiltroCliente(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {  
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);
        
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_exportarFiltroCliente.html";
  
    }
    
    @PostMapping("/exportarIdFiltroCliente")
    public String exportarIdFiltroCliente(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {  
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);
        
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_exportarIdFiltroCliente.html";
  
    }
    
    @PostMapping("/exportaFiltroCliente")
    public void exportaFiltroCliente(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {
  
        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFechaFiltro(id, desde, hasta);
        
        ArrayList<Transaccion> myObjects = lista;
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCuentaMovimiento(htmlContent, response, cuenta.getCliente().getNombre(), desde, hasta);

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

    public String convertirNumeroMiles(Double num) {  

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
