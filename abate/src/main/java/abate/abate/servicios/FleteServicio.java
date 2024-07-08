package abate.abate.servicios;

import abate.abate.entidades.Cliente;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.ClienteRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.FleteComparador;
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
public class FleteServicio {

    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private TransaccionServicio transaccionServicio;
    @Autowired
    private GastoServicio gastoServicio;

    @Transactional
    public void crearFleteChofer(String fechaCarga, Long idCliente, String origen, String fechaViaje, String destino, Double km,
            String tipoCereal, Double tarifa, String cPorte, String ctg, Double kg, Long idChofer) throws ParseException {

        Flete flete = new Flete();

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        Usuario chofer = new Usuario();
        Optional<Usuario> chof = usuarioRepositorio.findById(idChofer);
        if (chof.isPresent()) {
            chofer = chof.get();
        }

        String origenM = origen.toUpperCase();
        String destinoM = destino.toUpperCase();
        Date carga = convertirFecha(fechaCarga);
        Date viaje = convertirFecha(fechaViaje);

        Double neto = (kg / 1000) * tarifa;
        Double netoR = Math.round(neto * 100.0) / 100.0;
        Double iva = neto * 0.21;
        Double ivaR = Math.round(iva * 100.0) / 100.0;
        Double total = neto + iva;
        Double totalR = Math.round(total * 100.0) / 100.0;
        Double por = chofer.getPorcentaje() / 100;
        Double porcentaje = neto * por;
        Double porR = Math.round(porcentaje * 100.0) / 100.0;

        flete.setFechaCarga(carga);
        flete.setCliente(cliente);
        flete.setOrigenFlete(origenM);
        flete.setFechaFlete(viaje);
        flete.setDestinoFlete(destinoM);
        flete.setKmFlete(km);
        flete.setTipoCereal(tipoCereal);
        flete.setTarifa(tarifa);
        flete.setCartaPorte(cPorte);
        flete.setCtg(ctg);
        flete.setKgFlete(kg);
        flete.setNeto(netoR);
        flete.setIva(ivaR);
        flete.setTotal(totalR);
        flete.setPorcientoChofer(chofer.getPorcentaje());
        flete.setPorcentajeChofer(porR);
        flete.setChofer(chofer);
        flete.setUsuario(chofer);
        flete.setEstado("PENDIENTE");

        fleteRepositorio.save(flete);

    }

    @Transactional
    public void crearFleteAdmin(Long idChofer, String fechaCarga, Long idCliente, String origen, String fechaViaje, String destino, Double km,
            String tipoCereal, Double tarifa, String cPorte, String ctg, Double kg, Long idUsuario) throws ParseException {

        Flete flete = new Flete();

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
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

        String origenM = origen.toUpperCase();
        String destinoM = destino.toUpperCase();
        Date carga = convertirFecha(fechaCarga);
        Date viaje = convertirFecha(fechaViaje);

        Double neto = (kg / 1000) * tarifa;
        Double netoR = Math.round(neto * 100.0) / 100.0;
        Double iva = neto * 0.21;
        Double ivaR = Math.round(iva * 100.0) / 100.0;
        Double total = neto + iva;
        Double totalR = Math.round(total * 100.0) / 100.0;
        Double por = chofer.getPorcentaje() / 100;
        Double porcentaje = neto * por;
        Double porR = Math.round(porcentaje * 100.0) / 100.0;

        flete.setFechaCarga(carga);
        flete.setCliente(cliente);
        flete.setOrigenFlete(origenM);
        flete.setFechaFlete(viaje);
        flete.setDestinoFlete(destinoM);
        flete.setKmFlete(km);
        flete.setTipoCereal(tipoCereal);
        flete.setTarifa(tarifa);
        flete.setCartaPorte(cPorte);
        flete.setCtg(ctg);
        flete.setKgFlete(kg);
        flete.setNeto(netoR);
        flete.setIva(ivaR);
        flete.setTotal(totalR);
        flete.setPorcientoChofer(chofer.getPorcentaje());
        flete.setPorcentajeChofer(porR);
        flete.setChofer(chofer);
        flete.setUsuario(usuario);
        flete.setEstado("ACEPTADO");

        fleteRepositorio.save(flete);

        transaccionServicio.crearTransaccionFleteChofer(buscarUltimo());
        transaccionServicio.crearTransaccionFleteCliente(buscarUltimo());

    }

    @Transactional
    public void aceptarFlete(Long idFlete) {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        flete.setEstado("ACEPTADO");

        fleteRepositorio.save(flete);

        transaccionServicio.crearTransaccionFleteChofer(idFlete);
        transaccionServicio.crearTransaccionFleteCliente(idFlete);
        if (flete.getGasto() != null) {
            transaccionServicio.crearTransaccionGasto(flete.getGasto().getId());
        }

    }

    @Transactional
    public void pendienteFlete(Long idFlete, Long idUsuario) {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }
        
        transaccionServicio.eliminarTransaccionFlete(idFlete);

