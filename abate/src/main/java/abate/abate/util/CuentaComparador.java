package abate.abate.util;

import abate.abate.entidades.Cuenta;
import java.util.Comparator;

public class CuentaComparador {

    public static Comparator<Cuenta> ordenarNombreClienteAsc = new Comparator<Cuenta>() {
        @Override
        public int compare(Cuenta c1, Cuenta c2) {
            return c1.getCliente().getNombre().compareTo(c2.getCliente().getNombre());
        }
    };

    public static Comparator<Cuenta> ordenarNombreChoferAsc = new Comparator<Cuenta>() {
        @Override
        public int compare(Cuenta c1, Cuenta c2) {
            return c1.getChofer().getNombre().compareTo(c2.getChofer().getNombre());
        }
    };

    public static Comparator<Cuenta> ordenarIdDesc = new Comparator<Cuenta>() {
        @Override
        public int compare(Cuenta c1, Cuenta c2) {
            return c1.getId().compareTo(c2.getId());
        }
    };

    public static Comparator<Cuenta> ordenarSaldoDesc = new Comparator<Cuenta>() {
        @Override
        public int compare(Cuenta c1, Cuenta c2) {
            return c2.getSaldo().compareTo(c1.getSaldo());
        }
    };

}
