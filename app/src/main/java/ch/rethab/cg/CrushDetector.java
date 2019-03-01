package ch.rethab.cg;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.LinkedList;

public class CrushDetector implements SensorEventListener {

    private final CrushListener listener;

    private static final int WINDOW_SIZE = 50;
    private final LinkedList<Point> window = new LinkedList<>();

    private long ticksSinceLastCheck = 0;

    CrushDetector(CrushListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        ticksSinceLastCheck++;
        window.addLast(new Point(event.values[1]));
        while (window.size() > WINDOW_SIZE) {
            window.removeFirst();
        }

        if (shallCheck()) {
            ticksSinceLastCheck = 0;

            if (didCrush()) {
                this.listener.onCrush();
                this.window.clear();
            }
        }

    }

    private boolean didCrush() {
        if (this.window.size() < 3) return false;

        Point[] window = this.window.toArray(new Point[0]);
        for (int i = 0; i < window.length-2; i++) {
            Point fst = window[i];
            Point snd = window[i+1];
            Point trd = window[i+2];

            float fstDiff = Math.abs(fst.y - snd.y);
            float sndDiff = Math.abs(snd.y - trd.y);

            float factor = sndDiff / fstDiff;
            float diff = Math.abs(sndDiff - fstDiff);
            if (diff > 3 && factor > 5) {
                Log.d("CrushDetector", String.format("didCrush: %.2f / %.2f = %.2f", sndDiff, fstDiff, factor));
                return true;
            }
        }

        return false;
    }

    private boolean shallCheck() {
        return ticksSinceLastCheck > 10;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private static class Point {
        final float y;
        Point(float y) { this.y = y; }
    }
}

interface CrushListener {
    void onCrush();
}
