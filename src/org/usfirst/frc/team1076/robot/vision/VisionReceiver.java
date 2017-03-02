package org.usfirst.frc.team1076.robot.vision;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.strongback.Strongback;

/**
 * This class reads from an IP address and port 
 * It expects to receive data packets relating to vision.
 */
public class VisionReceiver {
	DatagramSocket socket;
	VisionData data;

	
	public VisionReceiver(String ip, int port) throws SocketException {
		socket = new DatagramSocket(null);
		InetSocketAddress address = new InetSocketAddress(ip, port);
		socket.bind(address);
		data = new VisionData();
	}

	/**
	 * Receives and processes a vision packet.
	 */
	public void receive() {
	      byte[] buffer = new byte[512];
	        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
	        try {
	            socket.setSoTimeout(1);
	        } catch (SocketException e1) {
	            e1.printStackTrace();
	            return;
	        }
	        
	        while (true) {
	            try {
	                socket.receive(packet);
	            } catch (SocketTimeoutException e) {
	                // Exit once we have received all the packets.
	            	Strongback.eventRecorder().record("VisionReciever", "Out of packets!");
	                return;
	            } catch (IOException e) {
	                e.printStackTrace();
	                return;
	            }
	            
	            String json = new String(packet.getData()).substring(0, packet.getLength());
	            this.data.update(json);
	        }
	}
	
	/**
	 * Return most recently received packet 
	 */
	public VisionData getData() {
		return data;
	}
}
