package cpe.iot.tocards.androidapp;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceiverTask extends AsyncTask<Void, byte[], Void> {

    DatagramSocket Socket;
    android.widget.TextView TextView;

    ReceiverTask(DatagramSocket socket, TextView textView) {
        super();
        Socket = socket;
        TextView = textView;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while(true){
            byte[] data = new byte [1024]; // Espace de réception des données.
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try
            {
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
            message = "Erreur à la réception";
        }
        finally
        {
            TextView.setText(message);
        }
    }
}