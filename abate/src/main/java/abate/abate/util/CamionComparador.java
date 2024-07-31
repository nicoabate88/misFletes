
package abate.abate.util;

import abate.abate.entidades.Camion;
import java.util.Comparator;


public class CamionComparador {
    
    public static Comparator<Camion> ordenarDominioAsc = new Comparator<Camion>() {
        @Override
        public int compare(Camion c1, Camion c2) {
            return c1.getDominio().compareTo(c2.getDominio());
        }
    };
    
}
