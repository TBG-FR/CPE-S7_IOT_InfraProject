package cpe.iot.tocards.androidapp.sensors;

import android.graphics.drawable.Icon;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import cpe.iot.tocards.androidapp.BuildConfig;
import cpe.iot.tocards.androidapp.dndrv_helpers.EditItemTouchHelperCallback;
import cpe.iot.tocards.androidapp.dndrv_helpers.ItemAdapter;
import cpe.iot.tocards.androidapp.MainActivity;
import cpe.iot.tocards.androidapp.R;

public class SensorsManager
{

    private ArrayList<SensorData> sensorsList;
    private ItemAdapter sensorAdapter;
    private ItemTouchHelper itemTouchHelper;

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

    private ItemTouchHelper getItemTouchHelper()
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

    public void updateValues(String sensorsString) throws JSONException
    {
        JSONObject sensorsJSON = new JSONObject(sensorsString);

        for(SensorData sensor : getSensorsList())
        {
            sensor.setValue(Double.parseDouble(sensorsJSON.getString(sensor.getType().toString())));
        }

        sensorAdapter.updateList(getSensorsList());
    }

}
