package abate.abate.util;

import abate.abate.entidades.Usuario;
import java.util.Comparator;

public class ChoferComparador {

    public static Comparator<Usuario> ordenarNombreAsc = new Comparator<Usuario>() {
        @Override
        public int compare(Usuario u1, Usuario u2) {
            return u1.getNombre().compareTo(u2.getNombre());
        }
    };

}
