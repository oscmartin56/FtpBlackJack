import java.io.Serializable;

// Clase para enviar órdenes del cliente al servidor (RA4)
public class AccionJugador implements Serializable {
    private static final long serialVersionUID = 1L;
    // Constantes para identificar la acción
    public static final int PEDIR = 1, PLANTARSE = 2, APOSTAR = 3;

    public int numeroAccion; // Código de la jugada
    public double dinero;    // Cantidad apostada

    public AccionJugador(int numeroAccion, double dinero) {
        this.numeroAccion = numeroAccion;
        this.dinero = dinero;
    }
}