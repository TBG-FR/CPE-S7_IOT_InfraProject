package cpe.iot.tocards.androidapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String Temperature = "T";
    public static final String Luminosity = "L";
    public static final String Humidity = "H";

    // Sensors
    TextView tv_sensor_one;
    TextView tv_sensor_two;
    TextView tv_sensor_three;
    TextView tv_sensor_datetime;
    Map<String, TextView> orderedSensors;
    Spinner spn_sensor_one;
    Spinner spn_sensor_two;
    Spinner spn_sensor_three;

    // Network
    private InetAddress address;
    private DatagramSocket UDPSocket;
    EditText et_network_ip;
    EditText et_network_port;
//    TextView tv_received;

    // Connection
    private boolean connected;
    Button btn_connection_action;
    TextView tv_connection_status;

    // Message
    Button btn_message_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Network
        et_network_ip = findViewById(R.id.et_ip);
        et_network_port = findViewById(R.id.et_port);
//        tv_received = findViewById(R.id.tv_received);

        // Connection
        btn_connection_action = findViewById(R.id.btn_connection_action);
        tv_connection_status = findViewById(R.id.tv_connection_status);

        // Sensors
        tv_sensor_one = findViewById(R.id.tv_sensor_one);
        tv_sensor_two = findViewById(R.id.tv_sensor_two);
        tv_sensor_three = findViewById(R.id.tv_sensor_three);
        refreshSensorsOrder();
        spn_sensor_one = findViewById(R.id.spn_sensor_one);
        spn_sensor_two = findViewById(R.id.spn_sensor_two);
        spn_sensor_three = findViewById(R.id.spn_sensor_three);

        // Message
        btn_message_send = findViewById(R.id.btn_message_send);

        btn_connection_action.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(UDPSocket != null)
                    manageNetwork(false);

                manageNetwork(true);
//                new ReceiverTask(UDPSocket, tv_received).execute();

                connected = true;
                refreshConnected();

            }
        });

        btn_message_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(connected)
                {
                    int port = Integer.parseInt(et_network_port.getText().toString()); // server port
                    new MessageThread(address, port, UDPSocket, "TLH").start();
                }
            }
        });

        // Sensors Order Selection

        ArrayList<String> sensorsList = new ArrayList<String>();
        sensorsList.add(Temperature);
        sensorsList.add(Humidity);
        sensorsList.add(Luminosity);

        ArrayList<String> sensorsOrder = new ArrayList<String>(sensorsList);
        /*
        sensorsList.add(Temperature);
        sensorsList.add(Humidity);
        sensorsList.add(Luminosity);
        */

        // Spinners
        SensorsArrayAdapter customAdapter = new SensorsArrayAdapter(this, sensorsList);
        customAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_sensor_one.setAdapter(customAdapter);
        spn_sensor_two.setAdapter(customAdapter);
        spn_sensor_three.setAdapter(customAdapter);
/*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sensorsList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spn_sensor_one.setAdapter(adapter);
        spn_sensor_two.setAdapter(adapter);
        spn_sensor_three.setAdapter(adapter);

        spn_sensor_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        manageNetwork(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manageNetwork(true);
    }

    private void refreshConnected()
    {
        // TODO Color

        String connectedMessage = getString(R.string.txt_connected_false);

        if(connected)
            connectedMessage = getString(R.string.txt_connected_true);


        tv_connection_status.setText(connectedMessage);
    }

    private void refreshSensorsOrder()
    {
        if(orderedSensors == null || orderedSensors.isEmpty())
        {
            new HashMap<String, TextView>() {{
                put(Temperature, tv_sensor_one);
                put(Luminosity, tv_sensor_two);
                put(Humidity, tv_sensor_three);
            }};
        }

        //https://stackoverflow.com/questions/2784081/android-create-spinner-programmatically-from-array

//
//        orderedSensors.get(Temperature).setText(R.string.txt_sensor_temperature);
//        orderedSensors.get(Humidity).setText(R.string.txt_sensor_humidity);
//        orderedSensors.get(Luminosity).setText(R.string.txt_sensor_luminosity);

//        ArrayList<String> strings = new ArrayList<String>();
//        strings.add(Temperature);
//        strings.add(Luminosity);
//        strings.add(Humidity);

//        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings); //selected item will look like a spinner set from XML
//        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spn_sensor_one.setAdapter(spinnerArrayAdapter);
//        spn_sensor_two.setAdapter(spinnerArrayAdapter);
//        spn_sensor_three.setAdapter(spinnerArrayAdapter);

    }

    // ========== SENSORS ==========
/*
    private void manageSensors(boolean set)
    {
        if(set)
        {
            try
            {
                sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                sm.registerListener(
                        this,
                        sm.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                        SensorManager.SENSOR_DELAY_UI
                );
            }
            catch(NullPointerException ex) { ex.printStackTrace(); }
        }
        else
        {
            try
            {
                sm.unregisterListener(this);
            }
            catch(Exception ex) { ex.printStackTrace(); }
            finally
            {
                sm = null;
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//        switch(event.sensor.getType())
//        {
//            case Sensor.TYPE_ACCELEROMETER:
//                refreshAccelerometerValues(event.values);
//                break;
//
//            case Sensor.TYPE_PROXIMITY:
//                refreshProximityValues(event.values);
//                break;
//
//            default:
//                break;
//        }

        refreshProximityValues(event.values);
        onAccuracyChanged(event.sensor, event.accuracy);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Date dt = new Date();
        tv_sensor_datetime.setText(getString(R.string.txt_sensor_datetime, dt));
    }

    private void refreshProximityValues(float values[])
    {
        tv_sensor_value.setText(getString(R.string.txt_sensor_value, values[0]));

        boolean tmp = (values[0] == 0);
        String tmpStr = tmp ? "TRUE" : "FALSE";
        tv_player_current.setText(tmpStr);

        if(playing && values[0] == 0)
        {
            String playmessage = "(_)";

            if(player == 1)
            {
                playmessage = MESSAGE_PLAY_P1;
            }
            else if(player == 2)
            {
                playmessage = MESSAGE_PLAY_P2;
            }

            int port = Integer.parseInt(et_network_port.getText().toString()); // server port
            new MessageThread(address, port, UDPSocket, playmessage).start();

        }
    }
*/
    // ========== NETWORK =========

    private void manageNetwork(boolean set)
    {
        if(set)
        {
            try
            {
//            // Init
//            String ip = "";
//            int port = -1;

                // Assign
                String ip = et_network_ip.getText().toString();
                int port = Integer.parseInt(et_network_port.getText().toString()); // our port

                // Check
                if(ip.isEmpty() || et_network_port.getText().toString().isEmpty())
                    return;

                UDPSocket = new DatagramSocket(port);
                address = InetAddress.getByName(ip);

                connected = true;
                refreshConnected();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                connected = false;
                refreshConnected();
            }
        }
        else
        {
            try
            {
                UDPSocket.close();
            }
            catch(Exception ex) { ex.printStackTrace(); }
            finally {
                UDPSocket = null;
                address = null;

                connected = false;
                refreshConnected();
            }
        }
    }
}

