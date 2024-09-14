import java.io.*;
import java.net.*;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import model.Produto;

public class CadastroServer {
    private static final int PORT = 4321;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado e aguardando conexões na porta " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                new ClientHandler(clientSocket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private ProdutoJpaController ctrl;
        private UsuarioJpaController ctrlUsu;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;

            // Instanciar os controladores
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
            this.ctrl = new ProdutoJpaController(emf);
            this.ctrlUsu = new UsuarioJpaController(emf);
        }

        @Override
        public void run() {
            try (
                ObjectInputStream entrada = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream saida = new ObjectOutputStream(clientSocket.getOutputStream())
            ) {
                // Recebe login e senha do cliente
                String login = (String) entrada.readObject();
                String senha = (String) entrada.readObject();
                System.out.println("Recebido: Login = " + login + ", Senha = " + senha);

                // Validação das credenciais
                if (validarCredenciais(login, senha)) {
                    saida.writeObject("Login válido. Conectado ao servidor.");

                    // Aguarda comando do cliente
                    String comando;
                    while ((comando = (String) entrada.readObject()) != null) {
                        if (comando.equalsIgnoreCase("L")) {
                            // Retorna o conjunto de produtos
                            List<?> produtos = ctrl.findProdutoEntities();
                            saida.writeObject(produtos);
                        } else {
                            saida.writeObject("Comando não reconhecido.");
                        }
                    }
                } else {
                    saida.writeObject("Login inválido. Desconectando...");
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean validarCredenciais(String login, String senha) {
            
            return "op1".equals(login) && "op1".equals(senha); // Credenciais de exemplo
        }
    }
}
