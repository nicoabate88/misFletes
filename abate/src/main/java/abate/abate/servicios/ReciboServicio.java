package abate.abate.servicios;

import abate.abate.entidades.Cliente;
import abate.abate.entidades.Recibo;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.ClienteRepositorio;
import abate.abate.repositorios.ReciboRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.ReciboComparador;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReciboServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ReciboRepositorio reciboRepositorio;
    @Autowired
    private TransaccionServicio transaccionServicio;

    @Transactional
    public void crearRecibo(Long idCliente, String fecha, Double importe, String observacion, Long idUsuario) throws ParseException {

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);

        Recibo recibo = new Recibo();

        recibo.setCliente(cliente);
        recibo.setFecha(f);
        recibo.setObservacion(obsMayusculas);
        recibo.setImporte(importe);
        recibo.setUsuario(usuario);

        reciboRepositorio.save(recibo);

        transaccionServicio.crearTransaccionRecibo(buscarUltimo());

    }

    @Transactional
    public void modificarRecibo(Long idRecibo, Long idCliente, String fecha, Double importe, String observacion, Long idUsuario) throws ParseException { //modificar Cliente u observacion de Recibo

        Recibo recibo = new Recibo();
        Optional<Recibo> rec = reciboRepositorio.findById(idRecibo);
        if (rec.isPresent()) {
            recibo = rec.get();
        }

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);

        recibo.setCliente(cliente);
        recibo.setFecha(f);
        recibo.setImporte(importe);
        recibo.setObservacion(obsMayusculas);
        recibo.setUsuario(usuario);

        reciboRepositorio.save(recibo);

        transaccionServicio.modificarTransaccionRecibo(idRecibo);

    }

    @Transactional
    public void eliminarRecibo(Long idRecibo) {

        Recibo recibo = new Recibo();
        Optional<Recibo> rec = reciboRepositorio.findById(idRecibo);
        if (rec.isPresent()) {
            recibo = rec.get();
        }

        transaccionServicio.eliminarTransaccionRecibo(idRecibo);

        recibo.setCliente(null);
        recibo.setImporte(0.0);
        recibo.setObservacion("ELIMINADO");
        recibo.setUsuario(null);

        reciboRepositorio.save(recibo);

    }

    public ArrayList<Recibo> buscarRecibosIdCliente(Long id) {

        ArrayList<Recibo> lista = reciboRepositorio.buscarRecibosIdCliente(id);

        Collections.sort(lista, ReciboComparador.ordenarFechaDesc); //ordena por nombre alfabetico los nombres de clientes

        return lista;

    }

    public ArrayList<Recibo> buscarRecibos() {

        ArrayList<Recibo> lista = reciboRepositorio.buscarRecibos();

        Collections.sort(lista, ReciboComparador.ordenarFechaDesc); //ordena por nombre alfabetico los nombres de clientes

        return lista;

    }

    public Long buscarUltimo() {

        return reciboRepositorio.ultimoRecibo();
    }

    public Recibo buscarRecibo(Long id) {

        return reciboRepositorio.getById(id);
    }

    public Date convertirFecha(String fecha) throws ParseException { 
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
