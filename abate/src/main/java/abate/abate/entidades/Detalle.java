package abate.abate.entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Detalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String concepto;
    private Integer cantidad;
    private Double precio;
    private Double total;
    private Long flete;
    private Long gasto;

    public Detalle() {
    }

    public Detalle(Long id, String concepto, Integer cantidad, Double precio, Double total, Long flete, Long gasto) {
        this.id = id;
        this.concepto = concepto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.total = total;
        this.flete = flete;
        this.gasto = gasto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Long getFlete() {
        return flete;
    }

    public void setFlete(Long flete) {
        this.flete = flete;
    }

    public Long getGasto() {
        return gasto;
    }

    public void setGasto(Long gasto) {
        this.gasto = gasto;
    }

    

}
