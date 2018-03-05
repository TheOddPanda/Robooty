package task;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class UltraSoundTask implements Runnable {

    private static final String DISTANCE_MODE = "Distance";
    private static final double impedimentDistance = 0.04;
    private EV3UltrasonicSensor uSensor;
    private float[] sampleSonic;

    public UltraSoundTask() {
        this.uSensor = new EV3UltrasonicSensor(SensorPort.S3);
    }

    @Override
    public void run() {
        uSensor.setCurrentMode(DISTANCE_MODE);
        sampleSonic = new float[uSensor.sampleSize()];

        while (!Thread.currentThread().isInterrupted() && sampleSonic[0] < impedimentDistance) {
            uSensor.fetchSample(sampleSonic, 0);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Ultrasoundtask terminated!");
            }
        }
    }

}
