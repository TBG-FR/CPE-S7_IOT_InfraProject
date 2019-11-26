package cpe.iot.tocards.androidapp.network;

import android.util.Log;
import android.util.Pair;

import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

import cpe.iot.tocards.androidapp.MainActivity;
import cpe.iot.tocards.androidapp.interfaces.OnTaskCompleted;

public class NetworkManager implements OnTaskCompleted<Pair<DatagramSocket, Boolean>> {

    private MainActivity mainActivity;
    private ReceiverTask receiverTask;
    private DatagramSocket UDPSocket;
    private Timer timer;
    private boolean connected = false;
    private static boolean run = true;

    public NetworkManager(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    private boolean getConnected() { return this.connected; }

    private void setConnected(boolean value)
    {
        this.connected = value;
        this.ConnectedChanged();
    }

    public void Connect(String destAddress, String destPort)
    {
        new NetworkConnectionTask(this, this.mainActivity).execute(destAddress, destPort);
    }

    public void Disconnect()
    {
        new Thread( new Runnable()
        { @Override public void run()
            {

                if(UDPSocket != null)
                    if(UDPSocket.isConnected())
                        UDPSocket.disconnect();

                if(UDPSocket != null)
                    UDPSocket.close();

                UDPSocket = null;
                setConnected(false);
            }
        } ).start();
    }

    public void Send(String message)
    {
        new MessageThread(UDPSocket, message).start();
    }

    private void Receive()
    {
        this.receiverTask = new ReceiverTask(UDPSocket, this.mainActivity, this.mainActivity);
        this.receiverTask.execute();
    }

    @Override
    public void onTaskCompleted(Pair<DatagramSocket, Boolean> objects) {
        this.UDPSocket = objects.first;
        this.setConnected(objects.second);

        this.ConnectedChanged();
    }

    private void ConnectedChanged()
    {
        if(this.getConnected())
        {
            run = true;
            ReceiverTask.run = true;

            new Timer().scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    if(run)
                    {
                        mainActivity.getValues();
                        Log.d("Thread", "Done");
                    }
                    else
                    {
                        if(timer != null)
                        {
                            timer.cancel();
                            timer.purge();
                        }
                    }
                }
            },500,2500);

            Receive();
        }
        else
        {
            run = false; //=> timer.cancel(); && timer(false).purge();
            ReceiverTask.run = false; //getReceiverTask(false).cancel(true);
        }

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                mainActivity.displayConnected(getConnected());
            }
        });

    }

}
