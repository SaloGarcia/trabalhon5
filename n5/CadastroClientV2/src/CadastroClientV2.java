import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class CadastroClientV2 {
    public static void main(String[] args) {
        try {
            try ( // Conectar ao servidor
                    Socket socket = new Socket("localhost", 4321)) {
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                
                // Criar a janela de saída
                SaidaFrame saidaFrame = new SaidaFrame(null);
                JTextArea textArea = saidaFrame.getTexto();
                saidaFrame.setVisible(true);
                
                // Criar e iniciar a Thread de leitura assíncrona
                ThreadClient threadClient = new ThreadClient(input, textArea);
                threadClient.start();
                
                // Enviar login e senha
                System.out.print("Digite o login: ");
                String login = reader.readLine();
                System.out.print("Digite a senha: ");
                String senha = reader.readLine();
                output.writeObject(login);
                output.writeObject(senha);
                output.flush();
                
                // Exemplo de menu
                String comando;
                do {
                    System.out.println("Escolha uma opção: L – Listar, X – Finalizar, E – Entrada, S – Saída");
                    comando = reader.readLine();
                    output.writeObject(comando);
                    output.flush();
                    
                    if (comando.equals("L")) {
                        // Listar produtos
                        saidaFrame.adicionarTexto("Listar produtos solicitado");
                    } else if (comando.equals("E") || comando.equals("S")) {
                        // Entrada ou saída de produtos
                        System.out.print("Digite o Id da pessoa: ");
                        int idPessoa = Integer.parseInt(reader.readLine());
                        output.writeObject(idPessoa);
                        
                        System.out.print("Digite o Id do produto: ");
                        int idProduto = Integer.parseInt(reader.readLine());
                        output.writeObject(idProduto);
                        
                        System.out.print("Digite a quantidade: ");
                        int quantidade = Integer.parseInt(reader.readLine());
                        output.writeObject(quantidade);
                        
                        System.out.print("Digite o valor unitário: ");
                        double valorUnitario = Double.parseDouble(reader.readLine());
                        output.writeObject(valorUnitario);
                        
                        output.flush();
                        
                        saidaFrame.adicionarTexto("Movimento realizado: " + comando);
                    }
                } while (!comando.equals("X"));
            }
        } catch (IOException | NumberFormatException e) {
        }
    }
}
