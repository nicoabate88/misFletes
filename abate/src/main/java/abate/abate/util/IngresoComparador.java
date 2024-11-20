
package abate.abate.util;

import abate.abate.entidades.Ingreso;
import java.util.Comparator;


public class IngresoComparador {
    
     public static Comparator<Ingreso> ordenarFechaDesc = new Comparator<Ingreso>() {
        @Override
        public int compare(Ingreso e1, Ingreso e2) {
            return e2.getFecha().compareTo(e1.getFecha());
        }
    };
    
}
