import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;

public class Main {

    public static void main(String[] args) {
        // Passo 1: Instanciar o EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");

        // Passo 2: Instanciar os controladores
        ProdutoJpaController produtoCtrl = new ProdutoJpaController(emf);
        UsuarioJpaController usuarioCtrl = new UsuarioJpaController(emf);

        // Passo 3: Instanciar o ServerSocket na porta 4321
        try (ServerSocket serverSocket = new ServerSocket(4321)) {
            System.out.println("Servidor iniciado na porta 4321");

            // Passo 4: Loop infinito para aceitar conexões
            while (true) {
                // Aceitar uma conexão de cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                // Criar e iniciar uma nova Thread para gerenciar a conexão do cliente
                CadastroThread clienteThread = new CadastroThread(produtoCtrl, usuarioCtrl, clientSocket);
                new Thread(clienteThread).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
