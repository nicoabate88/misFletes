package abate.abate.servicios;

import abate.abate.entidades.Cliente;
import abate.abate.entidades.Transaccion;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.ClienteRepositorio;
import abate.abate.repositorios.TransaccionRepositorio;
import abate.abate.util.ClienteComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;
    @Autowired
    private CuentaServicio cuentaServicio;

    @Transactional
    public void crearCliente(String nombre, Long cuit, String localidad, String direccion, Long telefono, String email) throws MiException {

        validarDatos(nombre);

        Cliente cliente = new Cliente();

        String nombreMayusculas = nombre.toUpperCase();
        String localidadMayusculas = localidad.toUpperCase();
        String direccionMayusculas = direccion.toUpperCase();

        cliente.setNombre(nombreMayusculas);
        cliente.setCuit(cuit);
        cliente.setLocalidad(localidadMayusculas);
        cliente.setDireccion(direccionMayusculas);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);

        clienteRepositorio.save(cliente);

        cuentaServicio.crearCuentaCliente(buscarUltimo());

    }

    @Transactional
    public void modificarCliente(Long id, String nombre, Long cuit, String localidad, String direccion, Long telefono, String email) {

        Cliente cliente = new Cliente();

        Optional<Cliente> cte = clienteRepositorio.findById(id);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        String nombreMayusculas = nombre.toUpperCase();
        String localidadMayusculas = localidad.toUpperCase();
        String direccionMayusculas = direccion.toUpperCase();

        cliente.setNombre(nombreMayusculas);
        cliente.setCuit(cuit);
        cliente.setLocalidad(localidadMayusculas);
        cliente.setDireccion(direccionMayusculas);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);

        clienteRepositorio.save(cliente);

    }

    @Transactional
    public void eliminarCliente(Long id) throws MiException {
        
        Cliente cliente = clienteRepositorio.getById(id);
        
        Transaccion transaccion = transaccionRepositorio.findTopByClienteOrderByIdDesc(cliente);

        if (transaccion == null) {

            clienteRepositorio.deleteById(id);

            cuentaServicio.eliminarCuentaCliente(id);

        } else {

            throw new MiException("El Cliente no puede ser eliminado, tiene Flete y/o Recibo asociado");
        }

    }

    public Cliente buscarCliente(Long id) {

        return clienteRepositorio.getById(id);
    }

    public ArrayList<Cliente> buscarClientesNombreAsc() {

        ArrayList<Cliente> lista = (ArrayList<Cliente>) clienteRepositorio.findAll();

        Collections.sort(lista, ClienteComparador.ordenarNombreAsc); //ordena por nombre alfabetico los nombres de clientes

        return lista;

    }

    public Long buscarUltimo() {

        return clienteRepositorio.ultimoCliente();

    }

    public void validarDatos(String nombre) throws MiException {

        ArrayList<Cliente> lista = (ArrayList<Cliente>) clienteRepositorio.findAll();

        for (Cliente c : lista) {
            if (c.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El NOMBRE de Cliente ya está registrado");
            }
        }
    }

}
