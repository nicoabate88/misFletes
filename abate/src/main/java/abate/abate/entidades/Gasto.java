package abate.abate.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idGasto;
    private Long idOrg;
    private String estado;
    @OneToOne
    private Usuario chofer;
    @OneToOne
    private Usuario usuario;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private String nombre;
    private Double importe;
    private String importeS;
    @OneToOne
    private Camion camion;
    @OneToOne
    private Imagen imagen;

    public Gasto() {
    }

    public Gasto(Long id, Long idGasto, Long idOrg, String estado, Usuario chofer, Usuario usuario, Date fecha, String nombre, Double importe, String importeS, Camion camion, Imagen imagen) {
        this.id = id;
        this.idGasto = idGasto;
        this.idOrg = idOrg;
        this.estado = estado;
        this.chofer = chofer;
        this.usuario = usuario;
        this.fecha = fecha;
        this.nombre = nombre;
        this.importe = importe;
        this.importeS = importeS;
        this.camion = camion;
        this.imagen = imagen;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdGasto() {
        return idGasto;
    }

    public void setIdGasto(Long idGasto) {
        this.idGasto = idGasto;
    }

    public Long getIdOrg() {
        return idOrg;
    }

    public void setIdOrg(Long idOrg) {
        this.idOrg = idOrg;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getChofer() {
        return chofer;
    }

    public void setChofer(Usuario chofer) {
        this.chofer = chofer;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public String getImporteS() {
        return importeS;
    }

    public void setImporteS(String importeS) {
        this.importeS = importeS;
    }

    public Camion getCamion() {
        return camion;
    }

    public void setCamion(Camion camion) {
        this.camion = camion;
    }

    public Imagen getImagen() {
        return imagen;
    }

    public void setImagen(Imagen imagen) {
        this.imagen = imagen;
    }

    
    
}
