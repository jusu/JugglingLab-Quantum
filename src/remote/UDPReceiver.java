package remote;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPReceiver {
	DatagramSocket socket;
	byte[] receiveData = new byte[8192];

	public UDPReceiver() {
		try {
			socket = new DatagramSocket(12001);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public String receive() {
		DatagramPacket packet = new DatagramPacket(receiveData,
				receiveData.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		if (packet.getLength() > 0) {
			byte[] ba = new byte[packet.getLength()];
			System.arraycopy(packet.getData(), 0, ba, 0, ba.length);

			return new String(ba);
		} else
			return null;
	}
}
