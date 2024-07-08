package abate.abate.controladores;

import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.servicios.ClienteServicio;
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
@RequestMapping("/cliente")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class ClienteControlador {

    @Autowired
    private ClienteServicio clienteServicio;

    @GetMapping("/registrar")
    public String registrarCliente() {

        return "cliente_registrar.html";
    }

    @PostMapping("/registro")
    public String registroCliente(@RequestParam String nombre, @RequestParam(required = false) Long cuit,
            @RequestParam(required = false) String localidad, @RequestParam(required = false) String direccion,
            @RequestParam(required = false) Long telefono, @RequestParam(required = false) String email, ModelMap modelo) {

        try {

            clienteServicio.crearCliente(nombre, cuit, localidad, direccion, telefono, email);
            Long id = clienteServicio.buscarUltimo();
            modelo.put("cliente", clienteServicio.buscarCliente(id));
            modelo.put("exito", "Cliente REGISTRADO con éxito");

            return "cliente_mostrar.html";

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.put("cuit", cuit);
            modelo.put("localidad", localidad);
            modelo.put("direccion", direccion);
            modelo.put("telefono", telefono);
            modelo.put("email", email);
            modelo.put("error", ex.getMessage());

            return "cliente_registrar.html";
        }
    }

    @GetMapping("/listar")
    public String listarClientes(ModelMap modelo) {

        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());

        return "cliente_listar.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cliente", clienteServicio.buscarCliente(id));

        return "cliente_modificar.html";

    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam(required = false) Long cuit,
            @RequestParam(required = false) String localidad, @RequestParam(required = false) String direccion, 
            @RequestParam(required = false) Long telefono, @RequestParam(required = false) String email, ModelMap modelo) {

        clienteServicio.modificarCliente(id, nombre, cuit, localidad, direccion, telefono, email);

        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.put("exito", "Cliente MODIFICADO con éxito");

        return "cliente_mostrar.html";

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cliente", clienteServicio.buscarCliente(id));

        return "cliente_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        try {

            clienteServicio.eliminarCliente(id);
            String nombreMayuscula = logueado.getUsuario().toUpperCase();
        
            modelo.put("usuario", nombreMayuscula);
            modelo.put("id", logueado.getId());
            modelo.put("exito", "Cliente ELIMINADO con éxito");

            return "index_admin.html";

        } catch (MiException ex) {

            modelo.put("cliente", clienteServicio.buscarCliente(id));
            modelo.put("error", ex.getMessage());

            return "cliente_eliminar.html";
        }
    }

}
