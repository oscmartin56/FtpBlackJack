import java.io.Serializable;
import java.util.ArrayList;

// Clase que contiene la "foto" de la partida enviada por el servidor (RA4)
public class EstadoJuego implements Serializable {
    private static final long serialVersionUID = 1L;
    public ArrayList<String> misCartas; 
    public ArrayList<String> cartasCrupier;
    public double miSaldo;     
    public String mensaje;      
    public boolean partidaFinalizada; // Controla el estado del servicio funcional

    public EstadoJuego() {
        misCartas = new ArrayList<>();
        cartasCrupier = new ArrayList<>();
        miSaldo = 100.0; 
        partidaFinalizada = true; // Por defecto espera apuesta
    }
}