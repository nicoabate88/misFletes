package abate.abate.util;

import abate.abate.entidades.Recibo;
import java.util.Comparator;

public class ReciboComparador {

    public static Comparator<Recibo> ordenarFechaDesc = new Comparator<Recibo>() {
        @Override
        public int compare(Recibo r1, Recibo r2) {
            return r2.getFecha().compareTo(r1.getFecha());
        }
    };

}
