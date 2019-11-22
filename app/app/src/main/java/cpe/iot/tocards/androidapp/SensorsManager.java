package cpe.iot.tocards.androidapp;

import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SensorsManager {

    private MainActivity mainActivity;
    private ArrayList<SensorData> sensorsList;
    private ItemAdapter sensorAdapter;
    private ItemTouchHelper itemTouchHelper;

    public SensorsManager(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    private ArrayList<SensorData> getSensorsList()
    {
        if(sensorsList == null || sensorsList.isEmpty())
        {
            sensorsList = new ArrayList<>();
            sensorsList.add(new SensorData(Icon.createWithResource(BuildConfig.APPLICATION_ID, R.drawable.ic_temperature), SensorTypeEnum.TEMPERATURE));
            sensorsList.add(new SensorData(Icon.createWithResource(BuildConfig.APPLICATION_ID, R.drawable.ic_luminosity), SensorTypeEnum.LUMINOSITY));
            sensorsList.add(new SensorData(Icon.createWithResource(BuildConfig.APPLICATION_ID, R.drawable.ic_humidity), SensorTypeEnum.HUMIDITY));
        }

        return sensorsList;
    }

    public ItemAdapter getAdapter(final MainActivity mainActivity)
    {
        if(sensorAdapter == null)
        {
            sensorAdapter = new ItemAdapter(mainActivity, getSensorsList(), mainActivity);
            sensorAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount);

                    mainActivity.NetworkManager.Send(sensorAdapter.getItemsOrder());
                    Log.d("NetworkManager.Send", sensorAdapter.getItemsOrder());
                }
            });
        }

        return sensorAdapter;
    }

    public ItemTouchHelper getItemTouchHelper()
    {
        if(itemTouchHelper == null)
        {
            ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(sensorAdapter);
            itemTouchHelper = new ItemTouchHelper(callback);
        }

        return itemTouchHelper;
    }

    public void attachItemTouchHelper(RecyclerView recyclerView)
    {
        getItemTouchHelper().attachToRecyclerView(recyclerView);
    }

    public void startDragItemTouchHelper(RecyclerView.ViewHolder viewHolder)
    {
        getItemTouchHelper().startDrag(viewHolder);
    }

    public String updateValues(String sensorsString)
    {
        //String returnString = "";

        try
        {
            JSONObject sensorsJSON = new JSONObject(sensorsString);
            //if(sensorsJSON.get()) // TODO : Handle error

            for(SensorData sensor : getSensorsList())
            {
                sensor.setValue(Double.parseDouble(sensorsJSON.getString(sensor.getType().toString())));
            }

            sensorAdapter.updateList(getSensorsList());
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
            sensorsString = ex.getMessage();
        }
        finally
        {
            //returnString = sensorsString;
        }

        return sensorsString;
    }

}
