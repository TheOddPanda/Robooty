package obstacle.task;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

//I AM THE DRIVAH
public class DriverTask implements Runnable {
    private static final int DRIVE_SPEED = 20;
    private EV3LargeRegulatedMotor motorL;
    private EV3LargeRegulatedMotor motorR;

    public DriverTask() {
        motorL = new EV3LargeRegulatedMotor(MotorPort.A);
        motorR = new EV3LargeRegulatedMotor(MotorPort.D);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            driveForward(DRIVE_SPEED);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Thread was terminated.");
                break;
            }
        }

        motorL.flt();
        motorR.flt();
    }

    private void driveForward(final int driveSpeed) {
        motorL.setSpeed(driveSpeed);
        motorL.forward();
        motorR.forward();
        motorR.setSpeed(driveSpeed);
    }

}
