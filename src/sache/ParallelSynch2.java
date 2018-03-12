package sache;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

/* 2018, B.Marti
 * Startet 6 Threads: Motor, Touch, EscapeButton, Color, Gyro und Ultraschall
 * Der Motorthread f�hrt langsam vorw�rts
 * der Touchthread unterbricht die Fahrt solange gedr�ckt
 * Der Ultrasonic �berpr�ft ob n�her als 10cm, f�hrt 2 Sekunden zur�ck
 * Der EscapeButton Thread unterbricht und beendet das Programm
 * Der Colorthread gibt die COlor ID aus
 * Der Gyro gibt den aktuellen winkel aus
*/

public class ParallelSynch2 {

	private EV3LargeRegulatedMotor motorL = new EV3LargeRegulatedMotor(MotorPort.A);
	private EV3LargeRegulatedMotor motorR = new EV3LargeRegulatedMotor(MotorPort.D);
	private EV3TouchSensor touchSensor = new EV3TouchSensor(SensorPort.S3);
	private float[] touchSample = new float[1];
	private EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S4);
	private float[] sampleColor = new float[3];
	private EV3UltrasonicSensor uSensor = new EV3UltrasonicSensor(SensorPort.S1);
	private float[] sampleSonic;
	boolean motorchanged = true;
	private EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S2);
	private float[] angle = { 0.0f };
	private SampleProvider gyroSamples = null;
	
	// Escape Button beendet das programm
	class EscapeButton implements Runnable {
		public void run() {
			while(Button.ESCAPE.isUp()) {	
				try{
					Thread.sleep(10);
				} catch (InterruptedException e) {break;}
			}
		}
	}
	
	// lies die Color ID (1 bis 8) ein und gibt aus
	class ColorClass implements Runnable {
		public void run() {		
			colorSensor.setCurrentMode("ColorID");
			colorSensor.setFloodlight(false);
			
			while(!Thread.currentThread().isInterrupted()){
				colorSensor.fetchSample(sampleColor, 0);
				LCD.drawString("ColorID: "+colorSensor.getColorID(), 0, 4);
				try{
					Thread.sleep(500);
				} catch (InterruptedException e) {break;}
				LCD.refresh();
			}
		}
	}
	
	//führt bei Hinderniss 2 s zurück
	class UltrasoundClass implements Runnable {
		public void run() {
			uSensor.setCurrentMode("Distance");
			sampleSonic = new float[uSensor.sampleSize()];
			int distanceCM = 0;
			while (!Thread.currentThread().isInterrupted()){
				uSensor.fetchSample(sampleSonic, 0);
				distanceCM = (int)(sampleSonic[0]*100);
				LCD.drawString("                        ", 0, 3);
				LCD.drawString("Distance: "+distanceCM, 0, 3);
				if (distanceCM<10){  			// ca 10 zentimeter
					synchronized (motorL) {   // warte bis motor frei ist, dann reservier
						motorL.setSpeed(50);
						motorR.setSpeed(50);
						motorR.backward();
						motorL.backward();
						motorchanged=true;
						
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							System.out.println("UltrasoundThread wurde beendet!!");
							break;
						}
					}
				}
			}
		}
	}
	
	// F�hrt langsam vorw�rts
	class MotorClass implements Runnable {
		public void run() {
			
			while(!Thread.currentThread().isInterrupted()) {	
				synchronized(motorL){
					if(motorchanged){     // wenn motor frei, reservier
						motorL.setSpeed(100);
						motorR.setSpeed(100);
						motorR.forward();
						motorL.forward();
						motorchanged=false;
					}
				}
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {break;}
			}
		}
	}

	// Gibt den Winkel vom Gyro auf dem Display aus
	class GyroClass implements Runnable {
		public void run() {
			
			while(!Thread.currentThread().isInterrupted()) {	
				gyroSamples.fetchSample(angle, 0);
				LCD.drawString("Gyro Angle: "+angle[0], 0, 5);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {break;}
			}
		}
	}
	
	// Motor ist im float wenn gedr�ckt
	class TouchClass implements Runnable {
        public void run() {     
            touchSensor.setCurrentMode("Touch");
             
            while(!Thread.currentThread().isInterrupted()){
               //lies den touch sensor
            	touchSensor.fetchSample(touchSample, 0);
            	//nimm den Motor wenn frei            		
        		synchronized(motorL){ 
        			while(touchSample[0]==1){
	            		motorchanged=true;
	                    motorL.flt(true);
	                    motorR.flt();
	                    touchSensor.fetchSample(touchSample, 0);
	                    try {
	                        Thread.sleep(100);
	                    } catch (InterruptedException e) {}
	                }	
            	}
                 
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println("Touchthread wurde beendet!!");
                    break;}
            } 
        }
    }
	
	public ParallelSynch2() throws InterruptedException {
		//initialisiere und resete den Gyro
		gyroSamples = gyro.getAngleMode();
        gyro.reset();
		
		Thread t1 = new Thread( new MotorClass() );		
		Thread t2 = new Thread( new TouchClass() );
		Thread t3 = new Thread( new EscapeButton() );
		Thread t4 = new Thread( new UltrasoundClass() );
		Thread t5 = new Thread( new ColorClass() );
		Thread t6 = new Thread( new GyroClass() );
		
		t1.start();		
		t2.start();	
		t3.start();	
		t4.start();	
		t5.start();	
		t6.start();	
		
		LCD.drawString("6 threads started", 0, 1);
		
		//warten auf das Ende des EscapeButton threads. Dann alle beenden
		t3.join();
		if(t1.isAlive()) t1.interrupt();
		if(t2.isAlive()) t2.interrupt();
		if(t4.isAlive()) t4.interrupt();
		if(t5.isAlive()) t5.interrupt();
		if(t6.isAlive()) t6.interrupt();	
	}

	public static void main(String[] args) throws InterruptedException { 
		ParallelSynch2 p = new ParallelSynch2();
	}
}
