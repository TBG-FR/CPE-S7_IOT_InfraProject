package cpe.iot.tocards.androidapp.network;

import android.util.Log;
import android.util.Pair;

import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

import cpe.iot.tocards.androidapp.MainActivity;
import cpe.iot.tocards.androidapp.OnTaskCompleted;

public class NetworkManager implements OnTaskCompleted<Pair<DatagramSocket, Boolean>> {

    private cpe.iot.tocards.androidapp.MainActivity MainActivity;
    private DatagramSocket UDPSocket;
    private boolean connected = false;
    private Timer timer;

    public NetworkManager(MainActivity mainActivity)
    {
        MainActivity = mainActivity;
    }

    public Timer getTimer()
    {
        if(timer == null)
        {
            timer = new Timer();
        }

        return timer;
    }

    public boolean getConnected() { return this.connected; }

    public void setConnected(boolean value)
    {
        this.connected = value;
        this.ConnectedChanged();
    }

    public void Connect(String destAddress, String destPort)
    {
        new NetworkConnectionTask(this).execute(destAddress, destPort);
    }

    public void Disconnect()
    {
        //new Thread().start(); //todo
        if(UDPSocket != null)
        {
            if(UDPSocket.isConnected())
                UDPSocket.disconnect();
            UDPSocket.close();
            UDPSocket = null;
        }
        setConnected(false);
    }

    public void Send(String message)
    {
        new MessageThread(UDPSocket, message).start();
    }

    public void Receive(MainActivity mainActivity)
    {
        new ReceiverTask(UDPSocket, mainActivity).execute();
    }

    @Override
    public void onTaskCompleted(Pair<DatagramSocket, Boolean> objects) {
        this.UDPSocket = objects.first;
        this.setConnected(objects.second);

        this.ConnectedChanged();
        MainActivity.displayConnected(this.getConnected());
    }

    private void ConnectedChanged()
    {
        if(this.getConnected())
        {

            getTimer().scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run() {
                    //if(getConnected()) //todo see if required
                    //{
                        MainActivity.getValues();
                        Log.d("Thread", "Done");
                    //}
                }
            },500,2500);

            new ReceiverTask(UDPSocket, MainActivity).execute();

        }
        else
        {
            getTimer().cancel();
            getTimer().purge();
        }
    }

}
