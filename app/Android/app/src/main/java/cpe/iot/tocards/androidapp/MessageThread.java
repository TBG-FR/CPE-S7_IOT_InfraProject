package cpe.iot.tocards.androidapp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MessageThread extends Thread {

    InetAddress Address;
    int Port;
    DatagramSocket Socket;
    String Message;

    MessageThread(InetAddress address, int port, DatagramSocket socket, String message) {
        super();
        Address = address;
        Port = port;
        Socket = socket;
        Message = message;
    }

    @Override
    public void run() {
        //super.run();
        try
        {
            byte[] data = Message.getBytes("UTF-8");

            DatagramPacket packet = new DatagramPacket(data, data.length, Address, Port);
            Socket.send(packet);
        }
        catch(Exception ex) // getBytes or send
        {
            ex.printStackTrace();
        }
    }
}