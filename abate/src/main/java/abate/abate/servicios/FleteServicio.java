package abate.abate.servicios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Cliente;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.CamionRepositorio;
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
    @Autowired
    private CamionRepositorio camionRepositorio;
    
    @Transactional
    public void crearFleteChofer(Long idOrg, String fechaCarga, Long idCliente, Long idCamion, String origen, String fechaViaje, String destino, Double km,
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
        
        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }
        
        Long ifFlete = buscarUltimoIdOrg(idOrg);
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
        Double porcentaje = (double) Math.round(neto * por);
        
        flete.setIdOrg(idOrg);
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
        flete.setPorcentajeChofer(porcentaje);
        flete.setChofer(chofer);
        flete.setUsuario(chofer);
        flete.setEstado("PENDIENTE");
        flete.setComisionTpte(0.0);
        flete.setComisionTpteValor(0.0);
        flete.setComisionTpteChofer("NO");
        flete.setCamion(camion);
        flete.setIdFlete(ifFlete+1);
        
        fleteRepositorio.save(flete);
        
    }
    
    @Transactional
    public void crearFleteAdmin(Long idOrg, Long idChofer, Long idCamion, String fechaCarga, Long idCliente, String origen, String fechaViaje, String destino, Double km,
            String tipoCereal, Double tarifa, String cPorte, String ctg, Double kg, Double comisionTpte, String comisionTpteChofer, String ivaN, Long idUsuario) throws ParseException {
        
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
        
        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }
        
        Long ifFlete = buscarUltimoIdOrg(idOrg);
        String origenM = origen.toUpperCase();
        String destinoM = destino.toUpperCase();
        Date carga = convertirFecha(fechaCarga);
        Date viaje = convertirFecha(fechaViaje);
        Double neto = (kg / 1000) * tarifa;
        Double netoR = Math.round(neto * 100.0) / 100.0;        
        Double por = chofer.getPorcentaje() / 100;
        Double porcentaje = (double) Math.round(neto * por);
        
        if (comisionTpte == 0.0) {
            flete.setNeto(netoR);
            flete.setComisionTpte(0.0);
            flete.setComisionTpteValor(0.0);
            flete.setComisionTpteChofer("NO");
            flete.setPorcentajeChofer(porcentaje);
            if (ivaN.equalsIgnoreCase("SI")) {
                Double iva = neto * 0.21;
                Double ivaR = Math.round(iva * 100.0) / 100.0;
                Double total = neto + iva;
                Double totalR = Math.round(total * 100.0) / 100.0;
                flete.setIva(ivaR);
                flete.setTotal(totalR);
            } else {
                flete.setIva(0.0);
                flete.setTotal(netoR);
            }
        } else {
            Double comision = ((comisionTpte / 100) * netoR);
            Double netoFlete = Math.round((netoR - comision) * 100.0) / 100.0;            
            flete.setNeto(netoFlete);
            flete.setComisionTpte(comisionTpte);
            flete.setComisionTpteValor(comision);
            if (ivaN.equalsIgnoreCase("SI")) {
                Double iva = netoFlete * 0.21;
                Double ivaR = Math.round(iva * 100.0) / 100.0;
                Double total = netoFlete + iva;
                Double totalR = Math.round(total * 100.0) / 100.0;
                flete.setIva(ivaR);
                flete.setTotal(totalR);
            } else {
                flete.setIva(0.0);
                flete.setTotal(netoFlete);
            }
            if (comisionTpteChofer.equalsIgnoreCase("NO")) {
                flete.setPorcentajeChofer(porcentaje);
                flete.setComisionTpteChofer("NO");
            } else {
                Double comisionChofer = ((comisionTpte / 100) * porcentaje);
                Double comisionChoferR = (double) Math.round(porcentaje - comisionChofer);    
                
                flete.setPorcentajeChofer(comisionChoferR);
                flete.setComisionTpteChofer("SI");
            }
        }
        
        flete.setIdOrg(idOrg);
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
        flete.setPorcientoChofer(chofer.getPorcentaje());
        flete.setChofer(chofer);
        flete.setUsuario(usuario);
        flete.setEstado("ACEPTADO");
        flete.setCamion(camion);
        flete.setIdFlete(ifFlete+1);
        
        fleteRepositorio.save(flete);
        
        transaccionServicio.crearTransaccionFleteChofer(buscarUltimo(idOrg));
        transaccionServicio.crearTransaccionFleteCliente(buscarUltimo(idOrg));
        
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
    
    public Long buscarUltimoIdOrg(Long idOrg) {
        
        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findTopByIdOrgAndEstadoNotOrderByIdDesc(idOrg, "ELIMINADO");
        if (fte.isPresent()) {
            flete = fte.get();
            
            return flete.getIdFlete();
            
        } else {
            
            int ultimo = 0;
            Long primero = Long.valueOf(ultimo);

            return primero;
            
        }

    }

    public Long buscarUltimo(Long idOrg) {

        return fleteRepositorio.ultimoFlete(idOrg);
    }

    public ArrayList<Flete> buscarFletesPendiente(Long idOrg) {

        ArrayList<Flete> lista = (ArrayList<Flete>) fleteRepositorio.buscarFletePendiente(idOrg);

        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

        return lista;
    }

    public ArrayList<Flete> buscarFletesRangoFecha(Long idOrg, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.findByFechaFleteBetweenAndEstadoNotAndIdOrg(d, h, "ELIMINADO", idOrg);
        Collections.sort(lista, FleteComparador.ordenarIdDesc);

        return lista;
    }
    
    public ArrayList<Flete> buscarFletesRangoFechaAsc(Long idOrg, String desde, String hasta) throws ParseException {

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.findByFechaFleteBetweenAndEstadoNotAndIdOrg(d, h, "ELIMINADO", idOrg);
        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

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
    
    public ArrayList<Flete> buscarFletesIdChoferFechaAsc(Long id, String desde, String hasta) throws ParseException {

        Usuario chofer = new Usuario();
        Optional<Usuario> chf = usuarioRepositorio.findById(id);
        if (chf.isPresent()) {
            chofer = chf.get();
        }

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.findByFechaFleteBetweenAndChoferAndEstadoNot(d, h, chofer, "ELIMINADO");

        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

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
    
    public ArrayList<Flete> buscarFletesIdClienteFechaAsc(Long id, String desde, String hasta) throws ParseException {

        Cliente cliente = new Cliente();
        Optional<Cliente> cli = clienteRepositorio.findById(id);
        if (cli.isPresent()) {
            cliente = cli.get();
        }

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Flete> lista = fleteRepositorio.findByFechaFleteBetweenAndClienteAndEstadoNot(d, h, cliente, "ELIMINADO");
        Collections.sort(lista, FleteComparador.ordenarFechaAsc);

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
/*
    @Transactional
    public void modificarFleteChofer(Long idFlete, Long idChofer, Long idCamion, String fechaCarga, Long idCliente, String origen, String fechaViaje, String destino, Double km,
            String tipoCereal, Double tarifa, String cPorte, String ctg, Double kg, Double ivaM, Double porciento, 
            Double comisionTpte, String comisionTpteChofer, Long idUsuario) throws ParseException { 

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
        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        String origenM = origen.toUpperCase();
        String destinoM = destino.toUpperCase();
        Date carga = convertirFecha(fechaCarga);
        Date viaje = convertirFecha(fechaViaje);
        Double neto = (kg / 1000) * tarifa;
        Double iva;
        
        if(ivaM == 0.0){
        iva = ivaM;
        } else {
            iva = neto * 0.21;
        }
        
        if(comisionTpte != 0.0){
            Double tpte = ((comisionTpte/100)*neto);
            neto = neto - tpte;
            flete.setComisionTpte(comisionTpte);
            flete.setComisionTpteValor(tpte);
            if(ivaM != 0.0){
            iva = neto * 0.21;
            }
        } else {
            flete.setComisionTpte(0.0);
            flete.setComisionTpteValor(0.0);
        }
        
        Double netoR = Math.round(neto * 100.0) / 100.0;
        Double ivaR = Math.round(iva * 100.0) / 100.0;
        
        Double total = neto + iva;
        Double totalR = Math.round(total * 100.0) / 100.0;
        Double por = chofer.getPorcentaje() / 100;
        if (porciento != chofer.getPorcentaje()) {
            por = porciento / 100;
        }
        
        Double porChofer = neto * por;
        flete.setComisionTpteChofer("SI");
        
        if(comisionTpteChofer.equalsIgnoreCase("NO")){
            porChofer = (kg / 1000) * tarifa * por;
            flete.setComisionTpteChofer("NO");
        }
        
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
        flete.setCamion(camion);

        fleteRepositorio.save(flete);

        if (flete.getEstado().equalsIgnoreCase("ACEPTADO")) {
            transaccionServicio.modificarTransaccionFlete(idFlete);
        }

    }
  */  
    @Transactional
    public void modificarFleteChofer(Long idFlete, Long idCamion, String fechaCarga, Long idCliente, String origen, String fechaViaje, String destino, Double km,
            String tipoCereal, Double tarifa, String cPorte, String ctg, Double kg) throws ParseException { 

        Flete flete = new Flete();
        Optional<Flete> fte = fleteRepositorio.findById(idFlete);
        if (fte.isPresent()) {
            flete = fte.get();
        }

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }
        
        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        String origenM = origen.toUpperCase();
        String destinoM = destino.toUpperCase();
        Date carga = convertirFecha(fechaCarga);
        Date viaje = convertirFecha(fechaViaje);
        Double neto = (kg / 1000) * tarifa;
        Double iva = neto * 0.21;
        
        Double netoR = Math.round(neto * 100.0) / 100.0;
        Double por = flete.getPorcientoChofer() / 100;
        Double porcentaje = (double) Math.round(netoR * por);
        
        Double ivaR = Math.round(iva * 100.0) / 100.0;
        
        Double total = neto + iva;
        Double totalR = Math.round(total * 100.0) / 100.0;

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
        flete.setPorcentajeChofer(porcentaje);
        flete.setCamion(camion);

        fleteRepositorio.save(flete);

    }
    
    @Transactional
    public void modificarFleteAdmin(Long idFlete, Long idChofer, Long idCamion, String fechaCarga, Long idCliente, String origen, String fechaViaje, String destino, Double km,
            String tipoCereal, Double tarifa, String cPorte, String ctg, Double kg, Double ivaM, Double porciento, Double porcentajeChofer,
            Double comisionTpte, String comisionTpteChofer, Long idUsuario) throws ParseException { 

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
        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        String origenM = origen.toUpperCase();
        String destinoM = destino.toUpperCase();
        Date carga = convertirFecha(fechaCarga);
        Date viaje = convertirFecha(fechaViaje);
        Double neto = (kg / 1000) * tarifa;
        Double iva;
        
        if(ivaM == 0.0){
        iva = ivaM;
        } else {
            iva = neto * 0.21;
        }
        
        if(comisionTpte != 0.0){
            Double tpte = ((comisionTpte/100)*neto);
            neto = neto - tpte;
            flete.setComisionTpte(comisionTpte);
            flete.setComisionTpteValor(tpte);
            if(ivaM != 0.0){
            iva = neto * 0.21;
            }
        } else {
            flete.setComisionTpte(0.0);
            flete.setComisionTpteValor(0.0);
        }
        
        Double netoR = Math.round(neto * 100.0) / 100.0;
        Double ivaR = Math.round(iva * 100.0) / 100.0;
        
        Double total = neto + iva;
        Double totalR = Math.round(total * 100.0) / 100.0;
        
        if(porciento != 0){
            
        porcentajeChofer = (double) Math.round(neto * (porciento / 100));
        
        if(comisionTpteChofer.equalsIgnoreCase("SI")){
            
            porcentajeChofer = (double) Math.round(netoR * (porciento / 100));
            flete.setComisionTpteChofer("SI");
            
        } else {
            porcentajeChofer = (double) Math.round(((kg * tarifa) / 1000) * (porciento / 100));
            flete.setComisionTpteChofer("NO");
        }
        } 

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
        flete.setPorcentajeChofer(porcentajeChofer);
        flete.setChofer(chofer);
        flete.setUsuario(usuario);
        flete.setCamion(camion);

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
        flete.setCamion(null);

        fleteRepositorio.save(flete);

    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
