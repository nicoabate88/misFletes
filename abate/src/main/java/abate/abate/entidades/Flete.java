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
public class Flete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idFlete;
    private Long idOrg;
    @Temporal(TemporalType.DATE)
    private Date fechaCarga;
    @OneToOne
    private Cliente cliente;
    private String origenFlete;
    @Temporal(TemporalType.DATE)
    private Date fechaFlete;
    private String destinoFlete;
    private Double kmFlete;
    private String tipoCereal;
    private Double tarifa;
    private String cartaPorte;
    private String ctg;
    private Double kgFlete;
    @OneToOne
    private Usuario chofer;
    @OneToOne
    private Camion camion;
    @OneToOne
    private Usuario usuario;
    private Double neto;
    private Double iva;
    private Double total;
    private Double porcientoChofer;
    private Double porcentajeChofer;
    private Double comisionTpte;
    private Double comisionTpteValor;
    private String comisionTpteChofer;
    private String estado;
    private String kgFleteS;
    private String tarifaS;
    private String comisionS;
    private String netoS;
    private String ivaS;
    private String totalS;
    private String gananciaChofer;
    @OneToOne
    private Gasto gasto;
    @OneToOne
    private Imagen imagenCP;
    @OneToOne
    private Imagen imagenDescarga;

    public Flete() {
    }

    public Flete(Long id, Long idFlete, Long idOrg, Date fechaCarga, Cliente cliente, String origenFlete, Date fechaFlete, String destinoFlete, Double kmFlete, String tipoCereal, Double tarifa, String cartaPorte, String ctg, Double kgFlete, Usuario chofer, Camion camion, Usuario usuario, Double neto, Double iva, Double total, Double porcientoChofer, Double porcentajeChofer, Double comisionTpte, Double comisionTpteValor, String comisionTpteChofer, String estado, String kgFleteS, String tarifaS, String comisionS, String netoS, String ivaS, String totalS, String gananciaChofer, Gasto gasto, Imagen imagenCP, Imagen imagenDescarga) {
        this.id = id;
        this.idFlete = idFlete;
        this.idOrg = idOrg;
        this.fechaCarga = fechaCarga;
        this.cliente = cliente;
        this.origenFlete = origenFlete;
        this.fechaFlete = fechaFlete;
        this.destinoFlete = destinoFlete;
        this.kmFlete = kmFlete;
        this.tipoCereal = tipoCereal;
        this.tarifa = tarifa;
        this.cartaPorte = cartaPorte;
        this.ctg = ctg;
        this.kgFlete = kgFlete;
        this.chofer = chofer;
        this.camion = camion;
        this.usuario = usuario;
        this.neto = neto;
        this.iva = iva;
        this.total = total;
        this.porcientoChofer = porcientoChofer;
        this.porcentajeChofer = porcentajeChofer;
        this.comisionTpte = comisionTpte;
        this.comisionTpteValor = comisionTpteValor;
        this.comisionTpteChofer = comisionTpteChofer;
        this.estado = estado;
        this.kgFleteS = kgFleteS;
        this.tarifaS = tarifaS;
        this.comisionS = comisionS;
        this.netoS = netoS;
        this.ivaS = ivaS;
        this.totalS = totalS;
        this.gananciaChofer = gananciaChofer;
        this.gasto = gasto;
        this.imagenCP = imagenCP;
        this.imagenDescarga = imagenDescarga;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdFlete() {
        return idFlete;
    }

    public void setIdFlete(Long idFlete) {
        this.idFlete = idFlete;
    }

    public Long getIdOrg() {
        return idOrg;
    }

    public void setIdOrg(Long idOrg) {
        this.idOrg = idOrg;
    }

    public Date getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getOrigenFlete() {
        return origenFlete;
    }

    public void setOrigenFlete(String origenFlete) {
        this.origenFlete = origenFlete;
    }

    public Date getFechaFlete() {
        return fechaFlete;
    }

    public void setFechaFlete(Date fechaFlete) {
        this.fechaFlete = fechaFlete;
    }

    public String getDestinoFlete() {
        return destinoFlete;
    }

    public void setDestinoFlete(String destinoFlete) {
        this.destinoFlete = destinoFlete;
    }

    public Double getKmFlete() {
        return kmFlete;
    }

    public void setKmFlete(Double kmFlete) {
        this.kmFlete = kmFlete;
    }

    public String getTipoCereal() {
        return tipoCereal;
    }

    public void setTipoCereal(String tipoCereal) {
        this.tipoCereal = tipoCereal;
    }

    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(Double tarifa) {
        this.tarifa = tarifa;
    }

    public String getCartaPorte() {
        return cartaPorte;
    }

    public void setCartaPorte(String cartaPorte) {
        this.cartaPorte = cartaPorte;
    }

    public String getCtg() {
        return ctg;
    }

    public void setCtg(String ctg) {
        this.ctg = ctg;
    }

    public Double getKgFlete() {
        return kgFlete;
    }

    public void setKgFlete(Double kgFlete) {
        this.kgFlete = kgFlete;
    }

    public Usuario getChofer() {
        return chofer;
    }

    public void setChofer(Usuario chofer) {
        this.chofer = chofer;
    }

    public Camion getCamion() {
        return camion;
    }

    public void setCamion(Camion camion) {
        this.camion = camion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Double getNeto() {
        return neto;
    }

    public void setNeto(Double neto) {
        this.neto = neto;
    }

    public Double getIva() {
        return iva;
    }

    public void setIva(Double iva) {
        this.iva = iva;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getPorcientoChofer() {
        return porcientoChofer;
    }

    public void setPorcientoChofer(Double porcientoChofer) {
        this.porcientoChofer = porcientoChofer;
    }

    public Double getPorcentajeChofer() {
        return porcentajeChofer;
    }

    public void setPorcentajeChofer(Double porcentajeChofer) {
        this.porcentajeChofer = porcentajeChofer;
    }

    public Double getComisionTpte() {
        return comisionTpte;
    }

    public void setComisionTpte(Double comisionTpte) {
        this.comisionTpte = comisionTpte;
    }

    public Double getComisionTpteValor() {
        return comisionTpteValor;
    }

    public void setComisionTpteValor(Double comisionTpteValor) {
        this.comisionTpteValor = comisionTpteValor;
    }

    public String getComisionTpteChofer() {
        return comisionTpteChofer;
    }

    public void setComisionTpteChofer(String comisionTpteChofer) {
        this.comisionTpteChofer = comisionTpteChofer;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getKgFleteS() {
        return kgFleteS;
    }

    public void setKgFleteS(String kgFleteS) {
        this.kgFleteS = kgFleteS;
    }

    public String getTarifaS() {
        return tarifaS;
    }

    public void setTarifaS(String tarifaS) {
        this.tarifaS = tarifaS;
    }

    public String getComisionS() {
        return comisionS;
    }

    public void setComisionS(String comisionS) {
        this.comisionS = comisionS;
    }

    public String getNetoS() {
        return netoS;
    }

    public void setNetoS(String netoS) {
        this.netoS = netoS;
    }

    public String getIvaS() {
        return ivaS;
    }

    public void setIvaS(String ivaS) {
        this.ivaS = ivaS;
    }

    public String getTotalS() {
        return totalS;
    }

    public void setTotalS(String totalS) {
        this.totalS = totalS;
    }

    public String getGananciaChofer() {
        return gananciaChofer;
    }

    public void setGananciaChofer(String gananciaChofer) {
        this.gananciaChofer = gananciaChofer;
    }

    public Gasto getGasto() {
        return gasto;
    }

    public void setGasto(Gasto gasto) {
        this.gasto = gasto;
    }

    public Imagen getImagenCP() {
        return imagenCP;
    }

    public void setImagenCP(Imagen imagenCP) {
        this.imagenCP = imagenCP;
    }

    public Imagen getImagenDescarga() {
        return imagenDescarga;
    }

    public void setImagenDescarga(Imagen imagenDescarga) {
        this.imagenDescarga = imagenDescarga;
    }

    
}
