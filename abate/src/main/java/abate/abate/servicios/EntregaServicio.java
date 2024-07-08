package abate.abate.servicios;

import abate.abate.entidades.Entrega;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.UsuarioRepositorio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import abate.abate.repositorios.EntregaRepositorio;
import abate.abate.util.EntregaComparador;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class EntregaServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private EntregaRepositorio entregaRepositorio;
    @Autowired
    private TransaccionServicio transaccionServicio;

    @Transactional
    public void crearEntrega(Long idChofer, String fecha, Double importe, String observacion, Long idUsuario) throws ParseException {

        Usuario chofer = new Usuario();
        Optional<Usuario> chof = usuarioRepositorio.findById(idChofer);
        if (chof.isPresent()) {
            chofer = chof.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);

        Entrega entrega = new Entrega();

        entrega.setChofer(chofer);
        entrega.setFecha(f);
        entrega.setObservacion(obsMayusculas);
        entrega.setImporte(importe);
        entrega.setUsuario(usuario);

        entregaRepositorio.save(entrega);

        transaccionServicio.crearTransaccionEntrega(buscarUltimo());

    }

    @Transactional
    public void modificarEntrega(Long idEntrega, Long idChofer, String fecha, Double importe, String observacion, Long idUsuario) throws ParseException { //modificar Cliente u observacion de Recibo

        Entrega entrega = new Entrega();
        Optional<Entrega> ent = entregaRepositorio.findById(idEntrega);
        if (ent.isPresent()) {
            entrega = ent.get();
        }

        Usuario chofer = new Usuario();
        Optional<Usuario> chof = usuarioRepositorio.findById(idChofer);
        if (chof.isPresent()) {
            chofer = chof.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        String obsMayusculas = observacion.toUpperCase();
        Date f = convertirFecha(fecha);

        entrega.setChofer(chofer);
        entrega.setFecha(f);
        entrega.setImporte(importe);
        entrega.setObservacion(obsMayusculas);
        entrega.setUsuario(usuario);

        entregaRepositorio.save(entrega);

        transaccionServicio.modificarTransaccionEntrega(idEntrega);

    }

    @Transactional
    public void eliminarEntrega(Long idEntrega) {

        Entrega entrega = new Entrega();
        Optional<Entrega> ent = entregaRepositorio.findById(idEntrega);
        if (ent.isPresent()) {
            entrega = ent.get();
        }

        transaccionServicio.eliminarTransaccionEntrega(idEntrega);

        entrega.setChofer(null);
        entrega.setImporte(0.0);
        entrega.setObservacion("ELIMINADO");
        entrega.setUsuario(null);

        entregaRepositorio.save(entrega);

    }

    public Long buscarUltimo() {

        return entregaRepositorio.ultimoEntrega();
    }

    public Entrega buscarEntrega(Long id) {

        return entregaRepositorio.getById(id);
    }

    public ArrayList<Entrega> buscarEntregas() {

        ArrayList<Entrega> lista = entregaRepositorio.buscarEntregas();

        Collections.sort(lista, EntregaComparador.ordenarFechaDesc);

        return lista;

    }

    public ArrayList<Entrega> buscarEntregasIdChofer(Long id) {

        ArrayList<Entrega> lista = entregaRepositorio.buscarEntregasIdChofer(id);

        Collections.sort(lista, EntregaComparador.ordenarFechaDesc);

        return lista;

    }

    public Date convertirFecha(String fecha) throws ParseException { 
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
