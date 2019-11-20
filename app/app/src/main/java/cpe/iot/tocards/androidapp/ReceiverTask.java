package cpe.iot.tocards.androidapp;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

public class ReceiverTask extends AsyncTask<Void, byte[], Void> {

    private DatagramSocket Socket;
    private OnTaskCompleted<JSONObject> listener;
    private TextView TextView;
    private List<SensorData> Sensors;

    ReceiverTask(DatagramSocket socket, TextView textView, List<SensorData> sensors, OnTaskCompleted<JSONObject> onTaskCompleted) {
        super();
        Socket = socket;
        TextView = textView;
        Sensors = sensors;
        listener = onTaskCompleted;
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
            try
            {
                JSONObject sensorsJSON = new JSONObject(message);
                //if(sensorsJSON.get()) // TODO : Handle error
                listener.onTaskCompleted(sensorsJSON);
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                TextView.setText(message);
            }
        }
    }
}