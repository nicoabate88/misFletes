package abate.abate.servicios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.CombustibleRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.CombustibleComparador;
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
public class CombustibleServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private CombustibleRepositorio combustibleRepositorio;
    @Autowired
    private CamionRepositorio camionRepositorio;

    @Transactional
    public void crearPrimerCarga(Long idOrg, String fecha, Double km, Long idCamion, Usuario usuario) throws ParseException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        Date f = convertirFecha(fecha);

        Combustible carga = new Combustible();

        carga.setIdOrg(idOrg);
        carga.setFechaCarga(f);
        carga.setKmCarga(km);
        carga.setKmRecorrido(0.0);
        carga.setLitro(0.0);
        carga.setCompleto("SI");
        carga.setEstado("ACEPTADO");
        carga.setConsumoPromedio(0.0);
        carga.setCamion(camion);
        carga.setUsuario(usuario);

        combustibleRepositorio.save(carga);

    }

    @Transactional
    public void crearCarga(Long idOrg, Long idCamion, String fecha, Double kmAnterior, Double kmCarga, Double litros, String completo, Usuario usuario) throws ParseException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(idCamion);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        Date f = convertirFecha(fecha);
        Double kmRecorrido = kmCarga - kmAnterior;
        Double consumo = ((100 * litros) / kmRecorrido);
        Double consumoRed = Math.round(consumo * 100.0) / 100.0;

        Combustible carga = new Combustible();

        carga.setIdOrg(idOrg);
        carga.setFechaCarga(f);
        carga.setLitro(litros);
        carga.setUsuario(usuario);
        carga.setKmCarga(kmCarga);
        carga.setKmAnterior(kmAnterior);
        carga.setKmRecorrido(kmRecorrido);
        carga.setConsumo(consumoRed);
        carga.setEstado("ACEPTADO");
        carga.setCamion(camion);

        if (completo.equalsIgnoreCase("NO")) {
            carga.setCompleto("NO");
            carga.setConsumoPromedio(0.0);
        } else {
            carga.setCompleto("SI");
            Double consumoPromedio = consumoPromedioTanque(consumoRed, kmRecorrido, litros, camion);
            carga.setConsumoPromedio(consumoPromedio);
        }

        combustibleRepositorio.save(carga);

    }
    
    @Transactional
    public void modificarPrimerCarga(Long id, String fecha, Double kmCarga) throws ParseException {

        Combustible carga = new Combustible();
        Optional<Combustible> cga = combustibleRepositorio.findById(id);
        if (cga.isPresent()) {
            carga = cga.get();
        }

        Date f = convertirFecha(fecha);

        carga.setFechaCarga(f);
        carga.setKmCarga(kmCarga);

        combustibleRepositorio.save(carga);

    }

    @Transactional
    public void modificarCarga(Long id, String fecha, Double kmCarga, Double litros, String completo, Usuario usuario) throws ParseException {

        Combustible carga = new Combustible();
        Optional<Combustible> cga = combustibleRepositorio.findById(id);
        if (cga.isPresent()) {
            carga = cga.get();
        }

        Date f = convertirFecha(fecha);
        Double kmRecorrido = kmCarga - carga.getKmAnterior();
        Double consumo = ((100 * litros) / kmRecorrido);
        Double consumoRed = Math.round(consumo * 100.0) / 100.0;

        carga.setFechaCarga(f);
        carga.setKmCarga(kmCarga);
        carga.setLitro(litros);
        carga.setKmRecorrido(kmRecorrido);
        carga.setConsumo(consumoRed);
        carga.setEstado("ACEPTADO");
        carga.setUsuario(usuario);

        if (completo.equalsIgnoreCase("NO")) {
            carga.setCompleto("NO");
            carga.setConsumoPromedio(0.0);
        } else {
            carga.setCompleto("SI");
            Double consumoPromedio = consumoPromedioTanqueModifica(consumoRed, kmRecorrido, litros, carga.getCamion(), carga.getCamion().getId());
            carga.setConsumoPromedio(consumoPromedio);
        }

        combustibleRepositorio.save(carga);

    }

    @Transactional
    public void eliminarCarga(Long id) throws ParseException {

        Combustible carga = new Combustible();
        Optional<Combustible> cga = combustibleRepositorio.findById(id);
        if (cga.isPresent()) {
            carga = cga.get();
        }

        carga.setImagen(null);
        carga.setUsuario(null);

        combustibleRepositorio.save(carga);

        combustibleRepositorio.deleteById(id);

    }

    public ArrayList<Combustible> buscarCargasCamion(Long idCamion) {

        Camion camion = camionRepositorio.getById(idCamion);

        ArrayList<Combustible> lista = combustibleRepositorio.findAllByCamionOrderByIdDesc(camion);

        return lista;

    }

    public ArrayList<Combustible> buscarCargasIdCamion(Long id, String desde, String hasta) throws ParseException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(id);
        if (cam.isPresent()) {
            camion = cam.get();
        }

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);

        ArrayList<Combustible> lista = combustibleRepositorio.findByFechaCargaBetweenAndCamion(d, h, camion);

        Collections.sort(lista, CombustibleComparador.ordenarIdDesc);

        if (!lista.isEmpty()) {
            Combustible carga = lista.get(0);
            carga.setEstado("ULTIMO");
        }

        return lista;
    }

    public ArrayList<Combustible> buscarCargasIdChofer(Long id, String desde, String hasta) throws ParseException {

        Usuario chofer = new Usuario();
        Optional<Usuario> chf = usuarioRepositorio.findById(id);
        if (chf.isPresent()) {
            chofer = chf.get();
        }

        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);
        Boolean flag = false;

        ArrayList<Combustible> lista = combustibleRepositorio.findByFechaCargaBetweenAndUsuario(d, h, chofer);

        Collections.sort(lista, CombustibleComparador.ordenarIdDesc);

        if (!lista.isEmpty()) {
            Combustible carga = lista.get(0);
            Combustible ultimaCarga = combustibleRepositorio.findTopByCamionOrderByIdDesc(carga.getCamion());
            if (carga.getId() == ultimaCarga.getId()) {
                flag = true;
            }
        }

        if (!lista.isEmpty() && flag == true) {
            Combustible carga = lista.get(0);
            carga.setEstado("ULTIMO");
        }

        return lista;
    }

    public Double consumoPromedioCamion(Long idCamion) {

        Camion camion = camionRepositorio.getById(idCamion);
        Double litros = 0.0;
        Double km = 0.0;
        Double redondeado = 0.0;

        ArrayList<Combustible> listaCargas = combustibleRepositorio.findAllByCamionOrderByIdDesc(camion);

        if (!listaCargas.isEmpty()) {

            int lastIndex = listaCargas.size() - 1;
            listaCargas.remove(lastIndex);  //elimino ultimo registro de la listra porque es la carga inicial de KM

            for (Combustible c : listaCargas) {
                litros = litros + c.getLitro();
                km = km + c.getKmRecorrido();
            }

            Double consumoPromedioCamion = (100 * litros) / km;
            redondeado = Math.round(consumoPromedioCamion * 100.0) / 100.0;

            return redondeado;

        } else {

            return redondeado;

        }

    }

    public Double consumoPromedioTanque(Double consumo, Double km, Double litros, Camion camion) {

        ArrayList<Combustible> listaPromedio = new ArrayList();

        Combustible ultimaCarga = combustibleRepositorio.findTopByCamionOrderByIdDesc(camion);

        if (ultimaCarga.getCompleto().equalsIgnoreCase("NO")) {

            ArrayList<Combustible> listaCargas = combustibleRepositorio.findAllByCamionOrderByIdDesc(camion);

            for (Combustible c : listaCargas) {
                if (c.getCompleto().equalsIgnoreCase("SI") || c.getConsumo() == null) {
                    break;
                }
                listaPromedio.add(c);
            }
        }

        if (!listaPromedio.isEmpty()) {  //si listaPromedio no está vacía

            km = km;
            litros = litros;

            for (Combustible c : listaPromedio) {
                km = km + c.getKmRecorrido();
                litros = litros + c.getLitro();

            }

            Double consumoPromedio = ((100 * litros) / km);
            consumo = Math.round(consumoPromedio * 100.0) / 100.0;

        }

        return consumo;
    }

    public Double consumoPromedioTanqueModifica(Double consumo, Double km, Double litros, Camion camion, Long id) {

        ArrayList<Combustible> listaPromedio = new ArrayList();

        ArrayList<Combustible> ultimasCargas = combustibleRepositorio.findTop2ByCamionOrderByIdDesc(id);  //se obtienen ultimas 2 cargas
        Combustible anteultimaCarga = ultimasCargas.get(1);  //se obtiene anteultima carga

        if (anteultimaCarga.getCompleto().equalsIgnoreCase("NO")) {

            ArrayList<Combustible> listaCargas = combustibleRepositorio.findAllByCamionOrderByIdDesc(camion);
            listaCargas.remove(0);

            for (Combustible c : listaCargas) {
                if (c.getCompleto().equalsIgnoreCase("SI") || c.getConsumo() == null) {

                    break;
                }

                listaPromedio.add(c);
            }
        }

        if (!listaPromedio.isEmpty()) {  //si listaPromedio no está vacía

            km = km;
            litros = litros;

            for (Combustible c : listaPromedio) {
                km = km + c.getKmRecorrido();
                litros = litros + c.getLitro();

            }

            Double consumoPromedio = ((100 * litros) / km);
            consumo = Math.round(consumoPromedio * 100.0) / 100.0;

        }

        return consumo;
    }

    public Long buscarUltimo(Long idOrg) {

        return combustibleRepositorio.ultimaCarga(idOrg);

    }

    public Combustible buscarCombustibleIdImagen(Long id) {

        return combustibleRepositorio.buscarCombustibleIdImagen(id);
    }

    public Combustible buscarCombustible(Long id) {

        return combustibleRepositorio.getById(id);
    }

    public Combustible cargaAnterior(Camion camion) {

        return combustibleRepositorio.findTopByCamionOrderByIdDesc(camion);

    }
    
    public Combustible cargaAnteultimo(Camion camion) {

        ArrayList<Combustible> ultimosRegistros = combustibleRepositorio.findTop2ByCamionOrderByIdDesc(camion);

        if (ultimosRegistros.size() >= 2) {
            return ultimosRegistros.get(1); 
        } else {
            return ultimosRegistros.get(0); 
        }
    }

    public boolean kmIniciales(Camion camion) {

        Optional<Combustible> iniciales = combustibleRepositorio.findFirstByCamionOrderByIdAsc(camion);
        if (iniciales.isPresent()) {

            boolean flag = true;

            return flag;

        } else {

            boolean flag = false;

            return flag;

        }

    }

    public Date convertirFecha(String fecha) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}
