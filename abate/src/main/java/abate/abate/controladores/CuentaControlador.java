package abate.abate.controladores;

import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CuentaServicio;
import abate.abate.servicios.EntregaServicio;
import abate.abate.servicios.ExcelServicio;
import abate.abate.servicios.FleteServicio;
import abate.abate.servicios.GastoServicio;
import abate.abate.servicios.ReciboServicio;
import abate.abate.servicios.TransaccionServicio;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
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
    public String mostrarChofer(@PathVariable Long id, ModelMap modelo) {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuenta(id);
        Boolean flag = true;
        if(lista.isEmpty()){
            flag = false;
        }
        
        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarChoferAdmin.html";

    }
    
    @PostMapping("/mostrarChoferFiltroAdmin")
    public String mostrarChoferFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{
        
        String saldo = "0.0";
        Boolean flag = true;
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        if (!lista.isEmpty()) {
          Transaccion primeraTransaccion = lista.get(0);
          saldo = convertirNumeroMiles(primeraTransaccion.getSaldoAcumulado());
        }

        if(lista.isEmpty()){
            flag = false;
        }
        
        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_mostrarChoferFiltroAdmin.html";
        
    }
    
     @PostMapping("/mostrarIdChoferFiltroAdmin")
    public String mostrarIdChoferFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{
        
        String saldo = "0.0";
        Boolean flag = true;
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        if (!lista.isEmpty()) {
          Transaccion primeraTransaccion = lista.get(0);
          saldo = convertirNumeroMiles(primeraTransaccion.getSaldoAcumulado());
        }

        if(lista.isEmpty()){
            flag = false;
        }
        
        modelo.put("flag", flag);
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_mostrarIdChoferFiltroAdmin.html";
        
    }
    
    @PostMapping("/mostrarChoferFiltro")
    public String mostrarChoferFiltro(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException{
        
        String saldo = "0.0";
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        if (!lista.isEmpty()) {
          Transaccion primeraTransaccion = lista.get(0);
          saldo = convertirNumeroMiles(primeraTransaccion.getSaldoAcumulado());
        }
        
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta));
        
        return "cuenta_mostrarChoferFiltro.html";
    }   

    @GetMapping("/mostrarIdChofer/{id}")
    public String mostrarIdChofer(@PathVariable Long id, ModelMap modelo) {

        Long idCuenta = cuentaServicio.buscarIdCuentaChofer(id);
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());

        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuenta(idCuenta));

        return "cuenta_mostrarChofer.html";

    }
    
    @GetMapping("/mostrarIdChoferAdmin/{id}")
    public String mostrarIdChoferAdmin(@PathVariable Long id, ModelMap modelo) {

        Long idCuenta = cuentaServicio.buscarIdCuentaChofer(id);
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuenta(idCuenta);
        Boolean flag = true;
        if(lista.isEmpty()){
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarIdChoferAdmin.html";

    }

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

    @GetMapping("/mostrarCliente/{id}")
    public String mostrarCliente(@PathVariable Long id, ModelMap modelo) {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuenta(id);
        Boolean flag = true;
        if(lista.isEmpty()){
            flag = false;
        }

        modelo.put("flag", flag);
        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.addAttribute("transacciones", lista);

        return "cuenta_mostrarCliente.html";

    }
    

    @GetMapping("/mostrarIdCliente/{id}")
    public String mostrarIdCliente(@PathVariable Long id, ModelMap modelo) {

        Long idCuenta = cuentaServicio.buscarIdCuentaCliente(id);
        Cuenta cuenta = cuentaServicio.buscarCuenta(idCuenta);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());

        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuenta(idCuenta));

        return "cuenta_mostrarCliente.html";

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

    @GetMapping("/mostrarTransaccionChofer/{id}")
    public String mostrarTransaccionChofer(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);

        if (transaccion.getFlete() != null) {

            modelo.put("flete", fleteServicio.buscarFlete(transaccion.getFlete().getId()));

            return "transaccion_fleteChofer.html";

        }
        if (transaccion.getGasto() != null) {

            modelo.put("gasto", gastoServicio.buscarGasto(transaccion.getGasto().getId()));

            return "transaccion_gasto.html";

        } else {

            Double valorAbsoluto = Math.abs(transaccion.getImporte());

            modelo.put("entrega", entregaServicio.buscarEntrega(transaccion.getEntrega().getId()));
            modelo.put("importe", valorAbsoluto);

            return "transaccion_entrega.html";
        }

    }
    
    @PostMapping("/exportarTodoAdmin")
    public String exportarTodoAdmin(@RequestParam Long id, ModelMap modelo) {
            
        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());

        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuenta(id));

        return "cuenta_exportarTodoAdmin.html";         
  
    }
    
    @PostMapping("/exportaTodoAdmin")
    public void exportaTodoAdmin(@RequestParam Long id, HttpServletResponse response) throws IOException, ParseException {
  
        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        
        ArrayList<Transaccion> myObjects = transaccionServicio.buscarTransaccionIdCuenta(id);
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCuenta(htmlContent, response, cuenta.getChofer().getNombre(), cuenta.getSaldo() );

    }
    
    @PostMapping("/exportarFiltroAdmin")
    public String exportarFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {  
        
        String saldo = "0.0";
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        if (!lista.isEmpty()) {
          Transaccion primeraTransaccion = lista.get(0);
          saldo = convertirNumeroMiles(primeraTransaccion.getSaldoAcumulado());
        }
        
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_exportarFiltroAdmin.html";
  
    }
    
    @PostMapping("/exportarIdFiltroAdmin")
    public String exportarIdFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, ModelMap modelo) throws ParseException {  
        
        String saldo = "0.0";
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        if (!lista.isEmpty()) {
          Transaccion primeraTransaccion = lista.get(0);
          saldo = convertirNumeroMiles(primeraTransaccion.getSaldoAcumulado());
        }
        
        modelo.put("cuenta", cuentaServicio.buscarCuenta(id));
        modelo.put("saldo", saldo);
        modelo.put("desde", desde);
        modelo.put("hasta", hasta);
        modelo.addAttribute("transacciones", lista);
        
        return "cuenta_exportarIdFiltroAdmin.html";
  
    }
    
    @PostMapping("/exportaFiltroAdmin")
    public void exportaFiltroAdmin(@RequestParam Long id, @RequestParam String desde, @RequestParam String hasta, HttpServletResponse response) throws IOException, ParseException {
  
        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        Double saldo = 0.0;
        
        ArrayList<Transaccion> lista = transaccionServicio.buscarTransaccionIdCuentaFecha(id, desde, hasta);
        if (!lista.isEmpty()) {
          Transaccion primeraTransaccion = lista.get(0);
          saldo = primeraTransaccion.getSaldoAcumulado();
        }
        
        ArrayList<Transaccion> myObjects = lista;
        String htmlContent = generateHtmlFromObjects(myObjects);
        excelServicio.exportHtmlToExcelCuenta(htmlContent, response, cuenta.getChofer().getNombre(), saldo);

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
    
    

}
