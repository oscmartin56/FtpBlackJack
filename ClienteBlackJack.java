import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

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

        btnAp = new JButton("Apostar");
        btnPedir = new JButton("Pedir");
        btnPlan = new JButton("Plantar");
        lblSaldo = new JLabel("Saldo: 500");
        lblMsg = new JLabel("Introduce apuesta");
        txtAp = new JTextField("20", 5);
        modJ = new DefaultListModel<>(); modC = new DefaultListModel<>();

        JPanel norte = new JPanel(); norte.add(lblSaldo); norte.add(txtAp); norte.add(btnAp);
        JPanel centro = new JPanel(new GridLayout(1,2));
        centro.add(new JScrollPane(new JList<>(modJ)));
        centro.add(new JScrollPane(new JList<>(modC)));
        JPanel sur = new JPanel(); sur.add(lblMsg); sur.add(btnPedir); sur.add(btnPlan);

        add(norte, BorderLayout.NORTH); add(centro, BorderLayout.CENTER); add(sur, BorderLayout.SOUTH);

        btnAp.addActionListener(e -> enviar(3, Double.parseDouble(txtAp.getText())));
        btnPedir.addActionListener(e -> enviar(1, 0));
        btnPlan.addActionListener(e -> enviar(2, 0));

        setSize(600, 400); setVisible(true); setDefaultCloseOperation(3);
    }

    private void enviar(int act, double d) {
        try {
            out.writeObject(new AccionJugador(act, d));
            out.flush();
        } catch(Exception e){ lblMsg.setText("Error de envío."); }
    }


public void run() {
    try {
        socket = new Socket("10.6.4.91", 44441);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        while(true) {
            EstadoJuego ej = (EstadoJuego) in.readObject();
            
            lblSaldo.setText("Saldo: " + ej.miSaldo);
            lblMsg.setText(ej.mensaje);
            
            modJ.clear(); 
            for(String c : ej.misCartas) modJ.addElement(c);
            
            modC.clear(); 
            for(String c : ej.cartasCrupier) modC.addElement(c);
            
            btnPedir.setEnabled(!ej.partidaFinalizada);
            btnPlan.setEnabled(!ej.partidaFinalizada);
            
            btnAp.setEnabled(ej.partidaFinalizada);
            txtAp.setEditable(ej.partidaFinalizada);
        }
    } catch(Exception e){
        lblMsg.setText("ERROR: Conexión perdida.");
    }
}

    public static void main(String[] args) { new Thread(new ClienteBlackJack()).start(); }
}