import java.io.*;
import java.net.*;

// Clase principal que escucha conexiones (RA3: Utiliza Socket)
public class Servidor {
    static int PUERTO = 44441; 

    public static void main(String[] args) {
        // try-with-resources para cierre automático del servidor (RA3)
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor BlackJack iniciado en puerto " + PUERTO);

            while (true) {
                // Bloqueo hasta que se conecte un cliente
                Socket cliente = servidor.accept(); 
                System.out.println("Jugador conectado: " + cliente.getInetAddress());
                
                // RA3: Gestión de concurrencia mediante Hilos (Hilos para clientes)
                new HiloServidor(cliente).start(); 
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}