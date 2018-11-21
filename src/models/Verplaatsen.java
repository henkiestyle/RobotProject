package models;

import lejos.hardware.motor.*;
import lejos.hardware.port.*;


/**
 * @author Cristina Deze klasse zorgt voor het aansturen van de motoren.
 *
 */
public class Verplaatsen {
	
	private UnregulatedMotor motorA;
	private UnregulatedMotor motorB;
	private final int POWER = 50;// motoren default vermogen
	private int powerA;//vermogen motor A
	private int powerB;//vermogen motor B

	// Maak twee motor objecten aan om de motoren te controleren/bedienen en set hun
	// vermogen naar de default waarde
	public Verplaatsen() {
		this.motorA = new UnregulatedMotor(MotorPort.C);
		this.motorB = new UnregulatedMotor(MotorPort.D);
		this.powerA = POWER;
		this.powerB = POWER;
	}

	// Pas vermogen aan om verschillende bewegingen uit te kunnen voeren.
	public void motorPower(int powerA, int powerB) {
		this.powerA = powerA;
		this.powerB = powerB;
	}

	// Laat robot vooruit rijden op basis van de meegegeven snelheid.
	// Een negatieve waarde zorgt ervoor dat die bepaalde motor achteruit gaat draaien
	public void rijVooruit() {
		motorA.forward();
		motorB.forward();
		motorA.setPower(powerA);
		motorB.setPower(powerB);

	}

	// Stop robot en geef motor resources vrij
	public void stop() {
		motorA.stop();//stop motoren met remmen aan
		motorB.stop();
		motorA.close();// geef motor resources vrij 
		motorB.close();
	}

}
