import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	public static void main(String[] args) {
		try {
			DatagramSocket cliente = new DatagramSocket();
			
			InetAddress serverAdress = InetAddress.getByName("localhost");
			int porta = 8000;
			String[] respostas = {"1;5;FFFFF", "2;5;FFFFF", "3;5;FFFFF", "4;5;FFFFF", "5;5;FFFFF"};

			for(int i = 0; i < 5; i++) {
				//System.out.print("Insira a resposta "+String.valueOf(i+1)+": ");
				//Scanner in = new Scanner(System.in);
				//String resposta = in.nextLine();
				DatagramPacket req = new DatagramPacket(respostas[i].getBytes(), respostas[i].length(), serverAdress, porta);
				cliente.send(req);
			}
			
			System.out.println("Gabarito: ");
			for (int i = 0; i < 5; i++) {
				byte[] buffer = new byte[1024];
				DatagramPacket resp = new DatagramPacket(buffer, buffer.length);
				cliente.receive(resp);
				System.out.println(new String(buffer));
			}
			
			cliente.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}