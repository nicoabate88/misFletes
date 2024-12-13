package abate.abate.servicios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Detalle;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Transaccion;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.GastoRepositorio;
import abate.abate.repositorios.TransaccionRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.GastoComparador;
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
    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;

    @Transactional
    public void crearGasto(Long idOrg, Long idFlete, Long idUsuario) throws ParseException {

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

        String nombre = "GASTO FTE ID" + flete.getIdFlete();
        Gasto gasto = new Gasto();
        
        gasto.setIdOrg(idOrg);
        gasto.setChofer(flete.getChofer());
        gasto.setFecha(flete.getFechaFlete());
        gasto.setNombre(nombre);
        gasto.setImporte(importe);
        gasto.setUsuario(user);
        gasto.setCamion(flete.getCamion());
        if(user.getRol().equalsIgnoreCase("ADMIN")){
        gasto.setEstado("ACEPTADO");
        } else {
            gasto.setEstado("FLETE");
        }

        gastoRepositorio.save(gasto);

        flete.setGasto(gasto);
        fleteRepositorio.save(flete);   //persiste Gasto en el flete correspondiente
        
        if(gasto.getEstado().equalsIgnoreCase("ACEPTADO")){
           transaccionServicio.crearTransaccionGasto(gasto.getId()); 
        }

    }
    
    @Transactional
    public Long crearGastoCaja(Long idOrg, String fecha, Long idCamion, Long idChofer, Long idUsuario) throws ParseException {

        Usuario chofer = usuarioRepositorio.getById(idChofer);
        Usuario usuario = usuarioRepositorio.getById(idUsuario);
        Camion camion = camionRepositorio.getById(idCamion);
        
        Date f = convertirFecha(fecha);
        Long idGasto = buscarUltimoIdOrg(idOrg);
        
        Gasto gasto = new Gasto();
        
        gasto.setChofer(chofer);
        gasto.setUsuario(usuario);
        gasto.setIdOrg(idOrg);
        gasto.setCamion(camion);
        gasto.setFecha(f);
        gasto.setIdGasto(idGasto+1);
        if(usuario.getRol().equalsIgnoreCase("ADMIN")){
        gasto.setEstado("ACEPTADO");
        } else {
            gasto.setEstado("PENDIENTE");
        }

        gastoRepositorio.save(gasto);

        return buscarUltimo(idOrg);
        
    }
    
    @Transactional
    public void crearModificarGastoCaja(Long idGasto) throws ParseException {

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        gasto.setImporte(importe);
        gasto.setNombre("GASTO ID"+gasto.getIdGasto());

        gastoRepositorio.save(gasto);
        
        transaccionServicio.crearTransaccionGasto(idGasto);

    }
    
    @Transactional
    public void modificarGastoCaja(Long idGasto, Long idUsuario, String fecha, Long idCamion) throws ParseException {

        Usuario user = new Usuario();
        Optional<Usuario> u = usuarioRepositorio.findById(idUsuario);
        if (u.isPresent()) {
            user = u.get();
        }

        Double importe = 0.0;
        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
        for (Detalle d : lista) {
            importe = importe + d.getTotal();
        }

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }
        
        Camion camion = camionRepositorio.getById(idCamion);
        Date f = convertirFecha(fecha);
        
        gasto.setImporte(importe);
        gasto.setUsuario(user);
        gasto.setFecha(f);
        gasto.setCamion(camion);

        gastoRepositorio.save(gasto);

        transaccionServicio.modificarTransaccionGasto(idGasto);
    }
    
    @Transactional
    public void modificarGastoFleteDesdeCaja(Long idFlete, Long idGasto, Long idUsuario, String fecha, Long idCamion) throws ParseException {

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
        Camion camion = camionRepositorio.getById(idCamion);
        Date f = convertirFecha(fecha);

        gasto.setImporte(importe);
        gasto.setUsuario(user);
        gasto.setFecha(f);
        gasto.setCamion(camion);

        gastoRepositorio.save(gasto);

        transaccionServicio.modificarTransaccionGasto(gasto.getId());

    }
    
    @Transactional
    public void aceptarGastoCaja(Long idGasto, Usuario logueado) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        gasto.setEstado("ACEPTADO");
        gasto.setUsuario(logueado);

        gastoRepositorio.save(gasto);

    }
    
    @Transactional
    public void volverPendienteGasto(Long idGasto, Usuario logueado) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }
        gasto.setEstado("PENDIENTE");
        gasto.setUsuario(logueado);

        gastoRepositorio.save(gasto);

    }
    
    @Transactional
    public void eliminarGastoCaja(Long idGasto, Long idUsuario) {

        Usuario user = new Usuario();
        Optional<Usuario> u = usuarioRepositorio.findById(idUsuario);
        if (u.isPresent()) {
            user = u.get();
        }

        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
        for (Detalle d : lista) {
            detalleServicio.eliminarDetalle(d.getId());
        }

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        gasto.setNombre("ELIMINADO");
        gasto.setEstado("ELIMINADO");
        gasto.setUsuario(user);
        gasto.setImporte(0.0);
        gasto.setIdOrg(null);
        gasto.setIdGasto(null);
        
        gastoRepositorio.save(gasto);
        
        transaccionServicio.eliminarTransaccionGasto(idGasto);

    }
    
    @Transactional
    public void crearEliminarGastoCaja(Long idGasto, Long idUsuario) {

        Usuario user = new Usuario();
        Optional<Usuario> u = usuarioRepositorio.findById(idUsuario);
        if (u.isPresent()) {
            user = u.get();
        }

        ArrayList<Detalle> lista = (ArrayList<Detalle>) detalleServicio.buscarDetallesGasto(idGasto);
        for (Detalle d : lista) {
            detalleServicio.eliminarDetalle(d.getId());
        }

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        gasto.setNombre("ELIMINADO");
        gasto.setEstado("ELIMINADO");
        gasto.setUsuario(user);
        gasto.setImporte(0.0);
        gasto.setIdOrg(null);
        gasto.setIdGasto(null);
        
        gastoRepositorio.save(gasto);

    }

    @Transactional
    public void modificarGasto(Long idFlete, Long idGasto, Long idUsuario, String fecha, Long idCamion) throws ParseException {

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

        Camion camion = camionRepositorio.getById(idCamion);
        Date f = convertirFecha(fecha);
        
        gasto.setImporte(importe);
        gasto.setUsuario(user);
        gasto.setFecha(f);
        gasto.setCamion(camion);

        gastoRepositorio.save(gasto);

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdGasto(idGasto);
        if(transaccion != null){
            transaccionServicio.modificarTransaccionGasto(gasto.getId());
        } 
    }
    
    @Transactional
    public void modificarChoferGasto(Gasto gasto, Usuario chofer){
        
        gasto.setChofer(chofer);
        
        gastoRepositorio.save(gasto);
        
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
        gasto.setEstado("ELIMINADO");
        gasto.setUsuario(user);
        gasto.setImporte(0.0);

        gastoRepositorio.save(gasto);

    }

    public Long buscarUltimo(Long idOrg) {

        return gastoRepositorio.ultimoGasto(idOrg);
    }

    public Gasto buscarGasto(Long id) {

        return gastoRepositorio.getById(id);
    }

    public Gasto buscarGastoIdImagen(Long id) {

        return gastoRepositorio.buscarGastoIdImagen(id);
    }
    
     public Long buscarUltimoIdOrg(Long idOrg) {
        
        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findTopByIdOrgAndNombreNotOrderByIdGastoDesc(idOrg, "ELIMINADO");
        if (gto.isPresent() && gto.get().getIdGasto() != null) {
            gasto = gto.get();
            
            return gasto.getIdGasto();
            
        } else {
            
            int ultimo = 0;
            Long primero = Long.valueOf(ultimo);

            return primero;
            
        }

    }
     
    public ArrayList<Gasto> buscarGastosCamion(Long idCamion, String desde, String hasta) throws ParseException {
        
        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Gasto> lista = gastoRepositorio.findByFechaBetweenAndEstadoNotAndCamionId(d, h, "ELIMINADO", idCamion);

        Collections.sort(lista, GastoComparador.ordenarFechaDesc);

        return lista;

    } 

    public Date convertirFecha(String fecha) throws ParseException { 
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }
}
