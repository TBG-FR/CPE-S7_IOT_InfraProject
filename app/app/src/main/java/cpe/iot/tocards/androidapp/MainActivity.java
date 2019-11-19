package cpe.iot.tocards.androidapp;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.support.v13.view.DragStartHelper;
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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements OnStartDragListener {

    //private final String IP = "192.168.1.129";
    //private final String IP = "192.168.200.5";
//    private final String IP = "192.168.1.132";
//    private final String IP = "10.42.0.1";
    private final String IP = "192.168.43.123";
//    private final int PORT = 10000;
    private final int PORT = 8081;
    private InetAddress address;
    private DatagramSocket UDPSocket;

    // =====
    public Button btn_network_send;
    public Button btn_network_connect;
    // =====
    public EditText et_network_ip;
    public EditText et_network_port;
    // =====
    public ItemAdapter sensorAdapter;
    public RecyclerView rv_sensors_data;
    public ItemTouchHelper itemTouchHelper;

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
        // =====
        // =====
        et_network_ip.setText(IP);
        et_network_port.setText(Integer.toString(PORT));
        // =====
        // =====
        btn_network_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                networkConnection(true);
            }
        });
        // =====
        rv_sensors_data = findViewById(R.id.rv_sensors_data);
        rv_sensors_data.setHasFixedSize(true);
//        rv_sensors_data.setLayoutFrozen(true);

        Context ctx = getApplicationContext();
        List<SensorData> list = new ArrayList<SensorData>();
        list.add(new SensorData(Icon.createWithResource(ctx, R.drawable.ic_temperature), SensorTypeEnum.TEMPERATURE));
        list.add(new SensorData(Icon.createWithResource(ctx, R.drawable.ic_luminosity), SensorTypeEnum.LUMINOSITY));
        list.add(new SensorData(Icon.createWithResource(ctx, R.drawable.ic_humidity), SensorTypeEnum.HUMIDITY));

        // RecyclerView : LayoutManager
        LinearLayoutManager mLayoutManager  = new LinearLayoutManager(this) {  @Override public boolean canScrollVertically() { return false; } };
        rv_sensors_data.setLayoutManager(mLayoutManager);

        // RecyclerView : Adapter
        sensorAdapter = new ItemAdapter(this, list, this);
        rv_sensors_data.setAdapter(sensorAdapter);
        sensorAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                tv_get_message.setText(sensorAdapter.getItemsOrder());
            }
        });

        // RecyclerView : ItemToucheHelper
        ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(sensorAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rv_sensors_data);

        et_send_message = findViewById(R.id.et_send_message);
        btn_get_message = findViewById(R.id.btn_get_message);
        tv_get_message = findViewById(R.id.tv_get_message);

        btn_network_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendMessage(et_send_message.getText().toString());
            }
        });

        btn_get_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new ReceiverTask(UDPSocket, tv_get_message).execute();
            }
        });

        // Permission : pas besoin de la demander au runtime ici (réseau only)

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    protected void networkConnection(boolean connection)
    {
        if(connection)
        {
            try
            {
                UDPSocket = new DatagramSocket(Integer.parseInt(et_network_port.getText().toString()));
                address = InetAddress.getByName(et_network_ip.getText().toString());

                Log.d("Network Connection", "Success !");
                tv_get_message.setText("Connected !");
                btn_network_connect.setEnabled(false);
            }
            catch (SocketException | UnknownHostException ex)
            {
                networkConnection(false);

                Log.e("Network Connection", "Failed !");
                ex.printStackTrace();
                tv_get_message.setText(ex.getMessage());
            }
        }
        else
        {
            UDPSocket.close();
            UDPSocket = null;
            address = null;

            tv_get_message.setText("Disconnected");
            btn_network_connect.setEnabled(true);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        networkConnection(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkConnection(false);
    }

    public void sendMessage(String message)
    {
        MessageThread mtd = new MessageThread(address, PORT, UDPSocket, message, true);
        mtd.start();
    }
}

