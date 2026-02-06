import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class HiloServidor extends Thread {
    Socket socket;
    ObjectOutputStream outObjeto;
    ObjectInputStream inObjeto;
    
    Baraja baraja = new Baraja();
    double saldo = 500.0;
    double apuestaTurno = 0;
    ArrayList<String> manoJugador, manoCrupier;

    public HiloServidor(Socket s) throws IOException {
        this.socket = s;
        outObjeto = new ObjectOutputStream(socket.getOutputStream());
        inObjeto = new ObjectInputStream(socket.getInputStream());
    }

    private int sumar(ArrayList<String> mano) {
        int puntos = 0;
        for (String c : mano) puntos += baraja.valorDe(c);
        return puntos;
    }

    public void run() {
        try {
            while (true) {
                Object obj = inObjeto.readObject();
                if (obj instanceof AccionJugador) {
                    AccionJugador accion = (AccionJugador) obj;
                    EstadoJuego estado = new EstadoJuego();

                    if (accion.numeroAccion == AccionJugador.APOSTAR) {
                        apuestaTurno = accion.dinero;
                        saldo -= apuestaTurno;
                        manoJugador = new ArrayList<>();
                        manoCrupier = new ArrayList<>();
                        
                        manoJugador.add(baraja.sacarCarta());
                        manoJugador.add(baraja.sacarCarta());
                        manoCrupier.add(baraja.sacarCarta()); // Solo una visible
                        
                        estado.mensaje = "Puntos: " + sumar(manoJugador) + ". ¿Carta o Te plantas?";
                    } 
                    else if (accion.numeroAccion == AccionJugador.PEDIR) {
                        manoJugador.add(baraja.sacarCarta());
                        int p = sumar(manoJugador);
                        if (p > 21) {
                            estado.mensaje = "¡Te has pasado! (" + p + ") Perdiste.";
                            estado.partidaFinalizada = true;
                        } else {
                            estado.mensaje = "Puntos: " + p + ". ¿Otra?";
                        }
                    } 
                    else if (accion.numeroAccion == AccionJugador.PLANTARSE) {
                        // El crupier revela y juega hasta 17
                        while (sumar(manoCrupier) < 17) {
                            manoCrupier.add(baraja.sacarCarta());
                        }
                        int pj = sumar(manoJugador);
                        int pc = sumar(manoCrupier);
                        
                        if (pc > 21 || pj > pc) {
                            saldo += apuestaTurno * 2;
                            estado.mensaje = "¡Ganas! Crupier tiene " + pc;
                        } else {
                            estado.mensaje = "Gana la casa con " + pc;
                        }
                        estado.partidaFinalizada = true;
                    }

                    estado.miSaldo = saldo;
                    estado.misCartas = manoJugador;
                    estado.cartasCrupier = manoCrupier;
                    outObjeto.writeObject(estado);
                }
            }
        } catch (Exception e) { }
    }
}