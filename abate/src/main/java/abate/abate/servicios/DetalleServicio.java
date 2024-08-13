package abate.abate.servicios;

import abate.abate.entidades.Detalle;
import abate.abate.repositorios.DetalleRepositorio;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleServicio {

    @Autowired
    private DetalleRepositorio detalleRepositorio;

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

    public Long buscarUltimo() {

        return detalleRepositorio.ultimoDetalle();
    }

    public List<Detalle> buscarDetallesFlete(Long flete) {

        ArrayList<Detalle> lista = detalleRepositorio.buscarDetallesFlete(flete);

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
