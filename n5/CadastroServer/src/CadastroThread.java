import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.math.BigDecimal;
import java.util.Date;
import model.Movimento;
import model.Pessoa;
import model.Produto;
import model.Usuario;

public class CadastroThread implements Runnable {

    private ProdutoJpaController ctrlProd;
    private UsuarioJpaController ctrlUsu;
    private MovimentoJpaController ctrlMov;
    private PessoaJpaController ctrlPessoa;
    private Socket s1;

    public CadastroThread(ProdutoJpaController ctrlProd, UsuarioJpaController ctrlUsu, MovimentoJpaController ctrlMov, PessoaJpaController ctrlPessoa, Socket s1) {
        this.ctrlProd = ctrlProd;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
        this.s1 = s1;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream saida = new ObjectOutputStream(s1.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(s1.getInputStream());

            // Obter login e senha do cliente
            String login = (String) entrada.readObject();
            String senha = (String) entrada.readObject();

            // Verificar login e senha
            Usuario usuario = ctrlUsu.findUsuario(login, senha);
            if (usuario == null) {
                saida.writeObject("Usuário não encontrado. Conexão encerrada.");
                s1.close();
                return;
            }

            // Processar comandos
            boolean continuar = true;
            while (continuar) {
                String comando = (String) entrada.readObject();

                if (comando.equalsIgnoreCase("L")) {
                    saida.writeObject(ctrlProd.findProdutoEntities());
                } else if (comando.equalsIgnoreCase("E") || comando.equalsIgnoreCase("S")) {
                    // Receber detalhes do movimento
                    Integer idPessoa = (Integer) entrada.readObject();
                    Integer idProduto = (Integer) entrada.readObject();
                    Integer quantidade = (Integer) entrada.readObject();
                    BigDecimal valorUnitario = (BigDecimal) entrada.readObject();

                    // Encontrar pessoa e produto
                    Pessoa pessoa = ctrlPessoa.findPessoa(idPessoa);
                    Produto produto = ctrlProd.findProduto(idProduto);

                    if (pessoa != null && produto != null) {
                        Movimento movimento = new Movimento();
                        movimento.setTipoMovimento(comando.charAt(0));  // 'E' ou 'S'
                        movimento.setIdPessoa(pessoa);
                        movimento.setIdProduto(produto);
                        movimento.setQuantidade(quantidade);
                        movimento.setValorUnitario(valorUnitario);
                        movimento.setDataMovimento(new Date());
                        movimento.setIdUsuario(usuario);

                        // Persistir movimento
                        ctrlMov.create(movimento);

                        // Atualizar produto
                        if (comando.equalsIgnoreCase("E")) {
                            produto.setQuantidade(produto.getQuantidade() + quantidade);
                        } else if (comando.equalsIgnoreCase("S")) {
                            produto.setQuantidade(produto.getQuantidade() - quantidade);
                        }
                        ctrlProd.edit(produto);

                        saida.writeObject("Movimento registrado com sucesso.");
                    } else {
                        saida.writeObject("Pessoa ou produto não encontrados.");
                    }
                } else {
                    saida.writeObject("Comando desconhecido. Encerrando.");
                    continuar = false;
                }
            }

            entrada.close();
            saida.close();
            s1.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
