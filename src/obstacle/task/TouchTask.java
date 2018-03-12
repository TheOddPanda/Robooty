package obstacle.task;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;

public class TouchTask implements Runnable {

    private static final String TOUCH_MODE = "Touch";
    private EV3TouchSensor touchSensor;
    private float[] touchSample = new float[1];

    public TouchTask() {
        touchSensor = new EV3TouchSensor(SensorPort.S1);
    }

    @Override
    public void run() {
        touchSensor.setCurrentMode(TOUCH_MODE);
        while (!Thread.currentThread().isInterrupted() && touchSample[0] == 1) {
            touchSensor.fetchSample(touchSample, 0);
        }
    }

}
