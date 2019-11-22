package cpe.iot.tocards.androidapp.network;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import cpe.iot.tocards.androidapp.OnTaskCompleted;

public class ReceiverTask extends AsyncTask<Void, byte[], Void> {

    private DatagramSocket Socket;
    private OnTaskCompleted<String> listener;

    ReceiverTask(DatagramSocket socket, OnTaskCompleted<String> onTaskCompleted) {
        super();
        Socket = socket;
        listener = onTaskCompleted;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while(true){
            byte[] data = new byte [1024]; // Espace de réception des données.
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try
            {
                if(Socket != null && Socket.isClosed() == false)
                    Socket.receive(packet);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            int size = packet.getLength();
            publishProgress(java.util.Arrays.copyOf(data, size));
        }
    }

    @Override
    protected void onProgressUpdate(byte[]... values) {
        super.onProgressUpdate(values);

        String message = "";
        try
        {
            message = new String(values[0], "UTF-8");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            message = "Erreur à la réception : " /*+ ex.getMessage()*/;
        }
        finally
        {
            listener.onTaskCompleted(message);
        }
    }
}