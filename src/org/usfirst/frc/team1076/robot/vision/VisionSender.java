package org.usfirst.frc.team1076.robot.vision;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.json.JSONObject;

public class VisionSender {
    DatagramSocket socket;
    public VisionSender(String ip, int port) throws SocketException {
        socket = new DatagramSocket(null);
        InetSocketAddress address = new InetSocketAddress(ip, port);
        socket.bind(address);
    }
    
    public void setVisionLogging(boolean doLogging, int framesPerImage) {
        byte[] buffer = new byte[512];
        JSONObject data = new JSONObject();
        data.put("sender", "robot");
        data.put("message", "logging");
        if (doLogging) {
            data.put("status", "enable");
        } else {
            data.put("status", "disable");
        }
        data.put("period", framesPerImage);
        buffer = data.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
