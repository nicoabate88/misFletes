
package abate.abate.util;

import abate.abate.entidades.CamionEstadistica;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class CamionEstadisticaComparador {
    
     public static Comparator<CamionEstadistica> ordenarMes = new Comparator<CamionEstadistica>() {
        
            @Override
            public int compare(CamionEstadistica o1, CamionEstadistica o2) {
                
                int yearComparison = Integer.compare(o2.getYear(), o1.getYear());
                if (yearComparison != 0) {
                    return yearComparison;
                }
               
                return Integer.compare(o2.getMonth(), o1.getMonth());
            }
        };
    }
    

