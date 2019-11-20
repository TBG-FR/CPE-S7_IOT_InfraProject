package cpe.iot.tocards.androidapp;

import android.graphics.drawable.Icon;

public class SensorData {

    private Icon Icon;
    private SensorTypeEnum Type;
    private double Value;

    SensorData(Icon icon, SensorTypeEnum type)
    {
        Icon = icon;
        Type = type;
        Value = 0.0;
    }

    public Icon getIcon()
    {
        return Icon;
    }

    public SensorTypeEnum getType() { return Type; }

    public String getTitle() { return Type.toString(); }

    public String getValue()
    {
        return Double.toString(Value);
    }

    public void setValue(double value) { Value = value; }
}