        if (flete.getGasto() != null) {
           // gastoServicio.eliminarGasto(idFlete, flete.getGasto().getId(), idUsuario);
           transaccionServicio.eliminarTransaccionGasto(flete.getGasto().getId());
        }

        flete.setEstado("PENDIENTE");

        fleteRepositorio.save(flete);
        
    }

    public Long buscarUltimo() {

        return fleteRepositorio.ultimoFlete();
    }

    public ArrayList<Flete> buscarFletesPendiente() {

        ArrayList<Flete> lista = (ArrayList<Flete>) fleteRepositorio.buscarFletePendiente();

        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesRangoFecha(String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.findByFechaFleteBetweenAndEstadoNot(d, h, "ELIMINADO");
        Collections.sort(lista, FleteComparador.ordenarIdDesc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesIdChoferFecha(Long id, String desde, String hasta) throws ParseException {

        Usuario chofer = new Usuario();
        Optional<Usuario> chf = usuarioRepositorio.findById(id);
        if (chf.isPresent()) {
            chofer = chf.get();
        }

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.findByFechaFleteBetweenAndChoferAndEstadoNot(d, h, chofer, "ELIMINADO");

        Collections.sort(lista, FleteComparador.ordenarFechaDesc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesIdClienteFecha(Long id, String desde, String hasta) throws ParseException {

        Cliente cliente = new Cliente();
        Optional<Cliente> cli = clienteRepositorio.findById(id);
        if (cli.isPresent()) {
            cliente = cli.get();
        }

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.findByFechaFleteBetweenAndClienteAndEstadoNot(d, h, cliente, "ELIMINADO");
        Collections.sort(lista, FleteComparador.ordenarIdDesc);

        return lista;
    }

    public Flete buscarFlete(Long id) {

        return fleteRepositorio.getById(id);
    }

    public Flete buscarFleteIdImagenCP(Long id) {

        return fleteRepositorio.buscarFleteIdImagenCP(id);
    }

    public Flete buscarFleteIdImagenDescarga(Long id) {

        return fleteRepositorio.buscarFleteIdImagenDescarga(id);
    }

    public Flete buscarFleteIdGasto(Long id) {

        return fleteRepositorio.buscarFleteIdGasto(id);
    }

    @Transactional
    public void modificarFlete(Long idFlete, Long idChofer, String fechaCarga, Long idCliente, String origen, String fechaViaje, String destino, Double km,
            String tipoCereal, Double tarifa, String cPorte, String ctg, Double kg, Double porciento, Long idUsuario) throws ParseException { //modificar Cliente u observacion de Recibo

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        Usuario chofer = new Usuario();
        Optional<Usuario> chof = usuarioRepositorio.findById(idChofer);
        if (chof.isPresent()) {
            chofer = chof.get();
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

        String origenM = origen.toUpperCase();
        String destinoM = destino.toUpperCase();
        Date carga = convertirFecha(fechaCarga);
        Date viaje = convertirFecha(fechaViaje);
        Double neto = (kg / 1000) * tarifa;
        Double netoR = Math.round(neto * 100.0) / 100.0;
        Double iva = neto * 0.21;
        Double ivaR = Math.round(iva * 100.0) / 100.0;
        Double total = neto + iva;
        Double totalR = Math.round(total * 100.0) / 100.0;
        Double por = chofer.getPorcentaje() / 100;
        if (porciento != chofer.getPorcentaje()) {
            por = porciento / 100;
        }
        Double porChofer = neto * por;
        Double porR = Math.round(porChofer * 100.0) / 100.0;

        flete.setFechaCarga(carga);
        flete.setCliente(cliente);
        flete.setOrigenFlete(origenM);
        flete.setFechaFlete(viaje);
        flete.setDestinoFlete(destinoM);
        flete.setKmFlete(km);
        flete.setTipoCereal(tipoCereal);
        flete.setTarifa(tarifa);
        flete.setCartaPorte(cPorte);
        flete.setCtg(ctg);
        flete.setKgFlete(kg);
        flete.setNeto(netoR);
        flete.setIva(ivaR);
        flete.setTotal(totalR);
        flete.setPorcientoChofer(porciento);
        flete.setPorcentajeChofer(porR);
        flete.setChofer(chofer);
        flete.setUsuario(usuario);

        fleteRepositorio.save(flete);

        if (flete.getEstado().equalsIgnoreCase("ACEPTADO")) {
            transaccionServicio.modificarTransaccionFlete(idFlete);
        }

    }

    @Transactional
    public void eliminarFlete(Long idFlete, Long idUsuario) {

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }
        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        if (flete.getEstado().equalsIgnoreCase("ACEPTADO")) {
            transaccionServicio.eliminarTransaccionFlete(idFlete);
        }

        if (flete.getGasto() != null) {
            gastoServicio.eliminarGasto(idFlete, flete.getGasto().getId(), idUsuario);
        }

        flete.setEstado("ELIMINADO");
        flete.setNeto(0.0);
        flete.setIva(0.0);
        flete.setTotal(0.0);
        flete.setPorcentajeChofer(0.0);
        flete.setUsuario(usuario);

        fleteRepositorio.save(flete);

    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
