package ch.rethab.cg;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements CrushListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private CrushDetector crushDetector;

    private TextView crushesView;
    private int crushes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        crushDetector = new CrushDetector(this);
        crushesView = findViewById(R.id.crushes);

        updateCrushesView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(crushDetector, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(crushDetector);
    }

    @Override
    public void onCrush() {
        crushes++;
        updateCrushesView();
    }

    private void updateCrushesView() {
        String newText = getResources().getQuantityString(R.plurals.crushes, crushes, crushes);
        crushesView.setText(newText);
    }
}

