package abate.abate.util;

import abate.abate.entidades.Flete;
import java.util.Comparator;

public class FleteComparador {

    public static Comparator<Flete> ordenarFechaDesc = new Comparator<Flete>() {
        @Override
        public int compare(Flete f1, Flete f2) {
            return f2.getFechaFlete().compareTo(f1.getFechaFlete());
        }
    };

    public static Comparator<Flete> ordenarFechaAsc = new Comparator<Flete>() {
        @Override
        public int compare(Flete f1, Flete f2) {
            return f1.getFechaFlete().compareTo(f2.getFechaFlete());
        }
    };

    public static Comparator<Flete> ordenarIdDesc = new Comparator<Flete>() {
        @Override
        public int compare(Flete f1, Flete f2) {
            return f2.getId().compareTo(f1.getId());
        }
    };

}
