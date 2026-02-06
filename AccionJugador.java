import java.io.Serializable;

public class AccionJugador implements Serializable {
    public static final int PEDIR = 1;
    public static final int PLANTARSE = 2;
    public static final int APOSTAR = 3;

    public int numeroAccion;
    public double dinero; 

    public AccionJugador(int numeroAccion, double dinero) {
        this.numeroAccion = numeroAccion;
        this.dinero = dinero;
    }
}