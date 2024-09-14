import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CadastroThread implements Runnable {

    private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;
    private Socket s1;

    /**
     * Construtor da classe CadastroThread.
     * @param ctrl Controlador de produtos.
     * @param ctrlUsu Controlador de usuários.
     * @param s1 Socket para comunicação.
     */
    public CadastroThread(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.s1 = s1;
    }

    @Override
    public void run() {
        try {
            // Encapsular os canais de entrada e saída do socket
            ObjectOutputStream saida = new ObjectOutputStream(s1.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(s1.getInputStream());

            // Obter login e senha do cliente
            String login = (String) entrada.readObject();
            String senha = (String) entrada.readObject();

            // Verificar login e senha usando o controlador de usuários
            if (ctrlUsu.findUsuario(login, senha) == null) {
                // Se o login falhar, termina a conexão
                saida.writeObject("Usuário não encontrado. Conexão encerrada.");
                s1.close();
                return;
            }

            // Se o usuário for válido, iniciar o loop para comandos
            boolean continuar = true;
            while (continuar) {
                // Receber o comando
                String comando = (String) entrada.readObject();

                // Se o comando for "L", enviar a lista de produtos
                if (comando.equalsIgnoreCase("L")) {
                    // Recuperar lista de produtos e enviar
                    saida.writeObject(ctrl.findProdutoEntities());
                } else {
                    // Se for outro comando, encerrar (para simplificação)
                    saida.writeObject("Comando desconhecido. Encerrando.");
                    continuar = false;
                }
            }

            // Fechar conexão e streams
            entrada.close();
            saida.close();
            s1.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
