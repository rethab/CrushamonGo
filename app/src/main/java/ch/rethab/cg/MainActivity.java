package ch.rethab.cg;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import ch.rethab.cg.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements CrushListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private CrushDetector crushDetector;
    private CrushStorage crushStorage;

    private UserViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        crushDetector = new CrushDetector(this);
        crushStorage = new CrushStorage(Volley.newRequestQueue(this));

        model = ViewModelProviders.of(this).get(UserViewModel.class);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setUserViewModel(model);

        crushStorage.getPlayer(model.getPlayerName(), new Response.Listener<Player>() {
            @Override
            public void onResponse(Player player) {
                model.setCrushes(player.crushes);
                Log.d("MainActivity", "model.setCrushes("+player.crushes+")");
            }
        });
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
        crushStorage.incCrushes(model.getPlayerName(), new Response.Listener<Player>(){
            @Override
            public void onResponse(Player player) {
                model.setCrushes(player.crushes);
            }
        });
    }
}

