package cpe.iot.tocards.androidapp.network;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import cpe.iot.tocards.androidapp.interfaces.OnErrorEncountered;
import cpe.iot.tocards.androidapp.interfaces.OnTaskCompleted;

public class ReceiverTask extends AsyncTask<Void, byte[], Void> {

    private DatagramSocket Socket;
    private OnTaskCompleted<String> taskCompletedListener;
    private OnErrorEncountered errorEncounteredListener;
    public static boolean run = true;

    ReceiverTask(DatagramSocket socket, OnTaskCompleted<String> onTaskCompleted, OnErrorEncountered onErrorEncountered) {
        super();
        Socket = socket;
        taskCompletedListener = onTaskCompleted;
        errorEncounteredListener = onErrorEncountered;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while(run)
        {

            byte[] data = new byte [1024]; // Espace de réception des données.
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try
            {
                if(Socket != null && Socket.isClosed() == false)
                {
                    Socket.receive(packet);
                    int size = packet.getLength();
                    publishProgress(java.util.Arrays.copyOf(data, size));
                }
            }
            catch (IOException ex)
            {
                Log.e("ReceiverTask.doInBackground", ex.getMessage());
                //ex.printStackTrace();
                errorEncounteredListener.onErrorEncountered("Socket receive", ex);
            }
        }
        //this.cancel(true);
        return null;
    }

    @Override
    protected void onProgressUpdate(byte[]... values) {
        super.onProgressUpdate(values);

        if(isCancelled() == false)
        {
            try
            {
                String message = new String(values[0], "UTF-8");
                taskCompletedListener.onTaskCompleted(message);
                Log.d("ReceiverTask.onProgressUpdate", "Done");
            }
            catch(UnsupportedEncodingException ex)
            {
                Log.e("ReceiverTask.onProgressUpdate", ex.getMessage());
                //ex.printStackTrace();
                errorEncounteredListener.onErrorEncountered("Parse bytes", ex);
            }
        }
    }
}