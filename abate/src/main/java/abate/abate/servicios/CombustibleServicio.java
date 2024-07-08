
package abate.abate.servicios;

import abate.abate.entidades.Combustible;
import abate.abate.entidades.Usuario;
import abate.abate.repositorios.CombustibleRepositorio;
import abate.abate.repositorios.UsuarioRepositorio;
import abate.abate.util.ChoferComparador;
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
    
    @Transactional
    public void crearPrimerCarga(String fecha, Double km, Long idUsuario) throws ParseException{
        
        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }
        
        Date f = convertirFecha(fecha);
        
        Combustible carga = new Combustible();
        
        carga.setFechaCarga(f);
        carga.setKmCarga(km);
        carga.setUsuario(usuario);
        carga.setCompleto("SI");
        carga.setEstado("ACEPTADO");
        carga.setConsumoPromedio(0.0);
        
        combustibleRepositorio.save(carga);
        
    }
    
    @Transactional
    public void crearCarga(String fecha, Double kmCarga, Double litros, String completo, Long idUsuario) throws ParseException{

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        Date f = convertirFecha(fecha);
        Double kmAnterior = kmAnterior(usuario);
        Double kmRecorrido = kmCarga - kmAnterior;
        Double consumo = ((100*litros)/kmRecorrido);
        Double consumoRed = Math.round(consumo * 100.0) / 100.0;
        
        Combustible carga = new Combustible();

        carga.setFechaCarga(f);
        carga.setLitro(litros);
        carga.setUsuario(usuario);
        carga.setKmCarga(kmCarga);
        carga.setKmAnterior(kmAnterior);
        carga.setKmRecorrido(kmRecorrido);
        carga.setConsumo(consumoRed);
        carga.setEstado("ACEPTADO");
        
        if(completo.equalsIgnoreCase("NO")){
            carga.setCompleto("NO");
            carga.setConsumoPromedio(0.0);
        } else {
            carga.setCompleto("SI");
            Double consumoPromedio = consumoPromedioTanque(consumoRed, kmRecorrido, litros, usuario);
            carga.setConsumoPromedio(consumoPromedio);
        }
        
        combustibleRepositorio.save(carga);

    }
    
    @Transactional
    public void modificarCarga(Long id, String fecha, Double kmCarga, Double litros, String completo, Long idChofer) throws ParseException{
        
        Combustible carga = new Combustible();
        Optional<Combustible> cga = combustibleRepositorio.findById(id);
        if(cga.isPresent()){
            carga = cga.get();
        }
        
        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idChofer);
        if (user.isPresent()) {
            usuario = user.get();
        }

        Date f = convertirFecha(fecha);
        Double kmRecorrido = kmCarga - carga.getKmAnterior();
        Double consumo = ((100*litros)/kmRecorrido);
        Double consumoRed = Math.round(consumo * 100.0) / 100.0;

        carga.setFechaCarga(f);
        carga.setKmCarga(kmCarga);
        carga.setLitro(litros);
        carga.setKmRecorrido(kmRecorrido);
        carga.setConsumo(consumoRed);
        carga.setEstado("ACEPTADO");
        
        if(completo.equalsIgnoreCase("NO")){
            carga.setCompleto("NO");
            carga.setConsumoPromedio(0.0);
        } else {
            carga.setCompleto("SI");
            Double consumoPromedio = consumoPromedioTanque(consumoRed, kmRecorrido, litros, usuario);
            carga.setConsumoPromedio(consumoPromedio);
        }
        
        combustibleRepositorio.save(carga);

    }
    
    @Transactional
    public void eliminarCarga(Long id) throws ParseException{
        
        Combustible carga = new Combustible();
        Optional<Combustible> cga = combustibleRepositorio.findById(id);
        if(cga.isPresent()){
            carga = cga.get();
        }

        carga.setImagen(null);
        carga.setUsuario(null);
        
        combustibleRepositorio.save(carga);
        
        combustibleRepositorio.deleteById(id);

    }
    
    
     public ArrayList<Combustible> buscarCargasChofer(Long idChofer) {
         
        Usuario chofer = usuarioRepositorio.getById(idChofer);

        ArrayList<Combustible> lista = combustibleRepositorio.findAllByUsuarioOrderByIdDesc(chofer);

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

        ArrayList<Combustible> lista = combustibleRepositorio.findByFechaCargaBetweenAndUsuario(d, h, chofer);

        Collections.sort(lista, CombustibleComparador.ordenarIdDesc);

        Combustible carga = lista.get(0);
        carga.setEstado("ULTIMO");
        
        return lista;
    }
     
    public ArrayList<Usuario> buscarConsumoCamiones() {

        ArrayList<Usuario> lista = usuarioRepositorio.buscarUsuariosChofer();
        
        for(Usuario u : lista){
            u.setPorcentaje(consumoPromedioCamion(u.getId()));
        }

        Collections.sort(lista, ChoferComparador.ordenarNombreAsc);

        return lista;
    }

    public Double consumoPromedioCamion(Long idChofer) {

        Usuario chofer = usuarioRepositorio.getById(idChofer);
        Double litros = 0.0;
        Double km = 0.0;
        Double redondeado = 0.0;

        ArrayList<Combustible> listaCargas = combustibleRepositorio.findAllByUsuarioOrderByIdDesc(chofer);

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
    
    public Double consumoPromedioTanque(Double consumo, Double km, Double litros, Usuario chofer){
        
        ArrayList<Combustible> listaPromedio = new ArrayList();
       
        Combustible ultimaCarga = combustibleRepositorio.findTopByUsuarioOrderByIdDesc(chofer);
        
        if(ultimaCarga.getCompleto().equalsIgnoreCase("NO")){  
            
            ArrayList<Combustible> listaCargas = combustibleRepositorio.findAllByUsuarioOrderByIdDesc(chofer);
            
            for(Combustible c : listaCargas){
                if(c.getCompleto().equalsIgnoreCase("SI") || c.getConsumo() == null){
                    break;
                }
                listaPromedio.add(c);
            }  
        } 
        
        if(!listaPromedio.isEmpty()){  //si listaPromedio no está vacía
            
            km = km;
            litros = litros;
            
            for(Combustible c : listaPromedio){
                km = km + c.getKmRecorrido();
                litros = litros + c.getLitro();
                        
        }
        
           Double consumoPromedio = ((100*litros)/km);
           consumo = Math.round(consumoPromedio * 100.0) / 100.0;
            
        }
        
        return consumo;
    }
    
    public Long buscarUltimo() {

        return combustibleRepositorio.ultimaCarga();

    }
    
    public Combustible buscarCombustible(Long id){
        
        return combustibleRepositorio.getById(id);
    }
    
    public Double kmAnterior(Usuario chofer){
        
        Combustible ultimaCarga = combustibleRepositorio.findTopByUsuarioOrderByIdDesc(chofer);
        
        return ultimaCarga.getKmCarga();
        
    }
    
    public boolean kmIniciales(Long idChofer){
        
        boolean flag = false;
        
        Optional<Combustible> iniciales = combustibleRepositorio.findFirstByUsuarioOrderByIdAsc(idChofer);
        if (iniciales.isPresent()) {
            flag = true;
        }
        
        return flag;
        
    }
    
     public Date convertirFecha(String fecha) throws ParseException { 
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }
    
}
