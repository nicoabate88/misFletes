package abate.abate.util;

import abate.abate.entidades.Transaccion;
import java.util.Comparator;

public class TransaccionComparador {

    public static Comparator<Transaccion> ordenarIdDesc = new Comparator<Transaccion>() {
        @Override
        public int compare(Transaccion t1, Transaccion t2) {
            return t2.getId().compareTo(t1.getId());
        }
    };

    public static Comparator<Transaccion> ordenarFechaDesc = new Comparator<Transaccion>() {
        @Override
        public int compare(Transaccion t1, Transaccion t2) {
            return t2.getFecha().compareTo(t1.getFecha());
        }
    };

    public static Comparator<Transaccion> ordenarFechaAcs = new Comparator<Transaccion>() {
        @Override
        public int compare(Transaccion t1, Transaccion t2) {
            return t1.getFecha().compareTo(t2.getFecha());
        }
    };

}
