import java.io.IOException;
import javax.swing.*;
import java.io.ObjectInputStream;
import java.util.List;
import model.Produto;



public class ThreadClient extends Thread {
    private final ObjectInputStream entrada;
    private final JTextArea textArea;

    // Construtor da ThreadClient
    public ThreadClient(ObjectInputStream entrada, JTextArea textArea) {
        this.entrada = entrada;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Ler objeto do servidor
                Object obj = entrada.readObject();

                // Atualizar o JTextArea com base no tipo de objeto recebido
                if (obj instanceof String mensagem) {
                    SwingUtilities.invokeLater(() -> textArea.append(mensagem + "\n"));
                } else if (obj instanceof List) {
                    List<?> lista = (List<?>) obj;
                    StringBuilder sb = new StringBuilder();
                    for (Object item : lista) {
                        if (item instanceof Produto produto) {
                            sb.append("Nome: ").append(produto.getNome())
                              .append(", Quantidade: ").append(produto.getQuantidade())
                              .append("\n");
                        }
                    }
                    String resultado = sb.toString();
                    SwingUtilities.invokeLater(() -> textArea.append(resultado));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
        }
    }
}
