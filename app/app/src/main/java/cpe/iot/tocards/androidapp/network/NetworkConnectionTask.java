package cpe.iot.tocards.androidapp.network;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import cpe.iot.tocards.androidapp.interfaces.OnErrorEncountered;
import cpe.iot.tocards.androidapp.interfaces.OnTaskCompleted;

public class NetworkConnectionTask extends AsyncTask<String, Void, Boolean> {

    private static final String CONNECTION_TEST_MESSAGE = "TEST_CONNECTION";

    private OnTaskCompleted<Pair<DatagramSocket, Boolean>> taskCompletedListener;
    private OnErrorEncountered errorEncounteredListener;
    private DatagramSocket Socket = null;

    NetworkConnectionTask(OnTaskCompleted<Pair<DatagramSocket, Boolean>> onTaskCompleted, OnErrorEncountered onErrorEncountered)
    {
        taskCompletedListener = onTaskCompleted;
        errorEncounteredListener = onErrorEncountered;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        //Socket = null;
        InetAddress dstAddress = null;
        int dstPort = -1;
        String response = "";
        boolean success = false;

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
                Socket.setSoTimeout(5000);

                // Send
                DatagramPacket testConnectionPacket = new DatagramPacket(CONNECTION_TEST_MESSAGE.getBytes(), CONNECTION_TEST_MESSAGE.length(), dstAddress, dstPort);
                Socket.send(testConnectionPacket);

                // Receive
                byte[] buf = new byte[ CONNECTION_TEST_MESSAGE.length()];
                DatagramPacket responsePacket = new DatagramPacket(buf, buf.length);
                Socket.receive(responsePacket);
                response = new String(buf);

                Socket.close();
                Socket = null;

                if(response.equals(CONNECTION_TEST_MESSAGE))
                {
                    // Socket for sending/receiving following messages

                    Socket = new DatagramSocket(dstPort);
                    Socket.connect(dstAddress, dstPort);
                    success = true;
                    Log.d("Socket.Connect", "Success");
                }
                else
                {
                    throw new UnknownHostException("Host unreachable");
                }

            }
        }
        catch (IOException ex) //(SocketException | UnknownHostException ex)
        {
            Log.e("Socket.Connect", ex.getMessage());
            //ex.printStackTrace();
            errorEncounteredListener.onErrorEncountered("Socket Connect", ex);

            if(Socket != null)
            {
                Socket.disconnect();
                Socket.close();
                Socket = null;
            }
        }

        return (success);
    }

    @Override
    protected void onPostExecute(Boolean connected) {
        super.onPostExecute(connected);

        taskCompletedListener.onTaskCompleted(new Pair<>(Socket, connected));
    }
}
