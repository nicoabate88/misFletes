package abate.abate.util;

import abate.abate.entidades.Cliente;
import java.util.Comparator;

public class ClienteComparador {

    public static Comparator<Cliente> ordenarNombreAsc = new Comparator<Cliente>() {
        @Override
        public int compare(Cliente c1, Cliente c2) {
            return c1.getNombre().compareTo(c2.getNombre());
        }
    };
}
