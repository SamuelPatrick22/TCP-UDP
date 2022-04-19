import java.net.*;
import java.util.Scanner;
import java.io.*;
public class TCPCliente {
    public static void main(String args[]) {
        Socket s = null;
        try {
            s = new Socket("localhost", 6789);
            DataInputStream  ent = new DataInputStream(s.getInputStream());
            DataOutputStream sai = new DataOutputStream(s.getOutputStream());
            sai.writeUTF("TESTE");
            System.out.print("Digite o nome do arquivo: ");
            Scanner in = new Scanner(System.in);
            sai.writeUTF(in.nextLine());
            in.close();
            String recebido = ent.readUTF();
            while (recebido != null) {
                System.out.println(recebido);
                recebido = ent.readUTF();
            }
        } catch (UnknownHostException e) {
            System.out.println("Servidor desconhecido: " + e.getMessage());
        } catch (EOFException e) {
            System.out.println("--- FIM DA TRANSFERENCIA ---");
        } catch (IOException e) {
            System.out.println("E/S: " + e.getMessage());
        } finally {
            if (s!=null)
                try {
                    s.close();
                } catch (IOException e){
                    System.out.println("Encerramento do socket falhou: " + e.getMessage());
                }
        }
    }
    
}
