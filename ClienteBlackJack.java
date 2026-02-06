import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ClienteBlackJack extends JFrame implements Runnable {
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    DefaultListModel<String> modJ, modC;
    JLabel lblSaldo, lblMensaje;
    JTextField txtApuesta;

    public ClienteBlackJack() {
        super("BlackJack Real");
        setLayout(new BorderLayout());
        
        JPanel pnlNorte = new JPanel();
        lblSaldo = new JLabel("Saldo: 500");
        txtApuesta = new JTextField("20", 5);
        JButton btnAp = new JButton("Apostar");
        pnlNorte.add(lblSaldo); pnlNorte.add(new JLabel("Apuesta:")); pnlNorte.add(txtApuesta); pnlNorte.add(btnAp);
        
        modJ = new DefaultListModel<>(); modC = new DefaultListModel<>();
        JPanel pnlCartas = new JPanel(new GridLayout(1,2));
        pnlCartas.add(new JScrollPane(new JList<>(modJ)));
        pnlCartas.add(new JScrollPane(new JList<>(modC)));
        
        JPanel pnlSur = new JPanel();
        lblMensaje = new JLabel("Haz tu apuesta");
        JButton btnPedir = new JButton("Pedir");
        JButton btnPlan = new JButton("Plantar");
        pnlSur.add(lblMensaje); pnlSur.add(btnPedir); pnlSur.add(btnPlan);
        
        add(pnlNorte, BorderLayout.NORTH); add(pnlCartas, BorderLayout.CENTER); add(pnlSur, BorderLayout.SOUTH);

        btnAp.addActionListener(e -> enviar(AccionJugador.APOSTAR, Double.parseDouble(txtApuesta.getText())));
        btnPedir.addActionListener(e -> enviar(AccionJugador.PEDIR, 0));
        btnPlan.addActionListener(e -> enviar(AccionJugador.PLANTARSE, 0));

        setSize(500, 400); setVisible(true); setDefaultCloseOperation(3);
    }

    private void enviar(int act, double d) {
        try { out.writeObject(new AccionJugador(act, d)); } catch(Exception e){}
    }

    public void run() {
        try {
            socket = new Socket("localhost", 44441);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            while(true) {
                EstadoJuego ej = (EstadoJuego) in.readObject();
                lblSaldo.setText("Saldo: " + ej.miSaldo);
                lblMensaje.setText(ej.mensaje);
                modJ.clear(); for(String c : ej.misCartas) modJ.addElement(c);
                modC.clear(); for(String c : ej.cartasCrupier) modC.addElement(c);
            }
        } catch(Exception e){}
    }

    public static void main(String[] args) { new Thread(new ClienteBlackJack()).start(); }
}