import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

//I AM THE DRIVAH
public class DriverTask implements Runnable {
    private EV3LargeRegulatedMotor motorL = new EV3LargeRegulatedMotor(MotorPort.A);
    private EV3LargeRegulatedMotor motorR = new EV3LargeRegulatedMotor(MotorPort.D);

    @Override
    public void run() {

    }

    private void driveForward() {
        motorL.forward();
        motorR.forward();
    }

    private void driveBackwards() {
        motorL.backward();
        motorR.backward();
    }

}
