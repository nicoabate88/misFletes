package abate.abate.util;

import abate.abate.entidades.Entrega;
import java.util.Comparator;

public class EntregaComparador {

    public static Comparator<Entrega> ordenarFechaDesc = new Comparator<Entrega>() {
        @Override
        public int compare(Entrega e1, Entrega e2) {
            return e2.getFecha().compareTo(e1.getFecha());
        }
    };

}
