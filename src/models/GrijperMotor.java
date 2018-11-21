package models;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.Button;

/**
 * 
 * @author Renke
 *		Deze klasse sluit en opent de grijper
 */
public class GrijperMotor {
	public static final int OPENINGSROTATIE = 1430;
	private float snelheid;
	public final static int DEFAULTSNELHEID = 500;
	private EV3MediumRegulatedMotor motor;
	
//	de constructor maakt een motor aan
	public GrijperMotor() {
		this(DEFAULTSNELHEID);
		
	}
	
	public GrijperMotor(int snelheid) {
		this.motor = new EV3MediumRegulatedMotor(MotorPort.A);
		this.snelheid = motor.getMaxSpeed();
	}
	
//	sluit de grijper tot op knop gedrukt wordt en print aantal graden uit
	public void testSluit() {
		int rotaties;
		this.motor.resetTachoCount();
		this.motor.setSpeed(snelheid);
		this.motor.forward();
		Button.waitForAnyPress();
		this.motor.stop();
		rotaties = this.motor.getTachoCount();
		System.out.printf("draaide: %d", rotaties);
	}
	
//	sluit de grijper helemaal van standaardopening
	public void sluit() {
		this.motor.setSpeed(this.snelheid);
		this.motor.rotate(OPENINGSROTATIE);		
	}
	
//	sluit de grijper met de ingevoerde hoek
	public void sluit(int hoek) {
		this.motor.setSpeed(this.snelheid);
		this.motor.rotate(hoek);		
	}

// opent de grijper de standaardopening
	public void open() {
		this.motor.setSpeed(this.snelheid);
		this.motor.rotate(-OPENINGSROTATIE);
	}
	
// opent de grijper de standaardopening
	public void open(int hoek) {
		this.motor.setSpeed(this.snelheid);
		this.motor.rotate(-hoek);
	}
	
	public void stop() {
		this.motor.stop();
		this.motor.close();
	}
}
