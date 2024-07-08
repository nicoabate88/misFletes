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
    @OneToOne
    private Usuario chofer;
    @OneToOne
    private Usuario usuario;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private String nombre;
    private Double importe;
    @OneToOne
    private Imagen imagen;

    public Gasto() {
    }

    public Gasto(Long id, Usuario chofer, Usuario usuario, Date fecha, String nombre, Double importe, Imagen imagen) {
        this.id = id;
        this.chofer = chofer;
        this.usuario = usuario;
        this.fecha = fecha;
        this.nombre = nombre;
        this.importe = importe;
        this.imagen = imagen;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Imagen getImagen() {
        return imagen;
    }

    public void setImagen(Imagen imagen) {
        this.imagen = imagen;
    }

}
