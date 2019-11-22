package cpe.iot.tocards.androidapp.network;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import cpe.iot.tocards.androidapp.OnTaskCompleted;

public class NetworkConnectionTask extends AsyncTask<String, Void, Boolean> {

    private OnTaskCompleted<Pair<DatagramSocket, Boolean>> TaskCompletedHandler;
    private DatagramSocket Socket = null;

    NetworkConnectionTask(OnTaskCompleted<Pair<DatagramSocket, Boolean>> taskCompletedHandler)
    {
        TaskCompletedHandler = taskCompletedHandler;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        //Socket = null;
        InetAddress dstAddress = null;
        int dstPort = -1;
        String request = "TEST_CONNECTION";
        String response = "";

        try
        {
            if(!(strings[0] == null || strings[0].isEmpty()))
            {
                dstAddress = InetAddress.getByName(strings[0]);
            }
            if(!(strings[1] == null || strings[1].isEmpty())) {
                dstPort =  Integer.parseInt(strings[1]);
            }
            if(dstAddress != null && dstPort != -1)
            {

                // Socket for testing connection
                Socket = new DatagramSocket(dstPort);
                Socket.setSoTimeout(500);

                // Send
                DatagramPacket testConnectionPacket = new DatagramPacket(request.getBytes(), request.length(), dstAddress, dstPort);
                Socket.send(testConnectionPacket);

                // Receive
                byte[] buf = new byte[ request.length()];
                DatagramPacket responsePacket = new DatagramPacket(buf, buf.length);
                Socket.receive(responsePacket);
                response = new String(buf);
                Log.d("Response", response);

                Socket.close();
                Socket = null;

                // Socket for sending/receiving following messages

                Socket = new DatagramSocket(dstPort);
                Socket.connect(dstAddress, dstPort);
                Log.d("Socket.Connect", "ADDR=" + dstAddress + " -- PORT=" + dstPort + " -> CONN=" + Socket.isConnected());

            }
        }
        catch (IOException ex) //(SocketException | UnknownHostException ex)
        {
            Log.e("Socket.Connect", "Err " + ex.getMessage());

            if(Socket != null)
            {
                Socket.disconnect();
                Socket.close();
                Socket = null;
            }

            ex.printStackTrace();
        }

        return (response.equals(request));
    }

    @Override
    protected void onPostExecute(Boolean connected) {
        super.onPostExecute(connected);

        TaskCompletedHandler.onTaskCompleted(new Pair<>(Socket, connected));
    }
}
