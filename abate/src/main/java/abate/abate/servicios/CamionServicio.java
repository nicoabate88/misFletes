
package abate.abate.servicios;

import abate.abate.entidades.Camion;
import abate.abate.entidades.CamionEstadistica;
import abate.abate.entidades.Combustible;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Usuario;
import abate.abate.excepciones.MiException;
import abate.abate.repositorios.CamionRepositorio;
import abate.abate.repositorios.CombustibleRepositorio;
import abate.abate.repositorios.FleteRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.CamionComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CamionServicio {
    
    @Autowired
    private CamionRepositorio camionRepositorio;
    @Autowired
    private CombustibleRepositorio combustibleRepositorio;
    @Autowired
    private FleteRepositorio fleteRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    
    @Transactional
    public void crearCamion(Long idOrg, String marca, String modelo, String dominio) throws MiException {

        validarDatos(idOrg, dominio);
        
        Camion camion = new Camion();

        String marcaMayusculas = marca.toUpperCase();
        String modeloMayusculas = modelo.toUpperCase();
        String dominioMayusculas = dominio.toUpperCase();

        camion.setIdOrg(idOrg);
        camion.setMarca(marcaMayusculas);
        camion.setModelo(modeloMayusculas);
        camion.setDominio(dominioMayusculas);

        camionRepositorio.save(camion);

    }
    
    @Transactional
    public void modificarCamion(Long id, String marca, String modelo, String dominio) throws MiException {

        Camion camion = new Camion();
        Optional<Camion> cam = camionRepositorio.findById(id);
        if (cam.isPresent()) {
            camion = cam.get();
        }
        validarDatosModificar(camion, dominio);
        
        String marcaMayusculas = marca.toUpperCase();
        String modeloMayusculas = modelo.toUpperCase();
        String dominioMayusculas = dominio.toUpperCase();

        camion.setMarca(marcaMayusculas);
        camion.setModelo(modeloMayusculas);
        camion.setDominio(dominioMayusculas);

        camionRepositorio.save(camion);

    }
    
    @Transactional
    public void eliminarCamion(Long id) throws MiException {
        
        Camion camion = camionRepositorio.getById(id);
        
        Combustible combustible = combustibleRepositorio.findTopByCamionOrderByIdDesc(camion);
        Flete flete = fleteRepositorio.findTopByCamionAndEstadoNotOrderByIdDesc(camion, "ELIMINADO");
        Usuario usuario = usuarioRepositorio.findTopByCamionOrderByIdDesc(camion);

        if (combustible == null && flete == null && usuario == null) {

            camionRepositorio.deleteById(id);

        } else {

            throw new MiException("El Camión no puede ser eliminado, tiene Flete, Combustible y/o Chofer asociado");
        }

    }
    
     public ArrayList<Camion> buscarCamionesAsc(Long idOrg) {

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(idOrg);

        Collections.sort(lista, CamionComparador.ordenarDominioAsc); 

        return lista;

    }
     
    public Camion buscarCamion(Long id) {

        return camionRepositorio.getById(id);
    } 
     
    public Long buscarUltimo(Long idOrg) {

        return camionRepositorio.ultimoCamion(idOrg);

    }
    
    public ArrayList<CamionEstadistica> estadisticaCamion(String desde, String hasta, Long idCamion) throws ParseException{
        
        Camion camion = camionRepositorio.getById(idCamion);
        Date d = convertirFecha(desde);
        Date h = convertirFecha(hasta);
        
        ArrayList<Flete> fletes = fleteRepositorio.findByFechaFleteBetweenAndCamionAndEstadoNot(d, h, camion, "ELIMINADO");
        ArrayList<Combustible> cargas = combustibleRepositorio.findByFechaCargaBetweenAndCamion(d, h, camion);
        
        // Paso 1: Procesar los fletes
        Map<String, CamionEstadistica> resumenMap = new HashMap<>();
        for (Flete flete : fletes) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(flete.getFechaFlete());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String key = year + "-" + month;

            if (resumenMap.containsKey(key)) {
                CamionEstadistica resumen = resumenMap.get(key);
                resumen.setFlete(resumen.getFlete() + 1);
                resumen.setNeto(resumen.getNeto() + flete.getNeto());
            } else {
                CamionEstadistica nuevoResumen = new CamionEstadistica(year, month, 1, flete.getNeto());
                resumenMap.put(key, nuevoResumen);
            }
        }

        // Paso 2: Procesar los combustibles y actualizar ResumenFletesMensual
        for (Combustible combustible : cargas) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(combustible.getFechaCarga());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String key = year + "-" + month;

            if (resumenMap.containsKey(key)) {
                CamionEstadistica resumen = resumenMap.get(key);
                resumen.setKmRecorrido(resumen.getKmRecorrido() + combustible.getKmRecorrido());
                resumen.setLitro(resumen.getLitro() + combustible.getLitro());
            } else {
                // Si no existe un resumen para ese mes y año, lo creamos (aunque esto no debería ocurrir si los fletes y combustibles coinciden en fechas)
                CamionEstadistica nuevoResumen = new CamionEstadistica(year, month, 0, 0.0);
                nuevoResumen.setKmRecorrido(combustible.getKmRecorrido());
                nuevoResumen.setLitro(combustible.getLitro());
                resumenMap.put(key, nuevoResumen);
            }
        }

        return new ArrayList<>(resumenMap.values());
        
    }

    public void validarDatos(Long idOrg, String dominio) throws MiException {

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(idOrg);

        for (Camion c : lista) {
            if (c.getDominio().equalsIgnoreCase(dominio)) {
                throw new MiException("El DOMINIO de Camión ya está registrado");
            }
        }
    }    
    
    
    public void validarDatosModificar(Camion camion, String dominio) throws MiException {

        ArrayList<Camion> lista = camionRepositorio.buscarCamiones(camion.getIdOrg());

        if(!camion.getDominio().equalsIgnoreCase(dominio)){
        for (Camion c : lista) {
            if (c.getDominio().equalsIgnoreCase(dominio)) {
                throw new MiException("El DOMINIO de Camión ya está registrado");
            }
        }
    } 
    }
    
    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }
    

 
}
