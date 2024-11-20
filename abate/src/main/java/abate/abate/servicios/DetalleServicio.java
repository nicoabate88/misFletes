package abate.abate.servicios;

import abate.abate.entidades.Detalle;
import abate.abate.entidades.Flete;
import abate.abate.repositorios.DetalleRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleServicio {

    @Autowired
    private DetalleRepositorio detalleRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;

    @Transactional
    public void crearDetalle(Long flete, String concepto, Integer cantidad, Double precio) {

        Detalle detalle = new Detalle();

        String conceptoM = concepto.toUpperCase();

        detalle.setFlete(flete);
        detalle.setConcepto(conceptoM);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(precio);
        detalle.setTotal(cantidad * precio);

        detalleRepositorio.save(detalle);

    }
    
     @Transactional
    public void crearDetalleGasto(Long gasto, String concepto, Integer cantidad, Double precio) {

        Detalle detalle = new Detalle();

        String conceptoM = concepto.toUpperCase();

        detalle.setGasto(gasto);
        detalle.setConcepto(conceptoM);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(precio);
        detalle.setTotal(cantidad * precio);

        detalleRepositorio.save(detalle);

    }

    public Long buscarUltimo() {

        return detalleRepositorio.ultimoDetalle();
    }

    public List<Detalle> buscarDetallesFlete(Long flete) {

        ArrayList<Detalle> lista = detalleRepositorio.buscarDetallesFlete(flete);

        return lista;

    }
    
    public List<Detalle> buscarDetallesFleteIdGasto(Long idGasto) {
        
        Long idFlete = fleteRepositorio.findFleteIdByIdGasto(idGasto);

        ArrayList<Detalle> lista = detalleRepositorio.buscarDetallesFlete(idFlete);

        return lista;

    }
    
    public List<Detalle> buscarDetallesGasto(Long gasto) {

        ArrayList<Detalle> lista = detalleRepositorio.buscarDetallesGasto(gasto);

        return lista;

    }
    
    @Transactional
    public void cancelarDetalle(Long idFlete) {

        ArrayList<Detalle> lista = detalleRepositorio.buscarDetallesFlete(idFlete);

        if (!lista.isEmpty()) {
            for (Detalle d : lista) {
                eliminarDetalle(d.getId());
            }
        }
    }

    @Transactional
    public void eliminarDetalle(Long id) {

        detalleRepositorio.deleteById(id);
    }

}
