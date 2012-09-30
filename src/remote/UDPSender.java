package remote;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPSender {
	DatagramSocket clientSocket;
	InetAddress IPAddress;

	public UDPSender() {
		try {
			clientSocket = new DatagramSocket();
			IPAddress = InetAddress.getByName("localhost");
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void send(byte[] ba) {
		DatagramPacket packet = new DatagramPacket(ba, ba.length, IPAddress,
				12000);
		try {
			clientSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String s) {
		send(s.getBytes());
	}

}
