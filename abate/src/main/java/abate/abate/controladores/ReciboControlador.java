package abate.abate.controladores;

import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Recibo;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.ClienteServicio;
import abate.abate.servicios.CuentaServicio;
import abate.abate.servicios.ReciboServicio;
import java.text.DecimalFormat;
import java.text.ParseException;
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
@RequestMapping("/recibo")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class ReciboControlador {

    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private ReciboServicio reciboServicio;
    @Autowired
    private CuentaServicio cuentaServicio;

    @GetMapping("/registrar")
    public String registrarRecibo(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("cuentas", cuentaServicio.buscarCuentasCliente(logueado.getIdOrg()));

        return "recibo_registrar.html";
    }

    @GetMapping("/registrarId/{id}")
    public String registrarReciboId(@PathVariable Long id, ModelMap modelo) {
        
        Cuenta cuenta = cuentaServicio.buscarCuentaCliente(id);
        String saldo = convertirNumeroMiles(cuenta.getSaldo());
        
        modelo.put("cuenta", cuenta);
        modelo.put("saldo", saldo);
        
        return "recibo_registrarId.html";
    }

    @PostMapping("/registro")
    public String registroUsuario(@RequestParam Long idCliente, @RequestParam String fecha,
            @RequestParam Double importe, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        reciboServicio.crearRecibo(logueado.getIdOrg(), idCliente, fecha, importe, observacion, logueado.getId());
        Long id = reciboServicio.buscarUltimo();
        Recibo recibo = reciboServicio.buscarRecibo(id);
        String total = convertirNumeroMiles(recibo.getImporte());

        modelo.put("recibo", recibo);
        modelo.put("importe", total);
        modelo.put("fecha", fecha);
        modelo.put("exito", "Recibo REGISTRADO con éxito");

        return "recibo_registrado.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.addAttribute("recibos", reciboServicio.buscarRecibos(logueado.getIdOrg()));

        return "recibo_listar.html";
    }

    @GetMapping("/listarIdCliente/{id}")
    public String listarIdCliente(@PathVariable Long id, ModelMap modelo) {

        modelo.addAttribute("recibos", reciboServicio.buscarRecibosIdCliente(id));
        modelo.put("cliente", clienteServicio.buscarCliente(id));

        return "recibo_listarCliente.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo, HttpSession session) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        modelo.put("recibo", reciboServicio.buscarRecibo(id));
        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc(logueado.getIdOrg()));

        return "recibo_modificar.html";

    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam Long idCliente, @RequestParam String fecha,
            @RequestParam Double importe, @RequestParam(required = false) String observacion,
            ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        reciboServicio.modificarRecibo(id, idCliente, fecha, importe, observacion, logueado.getId());

        Recibo recibo = reciboServicio.buscarRecibo(id);
        String total = convertirNumeroMiles(recibo.getImporte());

        modelo.put("recibo", recibo);
        modelo.put("importe", total);
        modelo.put("fecha", fecha);
        modelo.put("exito", "Recibo MODIFICADO con éxito");

        return "recibo_registrado.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("recibo", reciboServicio.buscarRecibo(id));

        return "recibo_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        reciboServicio.eliminarRecibo(id);

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String nombreMayuscula = logueado.getUsuario().toUpperCase();
        
        modelo.put("usuario", nombreMayuscula);
        modelo.put("id", logueado.getId());
        modelo.put("exito", "Recibo ELIMINADO con éxito");

        return "index_admin.html";

    }

    private String convertirNumeroMiles(Double num) {   

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }

}
