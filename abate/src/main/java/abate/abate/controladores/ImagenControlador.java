
package abate.abate.controladores;

import abate.abate.entidades.Combustible;
import abate.abate.entidades.Flete;
import abate.abate.entidades.Gasto;
import abate.abate.entidades.Imagen;
import abate.abate.entidades.Usuario;
import abate.abate.servicios.CombustibleServicio;
import abate.abate.servicios.DetalleServicio;
import abate.abate.servicios.FleteServicio;
import abate.abate.servicios.GastoServicio;
import abate.abate.servicios.ImagenServicio;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/imagen")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CHOFER')")
public class ImagenControlador {

    @Autowired
    private ImagenServicio imagenServicio;
    @Autowired
    private GastoServicio gastoServicio;
    @Autowired
    private FleteServicio fleteServicio;
    @Autowired
    private DetalleServicio detalleServicio;
    @Autowired
    private CombustibleServicio combustibleServicio;

    @GetMapping("/cargarGasto/{id}") //llega id de Flete
    public String registrar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("flete", fleteServicio.buscarFlete(id));

        return "imagen_gastoCargar.html";
    }

    @PostMapping("/cargaGasto")
    public String crearImagen(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {
        Flete flete = fleteServicio.buscarFlete(id);

        Imagen imagen = new Imagen();
        imagen.setNombre("Gasto Flete ID" + id);
        imagen.setTipo(file.getContentType()); 
        
        if(file.getContentType().equals("application/pdf")){
        imagen.setDatos(file.getBytes());
        } else {
            imagen.setDatos(optimizeImage(file));
        }

        imagenServicio.crearImagenGasto(id, imagen);

        modelo.put("gasto", gastoServicio.buscarGasto(flete.getGasto().getId()));
        modelo.put("id", id);
        modelo.addAttribute("detalles", detalleServicio.buscarDetallesFlete(id));
        modelo.put("exito", "Imagen de Gasto CARGADA con éxito");

        return "gasto_registradoImg.html";

    }
    
    @GetMapping("/cargarCombustible/{id}") 
    public String registrarCombustible(@PathVariable Long id, ModelMap modelo) {

        modelo.put("carga", combustibleServicio.buscarCombustible(id));

        return "imagen_combustibleCargar.html";
    }
    
    @PostMapping("/cargaCombustible")
    public String cargarImagenCombustible(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {
        
        Combustible carga = combustibleServicio.buscarCombustible(id);

        Imagen imagen = new Imagen();
        imagen.setNombre("Carga de Diesel " + carga.getFechaCarga());
        imagen.setTipo(file.getContentType()); 
        if(file.getContentType().equals("application/pdf")){
        imagen.setDatos(file.getBytes());
        } else {
            imagen.setDatos(optimizeImage(file));
        }

        imagenServicio.crearImagenCombustible(id, imagen);

        modelo.put("carga", carga);
        modelo.put("fecha", carga.getFechaCarga());
        modelo.put("exito", "Imagen de Diesel CARGADA con éxito");

        return "combustible_registrado.html";

    }

    @GetMapping("/cargarCPdesdeFlete/{id}") //llega id de Flete
    public String registrarCPdesdeFlete(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);

        modelo.put("flete", flete);

        return "imagen_CPcargarDesdeFlete.html";
    }

    @PostMapping("/cargaCPdesdeFlete")
    public String cargarImagenCPdesdeFlete(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {
        Flete flete = fleteServicio.buscarFlete(id);

        Imagen imagen = new Imagen();
        imagen.setNombre("CP Flete ID" + id);
        imagen.setTipo(file.getContentType()); 
        if(file.getContentType().equals("application/pdf")){
        imagen.setDatos(file.getBytes());
        } else {
            imagen.setDatos(optimizeImage(file));
        }

        imagenServicio.crearImagenCP(id, imagen);

        modelo.put("flete", flete);
        modelo.put("exito", "CP CARGADA con éxito");

        return "imagen_descargaCargarDesdeFlete.html";

    }

    @PostMapping("/cargaCP")
    public String cargarImagenCP(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo, HttpSession session) throws IOException {       
              
        Flete flete = fleteServicio.buscarFlete(id);
        
        Imagen imagen = new Imagen();
        imagen.setNombre("CP Flete ID" + id);
        imagen.setTipo(file.getContentType()); 
        if(file.getContentType().equals("application/pdf")){
        imagen.setDatos(file.getBytes());
        } else {
            imagen.setDatos(optimizeImage(file));
        }
        
        imagenServicio.crearImagenCP(id, imagen);
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if(logueado.getRol().equalsIgnoreCase("CHOFER")){
            
            modelo.put("flete", flete);
            modelo.put("fecha", flete.getFechaFlete());
            modelo.put("exito", "CP CARGADA con éxito");
            
            return "flete_registradoChofer.html";
            
        } else {
            
            modelo.put("flete", flete);
            modelo.put("fecha", flete.getFechaFlete());
            modelo.put("exito", "CP CARGADA con éxito");
            
            return "flete_registradoAdmin.html";
            
        }
        
    }

    @PostMapping("/cargaDescarga")
    public String cargarImagenDescarga(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo, HttpSession session) throws IOException {
        Flete flete = fleteServicio.buscarFlete(id);

        Imagen imagen = new Imagen();
        imagen.setNombre("Descarga Flete ID" + id);
        imagen.setTipo(file.getContentType()); 
        if(file.getContentType().equals("application/pdf")){
        imagen.setDatos(file.getBytes());
        } else {
            imagen.setDatos(optimizeImage(file));
        }

        imagenServicio.crearImagenDescarga(id, imagen);
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if(logueado.getRol().equalsIgnoreCase("CHOFER")){
            
            modelo.put("flete", flete);
            modelo.put("fecha", flete.getFechaFlete());
            modelo.put("exito", "Ticket de Descarga CARGADO con éxito");
            
            return "flete_registradoChofer.html";
            
        } else {
            
            modelo.put("flete", flete);
            modelo.put("fecha", flete.getFechaFlete());
            modelo.put("exito", "Ticket de Descarga CARGADO con éxito");
            
            return "flete_registradoAdmin.html";
            
        }

    }

    @PostMapping("/cargaDescargaDesdeFlete")
    public String cargarImagenDescargaDesdeFlete(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {
        Flete flete = fleteServicio.buscarFlete(id);

        Imagen imagen = new Imagen();
        imagen.setNombre("Descarga Flete ID" + id);
        imagen.setTipo(file.getContentType()); 
        if(file.getContentType().equals("application/pdf")){
        imagen.setDatos(file.getBytes());
        } else {
            imagen.setDatos(optimizeImage(file));
        }

        imagenServicio.crearImagenDescarga(id, imagen);

        modelo.put("flete", flete);
        modelo.put("exito", "Ticket de Descarga CARGADO con éxito");

        return "flete_agregarGasto.html";

    }

    @GetMapping("/verGasto/{id}")
    public String verGasto(@PathVariable Long id, ModelMap modelo) {

        Flete flete = fleteServicio.buscarFlete(id);
        Gasto gasto = gastoServicio.buscarGasto(flete.getGasto().getId());

        if (gasto.getImagen() != null) {

            Long idImagen = gasto.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);

                return "imagen_mostrarGastoPdf.html";

            } else {

                modelo.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                modelo.addAttribute("imagenNombre", imagen.getNombre());
                modelo.addAttribute("id", idImagen);

                return "imagen_mostrarGasto.html";

            }

        } else {

            modelo.put("flete", flete);

            return "imagen_gastoCargar.html";

        }
    }
    
    @GetMapping("/descargarGastoPdf/{id}")
    public String descargarGastoPdf(@PathVariable Long id, ModelMap modelo){
        
        Imagen imagen = imagenServicio.obtenerImagenPorId(id);
        
        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        
        return "imagen_descargarGastoPdf.html";
    }

    @GetMapping("/modificarGasto/{id}") //llega id de Imagen
    public String modificarGasto(@PathVariable Long id, ModelMap modelo) {

        modelo.put("id", id);

        return "imagen_gastoModificar.html";
    }

    @PostMapping("/modificaGasto")
    public String modificaGasto(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Gasto gasto = gastoServicio.buscarGastoIdImagen(id);
        Flete flete = fleteServicio.buscarFleteIdGasto(gasto.getId());

        Imagen imagen = new Imagen();
        imagen.setNombre("Gasto Flete ID" + flete.getId());
        imagen.setTipo(file.getContentType()); 
        if(file.getContentType().equals("application/pdf")){
        imagen.setDatos(file.getBytes());
        } else {
            imagen.setDatos(optimizeImage(file));
        }

        imagenServicio.modificarImagen(id, imagen);

        modelo.put("gasto", gasto);
        modelo.addAttribute("detalles", detalleServicio.buscarDetallesFlete(flete.getId()));
        modelo.put("exito", "Imagen de Gasto MODIFICADO con éxito");

        return "gasto_registradoImg.html";

    }

    @GetMapping("/verCP/{id}")
    public String ver(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getImagenCP() != null) {

            Long idImagen = flete.getImagenCP().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCPpdf.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCP.html";
            }

        } else {

            model.addAttribute("flete", flete);

            return "imagen_CPcargar.html";

        }
    }
    
    @GetMapping("/descargarCPpdf/{id}")
    public String descargarCPpdf(@PathVariable Long id, ModelMap modelo){
        
        Imagen imagen = imagenServicio.obtenerImagenPorId(id);
        
        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        
        return "imagen_descargarCPpdf.html";
    }

    @GetMapping("/modificarCP/{id}") //llega id de Imagen
    public String modificarCP(@PathVariable Long id, ModelMap modelo) {

        modelo.put("id", id);

        return "imagen_CPmodificar.html";
    }

    @PostMapping("/modificaCP")
    public String modificaCP(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo, HttpSession session) throws IOException {

        Flete flete = fleteServicio.buscarFleteIdImagenCP(id);

        Imagen imagen = new Imagen();
        imagen.setNombre("CP Flete ID" + flete.getId());
        imagen.setTipo(file.getContentType()); 
        if(file.getContentType().equals("application/pdf")){
        imagen.setDatos(file.getBytes());
        } else {
            imagen.setDatos(optimizeImage(file));
        }

        imagenServicio.modificarImagen(id, imagen);

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if(logueado.getRol().equalsIgnoreCase("CHOFER")){
            
            modelo.put("flete", flete);
            modelo.put("fecha", flete.getFechaFlete());
            modelo.put("exito", "CP MODIFICADA con éxito");
            
            return "flete_registradoChofer.html";
            
        } else {
            
            modelo.put("flete", flete);
            modelo.put("fecha", flete.getFechaFlete());
            modelo.put("exito", "CP MODIFICADA con éxito");
            
            return "flete_registradoAdmin.html";
            
        }

    }

    @GetMapping("/verDescarga/{id}")
    public String verDescarga(@PathVariable Long id, Model model) {

        Flete flete = fleteServicio.buscarFlete(id);

        if (flete.getImagenDescarga() != null) {

            Long idImagen = flete.getImagenDescarga().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarDescargaPdf.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarDescarga.html";
                
            }
        
        } else {

            model.addAttribute("flete", flete);

            return "imagen_descargaCargar.html";
        }
    }
    
    @GetMapping("/descargarDescargaPdf/{id}")
    public String descargarDescargaPdf(@PathVariable Long id, ModelMap modelo){
        
        Imagen imagen = imagenServicio.obtenerImagenPorId(id);
        
        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        
        return "imagen_descargarDescargaPdf.html";
    }

    @GetMapping("/modificarDescarga/{id}") //llega id de Imagen
    public String modificarDescarga(@PathVariable Long id, ModelMap modelo) {

        modelo.put("id", id);

        return "imagen_descargaModificar.html";
    }

    @PostMapping("/modificaDescarga")
    public String modificaDescarga(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo, HttpSession session) throws IOException {

        Flete flete = fleteServicio.buscarFleteIdImagenDescarga(id);

        Imagen imagen = new Imagen();
        imagen.setNombre("Descarga Flete ID" + flete.getId());
        imagen.setTipo(file.getContentType()); 
        if(file.getContentType().equals("application/pdf")){
        imagen.setDatos(file.getBytes());
        } else {
            imagen.setDatos(optimizeImage(file));
        }

        imagenServicio.modificarImagen(id, imagen);

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if(logueado.getRol().equalsIgnoreCase("CHOFER")){
            
            modelo.put("flete", flete);
            modelo.put("fecha", flete.getFechaFlete());
            modelo.put("exito", "Ticket de Descarga MODIFICADO con éxito");
            
            return "flete_registradoChofer.html";
            
        } else {
            
            modelo.put("flete", flete);
            modelo.put("fecha", flete.getFechaFlete());
            modelo.put("exito", "Ticket de Descarga MODIFICADO con éxito");
            
            return "flete_registradoAdmin.html";
            
        }

    }
    
     @GetMapping("/verCombustible/{id}")
    public String verCombustible(@PathVariable Long id, Model model) {

        Combustible carga = combustibleServicio.buscarCombustible(id);

        if (carga.getImagen() != null) {

            Long idImagen = carga.getImagen().getId();
            Imagen imagen = imagenServicio.obtenerImagenPorId(idImagen);

            if (imagen.getTipo().equalsIgnoreCase("application/pdf")) {

                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCombustiblePdf.html";

            } else {

                model.addAttribute("imagenUrl", "/imagen/img/bytes/" + idImagen);
                model.addAttribute("imagenNombre", imagen.getNombre());
                model.addAttribute("id", idImagen);

                return "imagen_mostrarCombustible.html";
            }

        } else {

           model.addAttribute("carga", combustibleServicio.buscarCombustible(id));

          return "imagen_combustibleCargar.html";

        }
    }
    
    @GetMapping("/descargarCombustiblePdf/{id}")
    public String descargarCombustiblePdf(@PathVariable Long id, ModelMap modelo){
        
        Imagen imagen = imagenServicio.obtenerImagenPorId(id);
        
        modelo.addAttribute("imagenUrl", "/imagen/pdf/" + id);
        modelo.addAttribute("imagenNombre", imagen.getNombre());
        modelo.addAttribute("id", id);
        
        return "imagen_descargarCombustiblePdf.html";
    }
    
    @GetMapping("/modificarCombustible/{id}") //llega id de Imagen
    public String modificarCombustible(@PathVariable Long id, ModelMap modelo) {

        modelo.put("id", id);

        return "imagen_combustibleModificar.html";
    }

    @PostMapping("/modificaCombustible")
    public String modificaCombustible(@RequestParam Long id, @RequestParam("file") MultipartFile file, ModelMap modelo) throws IOException {

        Combustible carga = combustibleServicio.buscarCombustible(id);

        Imagen imagen = new Imagen();
        imagen.setNombre("Carga de Diesel " + carga.getFechaCarga());
        imagen.setTipo(file.getContentType()); 
        if(file.getContentType().equals("application/pdf")){
        imagen.setDatos(file.getBytes());
        } else {
            imagen.setDatos(optimizeImage(file));
        }

        imagenServicio.modificarImagen(id, imagen);
          
            modelo.put("carga", carga);
            modelo.put("fecha", carga.getFechaCarga());
            modelo.put("exito", "Imagen de Diesel MODIFICADO con éxito");
            
            return "combustible_mostrar.html";

    }
    
    public byte[] optimizeImage(MultipartFile file) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails.of(file.getInputStream())
              .size(1024, 768) // Ajusta el tamaño según tus necesidades
              .outputQuality(0.99) // Ajusta la calidad según tus necesidades
              .toOutputStream(outputStream);
    return outputStream.toByteArray();
}

    @GetMapping("/img/bytes/{id}")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) {
        Imagen imagen = imagenServicio.obtenerImagenPorId(id);
        if (imagen != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(imagen.getTipo()));
            return new ResponseEntity<>(imagen.getDatos(), headers, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long id) {
        Imagen imagen = imagenServicio.obtenerImagenPorId(id);
        byte[] pdfContent = imagen.getDatos();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", imagen.getNombre()+".pdf");
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
   


}
