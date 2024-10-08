package abate.abate.servicios;

import abate.abate.entidades.Cuenta;
import abate.abate.entidades.Entrega;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Recibo;
import abate.abate.entidades.Transaccion;
import abate.abate.repositorios.ReciboRepositorio;
import abate.abate.repositorios.TransaccionRepositorio;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import abate.abate.repositorios.EntregaRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.GastoRepositorio;
import abate.abate.util.TransaccionComparador;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

@Service
public class TransaccionServicio {

    @Autowired
    private ReciboRepositorio reciboRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;
    @Autowired
    private EntregaRepositorio entregaRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private CuentaServicio cuentaServicio;
    @Autowired
    private GastoRepositorio gastoRepositorio;

    @Transactional
    public void crearTransaccionRecibo(Long idRecibo) {

        Recibo recibo = new Recibo();
        Optional<Recibo> rec = reciboRepositorio.findById(idRecibo);
        if (rec.isPresent()) {
            recibo = rec.get();
        }

        Transaccion transaccion = new Transaccion();

        transaccion.setCliente(recibo.getCliente());
        transaccion.setFecha(recibo.getFecha());
        transaccion.setConcepto("RECIBO");
        transaccion.setObservacion("RECIBO ID" + recibo.getIdRecibo());
        transaccion.setImporte(recibo.getImporte() * -1);
        transaccion.setRecibo(recibo);

        transaccionRepositorio.save(transaccion);

        cuentaServicio.agregarTransaccionCuentaCliente(buscarUltimo());

    }

    @Transactional
    public void modificarTransaccionRecibo(Long idRecibo) {

        Recibo recibo = new Recibo();
        Optional<Recibo> rec = reciboRepositorio.findById(idRecibo);
        if (rec.isPresent()) {
            recibo = rec.get();
        }

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdRecibo(idRecibo);

        if (!recibo.getCliente().getNombre().equalsIgnoreCase(transaccion.getCliente().getNombre())) {    //si lo que se modifico en la transacion es cliente, entra en este if

            cuentaServicio.eliminarTransaccionCuentaCliente(transaccion); //elimina transaccion en cuenta cliente modificado

            crearTransaccionRecibo(idRecibo);   //agrega transaccion en cuenta cliente modificado

        } else {

            transaccion.setFecha(recibo.getFecha());
            transaccion.setImporte(recibo.getImporte() * -1);

            transaccionRepositorio.save(transaccion);

            cuentaServicio.modificarTransaccionCuentaCliente(transaccion);

        }
    }

    @Transactional
    public void eliminarTransaccionRecibo(Long idRecibo) {

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdRecibo(idRecibo);

        cuentaServicio.eliminarTransaccionCuentaCliente(transaccion);

    }

    @Transactional
    public void crearTransaccionEntrega(Long idEntrega) {

        Entrega entrega = new Entrega();
        Optional<Entrega> ent = entregaRepositorio.findById(idEntrega);
        if (ent.isPresent()) {
            entrega = ent.get();
        }

        Transaccion transaccion = new Transaccion();

        transaccion.setChofer(entrega.getChofer());
        transaccion.setFecha(entrega.getFecha());
        transaccion.setConcepto("ENTREGA");
        transaccion.setObservacion("ENTREGA ID" + entrega.getIdEntrega());
        transaccion.setImporte(entrega.getImporte() * -1);
        transaccion.setEntrega(entrega);

        transaccionRepositorio.save(transaccion);

        cuentaServicio.agregarTransaccionCuentaChofer(buscarUltimo());

    }

