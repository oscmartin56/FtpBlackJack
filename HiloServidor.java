import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.security.MessageDigest; 

class HiloServidor extends Thread {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Baraja baraja = new Baraja();
    private double saldo = 500.0, apuesta = 0;
    private ArrayList<String> manoJ, manoC;

    public HiloServidor(Socket s) { this.socket = s; }

    private String aplicarSeguridad(String datos) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(datos.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString(); 
        } catch (Exception e) {
            return "error_seguridad";
        }
    }

    private int sumar(ArrayList<String> mano) {
        int pts = 0, ases = 0;
        for (String c : mano) {
            int v = baraja.valorDe(c);
            if (v == 11) ases++;
            pts += v;
        }
        while (pts > 21 && ases > 0) { pts -= 10; ases--; }
        return pts;
    }

    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Object obj = in.readObject();
                if (obj instanceof AccionJugador) {
                    AccionJugador acc = (AccionJugador) obj;
                    EstadoJuego ej = new EstadoJuego();

                    if (acc.numeroAccion == AccionJugador.APOSTAR) {
                        apuesta = acc.dinero; saldo -= apuesta;
                        manoJ = new ArrayList<>(); manoC = new ArrayList<>();
                        manoJ.add(baraja.sacarCarta()); manoJ.add(baraja.sacarCarta());
                        manoC.add(baraja.sacarCarta());
                        ej.mensaje = "Apuesta aceptada. ¿Carta o Plantas?";
                        ej.partidaFinalizada = false;
                    } 
                    else if (acc.numeroAccion == AccionJugador.PEDIR) {
                        manoJ.add(baraja.sacarCarta());
                        int p = sumar(manoJ);
                        if (p > 21) {
                            ej.mensaje = "TE PASASTE (" + p + "). Perdiste " + apuesta;
                            ej.partidaFinalizada = true;
                        } else {
                            ej.mensaje = "Tienes " + p + ". ¿Otra?";
                            ej.partidaFinalizada = false;
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
                        ej.partidaFinalizada = true;
                    }

                    ej.miSaldo = saldo; ej.misCartas = manoJ; ej.cartasCrupier = manoC;
                    
                    String textoHash = aplicarSeguridad(ej.mensaje + ej.miSaldo);
                    ej.mensaje += " | [Codigo Hasheado: " + textoHash.substring(0, 8) + "]";

                    out.reset(); 
                    out.writeObject(ej);
                    out.flush(); 
                }
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectado.");
        } finally {
            try { if (socket != null) socket.close(); } catch (IOException e) {}
        }
    }
}