
package abate.abate.util;

import abate.abate.entidades.Gasto;
import java.util.Comparator;


public class GastoComparador {
    
    public static Comparator<Gasto> ordenarFechaDesc = new Comparator<Gasto>() {
        @Override
        public int compare(Gasto g1, Gasto g2) {
            return g2.getFecha().compareTo(g1.getFecha());
        }
    };
    
}
