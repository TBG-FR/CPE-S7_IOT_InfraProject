package cpe.iot.tocards.androidapp.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MessageThread extends Thread {

    private DatagramSocket Socket;
    private String Message;
//    private InetAddress Address;
//    private int Port;
//    private DatagramSocket Socket;
//    private String Message;
//
//    MessageThread(InetAddress address, int port, DatagramSocket socket, String message) {
//        super();
//        Address = address;
//        Port = port;
//        Socket = socket;
//        Message = message;
//    }

    public MessageThread(DatagramSocket socket, String message) {
        super();
        Socket = socket;
        Message = message;
    }

    @Override
    public void run() {
        //super.run();
        try
        {
            if(Socket != null)
            {
                byte[] data = Message.getBytes("UTF-8");

                //DatagramPacket packet = new DatagramPacket(data, data.length, Address, Port);
                //DatagramPacket packet = new DatagramPacket(data, data.length, Socket.getRemoteSocketAddress());
                Socket.send(new DatagramPacket(data, data.length, Socket.getRemoteSocketAddress()));
            }
        }
        catch(IOException ex) // getBytes or send
        {
            ex.printStackTrace();
        }
    }
}