import java.io.*;
import java.net.*;
import java.util.ArrayList;

class HiloServidor extends Thread {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Baraja baraja = new Baraja();
    private double saldo = 500.0, apuesta = 0;
    private ArrayList<String> manoJ, manoC;

    public HiloServidor(Socket s) { this.socket = s; }

    // RA4: Servicio funcional - Lógica real de Blackjack para el AS
    private int sumar(ArrayList<String> mano) {
        int pts = 0, ases = 0;
        for (String c : mano) {
            int v = baraja.valorDe(c);
            if (v == 11) ases++;
            pts += v;
        }
        // Si se pasa de 21, los Ases valen 1 (restamos 10)
        while (pts > 21 && ases > 0) { pts -= 10; ases--; }
        return pts;
    }

    public void run() {
        try {
            // RA3: Gestión de flujos (Streams)
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                // RA4: Protocolo de aplicación - Recepción de objeto
                Object obj = in.readObject();
                if (obj instanceof AccionJugador) {
                    AccionJugador acc = (AccionJugador) obj;
                    EstadoJuego ej = new EstadoJuego();

                    if (acc.numeroAccion == AccionJugador.APOSTAR) {
                        apuesta = acc.dinero; saldo -= apuesta;
                        manoJ = new ArrayList<>(); manoC = new ArrayList<>();
                        manoJ.add(baraja.sacarCarta()); manoJ.add(baraja.sacarCarta());
                        manoC.add(baraja.sacarCarta());
                        ej.mensaje = "Puntos: " + sumar(manoJ) + ". ¿Carta o Plantas?";
                        ej.partidaFinalizada = false; // La partida empieza
                    } 
                    else if (acc.numeroAccion == AccionJugador.PEDIR) {
                        manoJ.add(baraja.sacarCarta());
                        int p = sumar(manoJ);
                        if (p > 21) {
                            ej.mensaje = "TE PASASTE (" + p + "). Perdiste " + apuesta;
                            ej.partidaFinalizada = true; // Aquí sí bloquea botones
                        } else {
                            ej.mensaje = "Tienes " + p + ". ¿Otra?";
                            ej.partidaFinalizada = false; // SIGUE JUGANDO (No bloquea)
                        }
                    }
                    else if (acc.numeroAccion == AccionJugador.PLANTARSE) {
                        while (sumar(manoC) < 17) manoC.add(baraja.sacarCarta());
                        int pJ = sumar(manoJ), pC = sumar(manoC);
                        if (pC > 21 || pJ > pC) {
                            saldo += apuesta * 2;
                            ej.mensaje = "¡GANASTE! Crupier tiene " + pC;
                        } else if (pJ == pC) {
                            saldo += apuesta;
                            ej.mensaje = "Empate. Recuperas apuesta.";
                        } else {
                            ej.mensaje = "GANA LA CASA con " + pC;
                        }
                        ej.partidaFinalizada = true; // Fin de ronda
                    }

                    ej.miSaldo = saldo; ej.misCartas = manoJ; ej.cartasCrupier = manoC;
                    
                    // RA3: Limpieza de flujo para evitar datos corruptos
                    out.reset(); 
                    out.writeObject(ej);
                    out.flush(); 
                }
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectado.");
        } finally {
            // RA3: Cierre de recursos obligatorio
            try { if (socket != null) socket.close(); } catch (IOException e) {}
        }
    }
}