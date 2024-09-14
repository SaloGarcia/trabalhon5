import java.io.*;
import java.net.Socket;
import java.util.List;
import model.Produto;

public class CadastroClient {
    public static void main(String[] args) {
        String login = "op1"; // Credenciais de exemplo
        String senha = "op1";

        try (Socket socket = new Socket("localhost", 4321);
             ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

            saida.writeObject(login);
            saida.writeObject(senha);

            String resposta = (String) entrada.readObject();
            System.out.println("Servidor: " + resposta);

            if (resposta.contains("Login válido")) {
                saida.writeObject("L");

                @SuppressWarnings("unchecked")
                List<Produto> produtos = (List<Produto>) entrada.readObject();

                for (Produto produto : produtos) {
                    System.out.println("Produto: " + produto.getNome());
                }
            } else {
                System.out.println("Falha no login. Desconectando...");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