    @Transactional
    public void modificarTransaccionEntrega(Long idEntrega) {

        Entrega entrega = new Entrega();
        Optional<Entrega> ent = entregaRepositorio.findById(idEntrega);
        if (ent.isPresent()) {
            entrega = ent.get();
        }

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdEntrega(idEntrega);

        if (!entrega.getChofer().getNombre().equalsIgnoreCase(transaccion.getChofer().getNombre())) {    //si lo que se modifico en la transacion es chofer, entra en este if

            cuentaServicio.eliminarTransaccionCuentaChofer(transaccion); //elimina transaccion en cuenta cliente modificado

            crearTransaccionEntrega(idEntrega);   //agrega transaccion en cuenta cliente modificado

        } else {

            transaccion.setFecha(entrega.getFecha());
            transaccion.setImporte(entrega.getImporte() * -1);

            transaccionRepositorio.save(transaccion);

            cuentaServicio.modificarTransaccionCuentaChofer(transaccion);

        }
    }

    @Transactional
    public void eliminarTransaccionEntrega(Long idEntrega) {

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdEntrega(idEntrega);

        cuentaServicio.eliminarTransaccionCuentaChofer(transaccion);

    }

    @Transactional
    public void crearTransaccionFleteChofer(Long idFlete) {

        Flete flete = new Flete();
        Optional<Flete> fle = fleteRepositorio.findById(idFlete);
        if (fle.isPresent()) {
            flete = fle.get();
        }

        Transaccion transaccion = new Transaccion();

        transaccion.setChofer(flete.getChofer());
        transaccion.setFecha(flete.getFechaFlete());
        transaccion.setConcepto("FLETE");
        transaccion.setObservacion("FLETE ID" + flete.getIdFlete());
        transaccion.setImporte(flete.getPorcentajeChofer());
        transaccion.setFlete(flete);

        transaccionRepositorio.save(transaccion);

        cuentaServicio.agregarTransaccionCuentaChofer(buscarUltimo());

    }

    @Transactional
    public void crearTransaccionFleteCliente(Long idFlete) {

        Flete flete = new Flete();
        Optional<Flete> fle = fleteRepositorio.findById(idFlete);
        if (fle.isPresent()) {
            flete = fle.get();
        }

        Transaccion transaccion = new Transaccion();

        transaccion.setCliente(flete.getCliente());
        transaccion.setFecha(flete.getFechaFlete());
        transaccion.setConcepto("FLETE");
        transaccion.setObservacion("FLETE ID" + flete.getIdFlete());
        transaccion.setImporte(flete.getTotal());
        transaccion.setFlete(flete);

        transaccionRepositorio.save(transaccion);

        cuentaServicio.agregarTransaccionCuentaCliente(buscarUltimo());

    }

    @Transactional
    public void modificarTransaccionFlete(Long idFlete) {

        Flete flete = new Flete();
        Optional<Flete> fle = fleteRepositorio.findById(idFlete);
        if (fle.isPresent()) {
            flete = fle.get();
        }

        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionesIdFlete(idFlete);

        Transaccion tChofer = new Transaccion();
        Transaccion tCliente = new Transaccion();
        for (Transaccion t : lista) {
            if (t.getChofer() != null) {
                tChofer = t;                  //se obtiene la transaccin de Chofer
            }
            if (t.getCliente() != null) {
                tCliente = t;                //se obtiene la transaccion de Cliente
            }
        }

        if (!flete.getChofer().getNombre().equalsIgnoreCase(tChofer.getChofer().getNombre())) {    //si lo que se modifico en la transacion es chofer, entra en este if

            cuentaServicio.eliminarTransaccionCuentaChofer(tChofer); //elimina transaccion en cuenta chofer modificado

            crearTransaccionFleteChofer(idFlete);   //agrega transaccion en cuenta Chofer modificado

        }
        if (!flete.getCliente().getNombre().equalsIgnoreCase(tCliente.getCliente().getNombre())) {    //si lo que se modifico en la transacion es cliente, entra en este if

            cuentaServicio.eliminarTransaccionCuentaCliente(tCliente); //elimina transaccion en cuenta cliente modificado

            crearTransaccionFleteCliente(idFlete);   //agrega transaccion en cuenta Chofer modificado

        } else {

            tChofer.setFecha(flete.getFechaFlete());
            tChofer.setImporte(flete.getPorcentajeChofer());

            transaccionRepositorio.save(tChofer);    //se modifica la transaccion de Chofer
            cuentaServicio.modificarTransaccionCuentaChofer(tChofer);

            tCliente.setFecha(flete.getFechaFlete());
            tCliente.setImporte(flete.getTotal());

            transaccionRepositorio.save(tCliente);   //se modifica la transaccion de Cliente
            cuentaServicio.modificarTransaccionCuentaCliente(tCliente);

        }

    }

