import java.io.*;
import java.net.*;

public class Servidor {
    static int PUERTO = 44441; // Usamos un puerto libre

    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(PUERTO);
        System.out.println("Servidor de BlackJack iniciado en el puerto " + PUERTO);

        while (true) {
            try {
                Socket cliente = servidor.accept(); // Espera al jugador
                System.out.println("¡Jugador conectado desde " + cliente.getInetAddress() + "!");
                
                // Creamos un hilo para este jugador
                HiloServidor hilo = new HiloServidor(cliente);
                hilo.start(); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}