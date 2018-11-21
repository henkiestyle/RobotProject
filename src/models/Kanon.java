package models;

import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

/**
 * @author Bastiën kanon
 */

public class Kanon extends TakenModule {

	private UnregulatedMotor kanonMotor;
	public final int FULL_POWER = 100;// default motorpower voor het kanon
	public final int MIN_POWER = 35;
	public final int MIN_DELAY = 500; 
	public final int MAX_DELAY = 2000;

	public Kanon() {
		this.kanonMotor = new UnregulatedMotor(MotorPort.B);
	}

	public void schiet() {
		kanonMotor.setPower(FULL_POWER);
		kanonMotor.backward();
		Delay.msDelay(MIN_DELAY); // De motor draait achteruit voor 500ms
		kanonMotor.forward();
		kanonMotor.setPower(MIN_POWER);
		Delay.msDelay(MAX_DELAY); // De motor draait vooruit voor 500ms
		kanonMotor.stop();
		Delay.msDelay(MIN_DELAY);
	}

	@Override
	public void voerUit() {
		this.schiet();
	}

	@Override
	public void stop() {
		kanonMotor.close();
	}
}
