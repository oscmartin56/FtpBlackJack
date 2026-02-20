import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

// GUI del juego que implementa Runnable para red en segundo plano (RA3)
public class ClienteBlackJack extends JFrame implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JButton btnAp, btnPedir, btnPlan;
    private DefaultListModel<String> modJ, modC;
    private JLabel lblSaldo, lblMsg;
    private JTextField txtAp;

    public ClienteBlackJack() {
        super("BlackJack Real - PSP");
        setLayout(new BorderLayout());

        // Inicialización (Evita el NullPointerException)
        btnAp = new JButton("Apostar");
        btnPedir = new JButton("Pedir");
        btnPlan = new JButton("Plantar");
        lblSaldo = new JLabel("Saldo: 500");
        lblMsg = new JLabel("Introduce apuesta");
        txtAp = new JTextField("20", 5);
        modJ = new DefaultListModel<>(); modC = new DefaultListModel<>();

        // Montaje de UI
        JPanel norte = new JPanel(); norte.add(lblSaldo); norte.add(txtAp); norte.add(btnAp);
        JPanel centro = new JPanel(new GridLayout(1,2));
        centro.add(new JScrollPane(new JList<>(modJ)));
        centro.add(new JScrollPane(new JList<>(modC)));
        JPanel sur = new JPanel(); sur.add(lblMsg); sur.add(btnPedir); sur.add(btnPlan);

        add(norte, BorderLayout.NORTH); add(centro, BorderLayout.CENTER); add(sur, BorderLayout.SOUTH);

        // Listeners: Envío seguro de objetos (RA4)
        btnAp.addActionListener(e -> enviar(3, Double.parseDouble(txtAp.getText())));
        btnPedir.addActionListener(e -> enviar(1, 0));
        btnPlan.addActionListener(e -> enviar(2, 0));

        setSize(600, 400); setVisible(true); setDefaultCloseOperation(3);
    }

    private void enviar(int act, double d) {
        try {
            out.writeObject(new AccionJugador(act, d));
            out.flush(); // RA3: Asegura salida de datos
        } catch(Exception e){ lblMsg.setText("Error de envío."); }
    }

    // Dentro de la clase ClienteBlackJack, modifica el método run:

public void run() {
    try {
        // RA3: Conexión mediante Socket TCP
        socket = new Socket("localhost", 44441);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        while(true) {
            // RA3: Recepción de objetos serializados
            EstadoJuego ej = (EstadoJuego) in.readObject();
            
            // Actualización de la GUI
            lblSaldo.setText("Saldo: " + ej.miSaldo);
            lblMsg.setText(ej.mensaje);
            
            modJ.clear(); 
            for(String c : ej.misCartas) modJ.addElement(c);
            
            modC.clear(); 
            for(String c : ej.cartasCrupier) modC.addElement(c);
            
            // RA4: Protocolo de aplicación - Control funcional de la interfaz
            // Si la partida NO ha finalizado, habilita PEDIR y PLANTAR
            btnPedir.setEnabled(!ej.partidaFinalizada);
            btnPlan.setEnabled(!ej.partidaFinalizada);
            
            // El botón Apostar solo se habilita al terminar la ronda
            btnAp.setEnabled(ej.partidaFinalizada);
            txtAp.setEditable(ej.partidaFinalizada);
        }
    } catch(Exception e){
        lblMsg.setText("ERROR: Conexión perdida.");
    }
}

    public static void main(String[] args) { new Thread(new ClienteBlackJack()).start(); }
}