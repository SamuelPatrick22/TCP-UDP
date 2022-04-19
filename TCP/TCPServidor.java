import java.net.*;
import java.io.*;
public class TCPServidor {
    public static void main(String args[]) {
        try {
            int porta = 6789;
            if (args.length > 0) porta = Integer.parseInt(args[0]);
            
			ServerSocket escuta = new ServerSocket(porta);
            System.out.println("*** Servidor ***");
            System.out.println("*** Inicio - porta de escuta (listening): " + porta);
            while (true) {
                Socket cliente = escuta.accept();
                System.out.println("*** Socket de escuta (listen): " + escuta.getLocalSocketAddress().toString());
                System.out.println("*** Conexao aceita de (remoto): " + cliente.getRemoteSocketAddress().toString());
                new Conexao(cliente);
            }
        } catch (IOException e) {
            System.out.println("Erro na escuta: " + e.getMessage());
        }
    }
    
}

class Conexao extends Thread {
    protected BufferedReader arq = null;
    DataInputStream ent;
    DataOutputStream sai;
    Socket cliente;
    String idCliente = null;
    
    public Conexao(Socket s) {
        try {
            cliente = s;
            ent = new DataInputStream(cliente.getInputStream());
            sai = new DataOutputStream(cliente.getOutputStream());
            idCliente = ent.readUTF();
            this.start();
        } catch (IOException e) {
            System.out.println("Erro IO Conexao: " + e.getMessage());
        }
    }
    
    public void enviarMsg(String msg) {
        try {
            sai.writeUTF(msg);
        } catch (IOException e1) {
            System.out.println("Erro de escrita no buffer da conexao ("+idCliente+")");
        }
    }
    
    
    public void run() {
        try {
            arq = new BufferedReader(new FileReader(ent.readUTF())); //Repasse de informação do cliente para o construtor FileReader
        } catch (FileNotFoundException e) {
            System.err.println("Arquivo nao econtrado: \""+e.getMessage()+"\"");
            enviarMsg("!!! Erro ao tentar abrir arquivo \""+e.getMessage()+"\"");
        } catch (IOException e){                                     //Tratamento de exceção lançada pela função readUTF
            System.err.println("Arquivo nao econtrado: \""+e.getMessage()+"\"");
            enviarMsg("!!! Erro ao tentar abrir arquivo \""+e.getMessage()+"\"");
        }
        
        if (arq != null) {
            try {
                String l = arq.readLine();
                while (l != null) {
                    enviarMsg(l);
                    l = arq.readLine();
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler linha do arquivo \""+ e.getMessage() +"\" ("+idCliente+")");
                enviarMsg("!!! Erro ao ler arquivo " + e.getMessage());
            }
            try {
                arq.close();
            } catch (IOException e) {
                System.out.println("Erro fechamento do arquivo \""+ e.getMessage() +"\" ("+idCliente+")");
            }
            try {
                cliente.close();
            } catch (IOException e) {
                System.out.println("Erro fechamento do socket cliente ("+idCliente+")");
            }
            System.out.println("*** Conexao encerrada com "+idCliente + "\n");
        }
    }
}