    @Transactional
    public void eliminarTransaccionFlete(Long idFlete) {

        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionesIdFlete(idFlete);
        Transaccion tChofer = new Transaccion();
        Transaccion tCliente = new Transaccion();
        for (Transaccion t : lista) {
            if (t.getChofer() != null) {
                tChofer = t;                  //se obtiene la transaccin de Chofer
            }
            if (t.getCliente() != null) {
                tCliente = t;                //se obtiene la transaccion de Cliente
            }
        }

        cuentaServicio.eliminarTransaccionCuentaChofer(tChofer);
        cuentaServicio.eliminarTransaccionCuentaCliente(tCliente);
    }

    @Transactional
    public void crearTransaccionGasto(Long idGasto) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        Transaccion transaccion = new Transaccion();

        transaccion.setChofer(gasto.getChofer());
        transaccion.setFecha(gasto.getFecha());
        transaccion.setConcepto("GASTO");
        transaccion.setObservacion(gasto.getNombre());
        transaccion.setImporte(gasto.getImporte());
        transaccion.setGasto(gasto);

        transaccionRepositorio.save(transaccion);

        cuentaServicio.agregarTransaccionCuentaChofer(buscarUltimo());

    }

    @Transactional
    public void modificarTransaccionGasto(Long idGasto) {

        Gasto gasto = new Gasto();
        Optional<Gasto> gto = gastoRepositorio.findById(idGasto);
        if (gto.isPresent()) {
            gasto = gto.get();
        }

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdGasto(idGasto);

        transaccion.setImporte(gasto.getImporte());

        transaccionRepositorio.save(transaccion);

        cuentaServicio.modificarTransaccionCuentaChofer(transaccion);

    }

    @Transactional
    public void eliminarTransaccionGasto(Long idGasto) {

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdGasto(idGasto);

        cuentaServicio.eliminarTransaccionCuentaChofer(transaccion);

    }

    public Long buscarUltimo() {

        return transaccionRepositorio.ultimoTransaccion();
    }

    public Transaccion buscarTransaccion(Long id) {

        return transaccionRepositorio.getById(id);
    }

    public ArrayList<Transaccion> buscarTransaccionIdCuenta(Long idCuenta) {

        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionCuenta(idCuenta);

        Collections.sort(lista, TransaccionComparador.ordenarFechaAcs);

        Double saldoAcumulado = 0.0;

        for (Transaccion t : lista) {                 //for para obtener el saldo acumulado
            saldoAcumulado = saldoAcumulado + t.getImporte();
            saldoAcumulado = Math.round(saldoAcumulado * 100.0) / 100.0;  //redondeamos saldoAcumulado solo a 2 decimales
            t.setSaldoAcumulado(saldoAcumulado);
        }

        Collections.reverse(lista);

        return lista;
    }
    
    public ArrayList<Transaccion> buscarTransaccionIdCuentaFecha(Long idCuenta, String desde, String hasta) throws ParseException {
        
        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);
        
        ArrayList<Transaccion> lista = transaccionRepositorio.buscarTransaccionCuentaPorRangoFechas(idCuenta, d, h);
        
        Collections.sort(lista, TransaccionComparador.ordenarFechaAcs);

        Double saldoAcumulado = 0.0;

        for (Transaccion t : lista) {                 //for para obtener el saldo acumulado
            saldoAcumulado = saldoAcumulado + t.getImporte();
            saldoAcumulado = Math.round(saldoAcumulado * 100.0) / 100.0;  //redondeamos saldoAcumulado solo a 2 decimales
            t.setSaldoAcumulado(saldoAcumulado);
        }

        Collections.reverse(lista);
        
        return lista;
    }
    
      public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }
    
    

}
