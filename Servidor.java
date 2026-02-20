import java.io.*;
import java.net.*;

public class Servidor {
    static int PUERTO = 44441; 

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor BlackJack iniciado en puerto " + PUERTO);

            while (true) {
                Socket cliente = servidor.accept(); 
                System.out.println("Jugador conectado: " + cliente.getInetAddress());
                
                new HiloServidor(cliente).start(); 
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}