package cpe.iot.tocards.androidapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import cpe.iot.tocards.androidapp.dndrv_helpers.OnStartDragListener;
import cpe.iot.tocards.androidapp.interfaces.*;
import cpe.iot.tocards.androidapp.network.NetworkManager;
import cpe.iot.tocards.androidapp.sensors.SensorsManager;

public class MainActivity extends AppCompatActivity implements OnStartDragListener, OnTaskCompleted<String>, OnErrorEncountered {

    public EditText et_network_ip;
    public EditText et_network_port;

    public Button btn_network_connect;
    public Button btn_network_disconnect;
    public ImageView iv_network_status;

    public RecyclerView rv_sensors_data;
    public TextView tv_get_message;

    public cpe.iot.tocards.androidapp.network.NetworkManager NetworkManager = new NetworkManager(MainActivity.this);
    public cpe.iot.tocards.androidapp.sensors.SensorsManager SensorsManager = new SensorsManager();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String ip = "192.168.43.186";
        int port = 10000;
        et_network_ip = findViewById(R.id.et_network_ip);
        et_network_port = findViewById(R.id.et_network_port);
        et_network_ip.setText(ip);
        et_network_port.setText(Integer.toString(port));

        iv_network_status = findViewById(R.id.iv_network_status);
        btn_network_connect = findViewById(R.id.btn_network_connect);
        btn_network_disconnect = findViewById(R.id.btn_network_disconnect);

        btn_network_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                NetworkManager.Connect(et_network_ip.getText().toString(), et_network_port.getText().toString());
            }
        });

        btn_network_disconnect.setEnabled(false);
        btn_network_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                NetworkManager.Disconnect();
            }
        });

        rv_sensors_data = findViewById(R.id.rv_sensors_data);
        rv_sensors_data.setHasFixedSize(true);
        //rv_sensors_data.setLayoutFrozen(true);

        // RecyclerView : LayoutManager
        LinearLayoutManager mLayoutManager  = new LinearLayoutManager(this) {  @Override public boolean canScrollVertically() { return false; } };
        rv_sensors_data.setLayoutManager(mLayoutManager);

        // RecyclerView : Adapter
        rv_sensors_data.setAdapter(SensorsManager.getAdapter(this));
        SensorsManager.attachItemTouchHelper(rv_sensors_data);

        tv_get_message = findViewById(R.id.tv_get_message);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        NetworkManager.Disconnect();
    }

    public void getValues()
    {
        NetworkManager.Send("getValues()");
        Log.d("SendMessage", "getValues()");
    }

    @Override
    public void onTaskCompleted(String sensorsString) {

        try
        {
            SensorsManager.updateValues(sensorsString);
            tv_get_message.setText("Values received without error(s)." + " [" + getCurrentTime() + "]");
        }
        catch (JSONException ex)
        {
            //ex.printStackTrace();
            onErrorEncountered("Parse JSON", ex);
        }

    }

    private String getCurrentTime()
    {
        return java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss", Locale.FRANCE));
    }

    @Override
    public void onErrorEncountered(String sender, Exception ex)
    {
        tv_get_message.setText("Error (" + sender + ") : " + ex.getMessage() + " [" + getCurrentTime() + "]");
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        SensorsManager.startDragItemTouchHelper(viewHolder);
    }

    public void displayConnected(boolean connected)
    {
        if(connected)
        {
            Log.d("Network Connection", "Success !");
            iv_network_status.setColorFilter(Color.GREEN);
            //tv_get_message.setText("Connected !");
            btn_network_connect.setEnabled(false);
            btn_network_disconnect.setEnabled(true);
        }
        else
        {
            Log.d("Network Connection", "Failed !");
            iv_network_status.setColorFilter(Color.RED);
            //tv_get_message.setText("Disconnected");
            btn_network_connect.setEnabled(true);
            btn_network_disconnect.setEnabled(false);
        }
    }
}

