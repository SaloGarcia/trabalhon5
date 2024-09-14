import javax.swing.*;

public class SaidaFrame extends JDialog {
    private final JTextArea texto;

    public SaidaFrame(JFrame parent) {
        super(parent, "Saída", false);
        texto = new JTextArea();
        texto.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(texto);
        add(scrollPane);
        setBounds(100, 100, 400, 300);
    }

    public JTextArea getTexto() {
        return texto;
    }

    public void adicionarTexto(String mensagem) {
        SwingUtilities.invokeLater(() -> texto.append(mensagem + "\n"));
    }
}
