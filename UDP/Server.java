import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Server {

	public static String[] questionario() {
		String[] q = new String[5];
		q[0] = "1;5;FVFFV";
		q[1] = "2;5;VFVVF";
		q[2] = "3;5;VVFVV";
		q[3] = "5;5;VVFVV";
		q[4] = "4;5;FFFFV";
		return q;
	}

	public static void main(String args[]) {
		String[] questionario = questionario();
		ArrayList<Cliente> clientes = new ArrayList<>();
		DatagramSocket s = null;
		try {
			s = new DatagramSocket(8000);
			byte[] buffer = new byte[1024];
			System.out.println("Servidor iniciado na porta 8000");
			while (true) {
				DatagramPacket req = new DatagramPacket(buffer, buffer.length);
				// espera uma requisição
				s.receive(req);
				System.out.println("Cliente na porta " + req.getPort() + " se conectou");
				// verifica se o cliente já se conectou
				Cliente c = pesquisaCliente(req.getPort(), clientes);
				if (c != null) {
					// adiciona a resposta ao objeto do cliente
					c.adicionarResposta(new String(req.getData()).trim());
					// verifica se ja pode mandar o gabarito
					if (c.isCompleted()) {
						// prepara o gabarito
						String[] gabarito = c.genAnswerKey(questionario);
						DatagramPacket resp;
						// envia o gabarito
						for (int i = 0; i < 5; i++) {
							resp = new DatagramPacket(gabarito[i].getBytes(), gabarito[i].length(),
									InetAddress.getLocalHost(),
									c.getPorta());
							s.send(resp);
						}
					}
				} else {
					// cria um novo objeto pro cliente e adiciona no array
					clientes.add(new Cliente(req.getPort(), new String(req.getData())));
				}
			}
		} catch (SocketException e) {
			System.out.println("Erro de socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Erro envio/recepcao pacote: " + e.getMessage());
		} finally {
			if (s != null)
				s.close();
		}
	}

	public static Cliente pesquisaCliente(int porta, ArrayList<Cliente> array) {
		for (Cliente c : array) {
			if (c.getPorta() == porta) {
				return c;
			}
		}
		return null;
	}
}

class Cliente {
	private int porta;
	private String[] respostas = new String[5];
	private int control = 0;

	public int getControl() {
		return this.control;
	}

	public void setControl(int control) {
		this.control = control;
	}

	public int getPorta() {
		return this.porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

	public String[] getRespostas() {
		return this.respostas;
	}

	public void setRespostas(String[] respostas) {
		this.respostas = respostas;
	}

	Cliente(int porta, String resposta) {
		this.porta = porta;
		this.respostas[0] = resposta;
		this.control++;
	}

	public void adicionarResposta(String respostas) {
		this.getRespostas()[this.control] = respostas;
		control++;
	}

	public boolean isCompleted() {
		if (this.control >= 5)
			return true;
		else
			return false;
	}

	public String[] genAnswerKey(String[] gabarito) {
		String[] ggabarito = new String[5];
		for (int i = 0; i < 5; i++) {
			String[] a = this.respostas[i].split(";")[2].split("");
			String[] b = gabarito[i].split(";")[2].split("");
			int acertos = 0;

			for (int j = 0; j < 5; j++) {
				if (b[j].equals(a[j])) {
					acertos++;
				}
			}
			ggabarito[i] = i + 1 + ";" + acertos + ";" + String.valueOf(b.length - acertos);
		}
		return ggabarito;
	}
}