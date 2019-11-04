package cpe.iot.tocards.androidapp;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import static cpe.iot.tocards.androidapp.MainActivity.Humidity;
import static cpe.iot.tocards.androidapp.MainActivity.Luminosity;
import static cpe.iot.tocards.androidapp.MainActivity.Temperature;

public class SensorsArrayAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> sensorsList = new ArrayList<>();

    public SensorsArrayAdapter(@NonNull Context context, /*@LayoutRes*/ ArrayList<String> list) {
        super(context, android.R.layout.simple_spinner_item , modifySensorList(list));
        mContext = context;
        //sensorsList = list;
    }

    private ArrayList<String> modifySensorList(ArrayList<String> sList) {

        sensorsList = new ArrayList<String>();
        for (String sensorLetter : sList) {

            String sensorWord = "";
            switch(sensorLetter) {
                case "T":
                    sensorWord = "Temperature";
                    break;
                case "H":
                    sensorWord = "Humidity";
                    break;
                case "L":
                    sensorWord = "Luminosity";
                    break;
                default:
                    break;
            }

            if(sensorWord != "") {
                sensorsList.add(sensorWord);
            }
        }

        return sensorsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            //convertView = View.inflate(parent.getContext(), android.R.layout.simple_spinner_item, null);
            convertView = View.inflate(parent.getContext(), android.R.layout.simple_spinner_dropdown_item, null);
        }
        if(!isEnabled(position)) {
            convertView.setEnabled(false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //NO-OP: Just intercept click on disabled item
                }
            });
        }

        return convertView;
        /*
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);

        Movie currentMovie = moviesList.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.imageView_poster);
        image.setImageResource(currentMovie.getmImageDrawable());

        TextView name = (TextView) listItem.findViewById(R.id.textView_name);
        name.setText(currentMovie.getmName());

        TextView release = (TextView) listItem.findViewById(R.id.textView_release);
        release.setText(currentMovie.getmRelease());

        return listItem;
        */
    }

    @Override
    public boolean isEnabled(int position) {
        //return super.isEnabled(position);
        //return getItem(position).isEnabled();
        return true;
    }
}
