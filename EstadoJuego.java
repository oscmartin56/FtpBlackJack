import java.io.Serializable;
import java.util.ArrayList;

public class EstadoJuego implements Serializable {
    public ArrayList<String> misCartas; 
    public ArrayList<String> cartasCrupier;
    public double miSaldo;     
    public double apuestaActual;
    public String mensaje;      
    public boolean partidaFinalizada; 

    public EstadoJuego() {
        misCartas = new ArrayList<>();
        cartasCrupier = new ArrayList<>();
        miSaldo = 100.0; 
        partidaFinalizada = false;
    }
}