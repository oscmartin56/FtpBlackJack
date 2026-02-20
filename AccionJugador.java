import java.io.Serializable;

public class AccionJugador implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int PEDIR = 1, PLANTARSE = 2, APOSTAR = 3;

    public int numeroAccion; 
    public double dinero;

    public AccionJugador(int numeroAccion, double dinero) {
        this.numeroAccion = numeroAccion;
        this.dinero = dinero;
    }
}