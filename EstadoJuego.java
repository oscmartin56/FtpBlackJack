import java.io.Serializable;
import java.util.ArrayList;

public class EstadoJuego implements Serializable {
    private static final long serialVersionUID = 1L;
    public ArrayList<String> misCartas; 
    public ArrayList<String> cartasCrupier;
    public double miSaldo;     
    public String mensaje;      
    public boolean partidaFinalizada; 

    public EstadoJuego() {
        misCartas = new ArrayList<>();
        cartasCrupier = new ArrayList<>();
        miSaldo = 100.0; 
        partidaFinalizada = true; 
    }
}