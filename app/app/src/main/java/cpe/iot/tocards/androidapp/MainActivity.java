package cpe.iot.tocards.androidapp;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cpe.iot.tocards.androidapp.network.NetworkManager;

public class MainActivity extends AppCompatActivity implements OnStartDragListener, OnTaskCompleted<String> {

    //private DatagramSocket UDPSocket;
    // ==========================================================================================

    //private final String IP = "192.168.1.129";
    //private final String IP = "192.168.200.5";
//    private final String IP = "192.168.1.132";
//    private final String IP = "10.42.0.1";
    private final String IP = "192.168.43.186";
    private final int PORT = 10000;
//    private final int PORT = 8081;
    private InetAddress address;

    NetworkManager NetworkManager = new NetworkManager(MainActivity.this);//this);
    SensorsManager SensorsManager = new SensorsManager(MainActivity.this);

    // =====
    public Button btn_network_send;
    public Button btn_network_connect;
    // =====
    public EditText et_network_ip;
    public EditText et_network_port;
    // =====
//    public ItemAdapter sensorAdapter;
//    public List<SensorData> sensorList;
    public RecyclerView rv_sensors_data;
//    public ItemTouchHelper itemTouchHelper;

    public EditText et_send_message;
    public TextView tv_get_message;
    public Button btn_get_message;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // =====
        btn_network_connect = findViewById(R.id.btn_network_connect);
        btn_network_send = findViewById(R.id.btn_network_send);
        // =====
        et_network_ip = findViewById(R.id.et_network_ip);
        et_network_port = findViewById(R.id.et_network_port);
        et_network_ip.setText(IP);
        et_network_port.setText(Integer.toString(PORT));
        // =====
        // =====
        btn_network_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                NetworkManager.Connect(et_network_ip.getText().toString(), et_network_port.getText().toString());
            }
        });
        // =====
        rv_sensors_data = findViewById(R.id.rv_sensors_data);
        rv_sensors_data.setHasFixedSize(true);
//        rv_sensors_data.setLayoutFrozen(true);

        // RecyclerView : LayoutManager
        LinearLayoutManager mLayoutManager  = new LinearLayoutManager(this) {  @Override public boolean canScrollVertically() { return false; } };
        rv_sensors_data.setLayoutManager(mLayoutManager);

        // RecyclerView : Adapter
        rv_sensors_data.setAdapter(SensorsManager.getAdapter(this));
        SensorsManager.attachItemTouchHelper(rv_sensors_data);

        et_send_message = findViewById(R.id.et_send_message);
        btn_get_message = findViewById(R.id.btn_get_message);
        tv_get_message = findViewById(R.id.tv_get_message);

        btn_network_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                NetworkManager.Send(et_send_message.getText().toString());
            }
        });

//        //todo
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if(NetworkManager.Connected)
////                if(UDPSocket != null && UDPSocket.isConnected())
////                {
//                    getValues();
//                    Log.d("Thread", "Done");
////                }
//            }
//        },500,2500);
//
//        //new ReceiverTask(UDPSocket, MainActivity.this).execute();

        btn_get_message.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                NetworkManager.Receive(MainActivity.this);
                //new ReceiverTask(UDPSocket, MainActivity.this).execute();
            }
        });

        // Permission : pas besoin de la demander au runtime ici (réseau only)

    }

    public void getValues()
    {
        NetworkManager.Send("getValues()");
        Log.d("SendMessage", "getValues()");
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        SensorsManager.startDragItemTouchHelper(viewHolder);
    }

//    protected void networkConnection(boolean connection)
//    {
//        if(connection)
//        {
//            new NetworkTask(UDPSocket).doInBackground(et_network_ip.getText().toString(), et_network_port.getText().toString());
//        }
//        else
//        {
//
//        }
//
//
//        if(connection)
//        {
//            try
//            {
//                UDPSocket = new DatagramSocket(Integer.parseInt(et_network_port.getText().toString()));
//                address = InetAddress.getByName(et_network_ip.getText().toString());
//                //UDPSocket.connect(address, 10000);
//
//                if(UDPSocket != null /*&& UDPSocket.isConnected()*/)
//                {
//                    Log.d("Network Connection", "Success !");
//                    tv_get_message.setText("Connected !");
//                    btn_network_connect.setEnabled(false);
//                }
//                else { throw new SocketException("Not connected !"); }
//
//
//
//            }
//            catch (SocketException | UnknownHostException ex)
//            {
//                networkConnection(false);
//
//                Log.e("Network Connection", "Failed !");
//                ex.printStackTrace();
//                tv_get_message.setText(ex.getMessage());
//            }
//        }
//        else
//        {
//            if(UDPSocket != null)
//            {
//                UDPSocket.close();
//                UDPSocket = null;
//            }
//            address = null;
//
//            tv_get_message.setText("Disconnected");
//            btn_network_connect.setEnabled(true);
//        }
//    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //networkConnection(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        NetworkManager.Disconnect();

//        if(UDPSocket != null)
//        {
//            //UDPSocket.disconnect();
//            UDPSocket.close();
//            UDPSocket = null;
//        }
//        displayConnected();
    }

//    public void sendMessage(String message)
//    {
//        new MessageThread(address, PORT, UDPSocket, message).start();
//    }

    @Override
    public void onTaskCompleted(String sensorsString) {

        tv_get_message.setText(SensorsManager.updateValues(sensorsString));

//        try
//        {
//            JSONObject sensorsJSON = new JSONObject(sensorsString);
//            //if(sensorsJSON.get()) // TODO : Handle error
//
//            for(SensorData sensor : sensorList)
//            {
//                sensor.setValue(Double.parseDouble(sensorsJSON.getString(sensor.getType().toString())));
//            }
//
//            sensorAdapter.updateList(sensorList);
//        }
//        catch (JSONException ex)
//        {
//            ex.printStackTrace();
//            sensorsString = ex.getMessage();
//        }
//        finally
//        {
//            tv_get_message.setText(sensorsString);
//        }

    }

    // ==========================================================================================

//    public void setUDPSocket(DatagramSocket UDPSocket)
//    {
//        if(UDPSocket != null)
//            this.UDPSocket = UDPSocket;
//    }
//
//    public void doConnect()
//    {
//        new NetworkTask(this).execute(et_network_ip.getText().toString(), et_network_port.getText().toString());
//    }
//
//    public boolean getConnected()
//    {
//        return UDPSocket != null && UDPSocket.isConnected();
//    }
//
    public void displayConnected(boolean connected)
    {
        if(connected)
        {
            Log.d("Network Connection", "Success !");
            tv_get_message.setText("Connected !");
            btn_network_connect.setEnabled(false);
        }
        else
        {
            Log.d("Network Connection", "Failed !");
            tv_get_message.setText("Disconnected");
            btn_network_connect.setEnabled(true);
        }
    }
//
//    public void sendMessage(String message)
//    {
//        new MessageThread(UDPSocket, message).start();
//    }

}

