import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(MotorPort.A);
        motor.setSpeed(200);
        motor.forward();
        Thread.sleep(1000);
    }
}
