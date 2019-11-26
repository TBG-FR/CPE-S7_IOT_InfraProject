package cpe.iot.tocards.androidapp.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MessageThread extends Thread {

    private DatagramSocket Socket;
    private String Message;

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
                Socket.send(new DatagramPacket(data, data.length, Socket.getRemoteSocketAddress()));
            }
        }
        catch(IOException ex) // getBytes or send
        {
            ex.printStackTrace();
        }
    }
}