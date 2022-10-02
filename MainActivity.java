package com.example.test3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.lang.Math.*;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private String message;
    private double heavy_bound = 2500;
    private double medium_bound=1500;

    private String name; //user name
    private double mass=63.5;//user mass
    private String ph_number="+91";   //ph number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        begin();

        Log.d(TAG,"onCreate: Initializing Sensor service");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG,"onCreate: Registered accelerometer  listener");
    }

    protected void begin(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"message service enabled",Toast.LENGTH_SHORT).show();
            }
            else{
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x =sensorEvent.values[0];
        double y =sensorEvent.values[1];
        double z =sensorEvent.values[2];

        double impact_factor = (double) (Math.sqrt(x*x+y*y+z*z)*mass-(9*mass));
        Log.d(TAG,name+"x:"+sensorEvent.values[0]+" y: "+sensorEvent.values[1]+" z: "+z+" im:"+impact_factor);
        Toast.makeText(this,""+impact_factor,Toast.LENGTH_SHORT).show();
        if(impact_factor >= medium_bound && impact_factor <heavy_bound){
            message = "MediumImpact on User:"+impact_factor;
            sendSMS(ph_number,message);}

        else if(impact_factor>=heavy_bound){
            message = "HeavyImpact on User:"+impact_factor;
            sendSMS(ph_number,message);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    private void sendSMS(String ph_number,String message){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(ph_number, null,message, null, null);
            Toast.makeText(this, "Sent Message", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show();
        }
    }
}
