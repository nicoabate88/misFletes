package abate.abate.servicios;

import abate.abate.entidades.Detalle;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.GastoRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GastoServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private TransaccionServicio transaccionServicio;
    @Autowired
    private GastoRepositorio gastoRepositorio;
    @Autowired
    private DetalleServicio detalleServicio;
    @Autowired
    private FleteRepositorio fleteRepositorio;

    @Transactional
    public void crearGasto(Long idFlete, Long idUsuario) throws ParseException {

        Usuario user = new Usuario();
        Optional<Usuario> u = usuarioRepositorio.findById(idUsuario);
        if (u.isPresent()) {
            user = u.get();
        }

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFlete(idFlete);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        String nombre = "GASTO FTE ID " + idFlete;
        Gasto gasto = new Gasto();

        gasto.setChofer(flete.getChofer());
        gasto.setFecha(flete.getFechaFlete());
        gasto.setNombre(nombre);
        gasto.setImporte(importe);
        gasto.setUsuario(user);
        gasto.setCamion(flete.getCamion());

        gastoRepositorio.save(gasto);

        Long idGasto = buscarUltimo();
        flete.setGasto(gastoRepositorio.getById(idGasto));
        fleteRepositorio.save(flete);   //persiste Gasto en el flete correspondiente

    }

    @Transactional
    public void modificarGasto(Long idFlete, Long idGasto, Long idUsuario) throws ParseException {

        Usuario user = new Usuario();
        Optional<Usuario> u = usuarioRepositorio.findById(idUsuario);
        if (u.isPresent()) {
            user = u.get();
        }

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFlete(idFlete);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        gasto.setImporte(importe);
        gasto.setUsuario(user);

        gastoRepositorio.save(gasto);

        if (user.getRol().equalsIgnoreCase("ADMIN") && flete.getEstado().equalsIgnoreCase("ACEPTADO")) {   //si el Rol de Usuario es Admin ya persiste en gasto en cuenta de chofer

            transaccionServicio.modificarTransaccionGasto(gasto.getId());

        }

    }

    @Transactional
    public void eliminarGasto(Long idFlete, Long idGasto, Long idUsuario) {

        Usuario user = new Usuario();
        Optional<Usuario> u = usuarioRepositorio.findById(idUsuario);
        if (u.isPresent()) {
            user = u.get();
        }

        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesFlete(idFlete);
        for (Detalle d : lista) {
            detalleServicio.eliminarDetalle(d.getId());
        }

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        if (flete.getEstado().equalsIgnoreCase("ACEPTADO")) {
            transaccionServicio.eliminarTransaccionGasto(idGasto);
        }

        flete.setGasto(null);

        fleteRepositorio.save(flete);

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        gasto.setNombre("ELIMINADO");
        gasto.setUsuario(user);
        gasto.setImporte(0.0);

        gastoRepositorio.save(gasto);

    }

    public Long buscarUltimo() {

        return gastoRepositorio.ultimoGasto();
    }

    public Gasto buscarGasto(Long id) {

        return gastoRepositorio.getById(id);
    }

    public Gasto buscarGastoIdImagen(Long id) {

        return gastoRepositorio.buscarGastoIdImagen(id);
    }

    public Date convertirFecha(String fecha) throws ParseException { 
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }
}
