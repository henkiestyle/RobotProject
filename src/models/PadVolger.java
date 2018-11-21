package models;
import lejos.hardware.Button;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
/**
 * @author: Renke/Bastiën
 * deze class is gemaakt om een lijn te kunnen volgen
 */
import tools.ColorTools;

public class PadVolger extends TakenModule implements Runnable {
	
	
	private ColorTools padSensor;
	private double intensiteit;	// de gemeten waarde van het licht, tussen 0 (zwart) en 1 (wit)
	private Verplaatsen verplaatsen;
	private int vermogenBocht;
	private int vermogenRechtdoor;
	private double maxLicht;  // de hoogst gemeten waarde gecalibreerd op wit
	private int vanPadTimer;  // telt hoe lang de sensor van het pad is, te gebruiken om zoekPad te starten
	private int spiraalTimer;  // telt hoe lang zoekPad al draait
	public static final int MAX_POWER = 100;
	public static final double MAX_DONKER = 0.25; //maximale hoeveelheid donker voor we het zwart noemen
										//Hiertussen ligt de sweetspot waar Robbie continu naar moet streven
	public static final double MIN_LICHT = 0.42; //de minste hoeveelheid intensiteit voor we het wit noemen
	public static final int ACHTERUITPOWER = 100;
	public static final int MAX_TIJD_VAN_PAD = 10000; // maximum aantal loops
	public static final int MACHTSFACTOR = -5;
	public static final int DEFAULTPOWER = 30; // vermogen standaard op 30%
	private static final int DELAY = 1000;  // wacht een seconde

	public PadVolger() {
		this.padSensor = new ColorTools(SensorPort.S1);
		this.verplaatsen = new Verplaatsen();
		this.padSensor.setMode("Red");
		this.vanPadTimer = MAX_TIJD_VAN_PAD;
		this.spiraalTimer = 0;
		this.vermogenBocht = DEFAULTPOWER;
		this.vermogenRechtdoor = DEFAULTPOWER;
	}
	
	public void voerUit() {
//		System.out.println("Calibreer wit");
//		Button.ENTER.waitForPress();
		leesLicht();
		this.maxLicht = intensiteit;
		System.out.printf("Witcalibratie = %f\n", intensiteit);
//		System.out.println("Druk op enter bij de start");
//		Button.ENTER.waitForPress();
		Delay.msDelay(DELAY);
		System.out.println("Druk omhoog om te stoppen");
		while (Button.UP.isUp()){
			
			rij();
		}
//		System.out.println("vanPadTimer: " + vanPadTimer);
//		System.out.println("spiraalTimer: " + spiraalTimer);
//		Button.ENTER.waitForPress();
	}
	
//	meet de lichtsterkte
	public void leesLicht() {
		intensiteit = padSensor.getRed();	
	}
	

//	kies tussen over het pad rijden en het pad zoeken
	public void rij () {
		if (vanPadTimer >= MAX_TIJD_VAN_PAD) {
			zoekPad();
		}
		else rijPad();
	}
	
//	robot rijdt in een spiraal tot het pad gevonden wordt
	public void zoekPad() {
		leesLicht();
		if (intensiteit > MIN_LICHT) {
			spiraalTimer++;
			verplaatsen.motorPower((int) (spiraalVerhouding() * vermogenBocht), vermogenBocht);
			verplaatsen.rijVooruit();
		}
		else {
			spiraalTimer = 0;
			vanPadTimer = 0;
		}
		
	}

//	maakt een asymptoot die 1 benadert: hoe hoger de spiraalTimer, hoe flauwer de bocht dus wordt
	private double spiraalVerhouding() {
		return (-2/Math.pow(spiraalTimer, MACHTSFACTOR)) + 1;
	}
	
	
//	rijdt over de rechterrand van een zwarte lijn op witte achtergrond
	public void rijPad() {
		leesLicht();
//		als de robot te veel op de lijn zit draait hij naar rechts: hoe donkerder hoe scherper de hoek
		if(intensiteit < MAX_DONKER) {
			verplaatsen.motorPower(vermogenBocht, (int)((vermogenBocht/MAX_POWER) * ((intensiteit/ MAX_DONKER) * (MAX_POWER+ACHTERUITPOWER)) - ACHTERUITPOWER));
			verplaatsen.rijVooruit();
			vanPadTimer = 0;
		}
//		als de robot te weinig op de lijn zit draait hij naar links: hoe donkerder hoe scherper de hoek
		if(intensiteit > MIN_LICHT) {
			verplaatsen.motorPower((int)((vermogenBocht/MAX_POWER)-((((intensiteit-MIN_LICHT)/(maxLicht-MIN_LICHT)) * (MAX_POWER+ACHTERUITPOWER)) - ACHTERUITPOWER)), vermogenBocht);
			verplaatsen.rijVooruit();
			if (intensiteit > vanPadLicht()) {
				vanPadTimer ++;
			}
			else vanPadTimer = 0;
		}
//		als de robot in de "sweet spot" zit, rijdt hij recht door
		else {
			verplaatsen.motorPower(vermogenRechtdoor, vermogenRechtdoor);
			verplaatsen.rijVooruit();
			vanPadTimer = 0;
		}
	}
	
//	beëindigt beweging en geeft poorten vrij (motorpoorten en sensorpoort)
	public void stop() {
		verplaatsen.stop();
		padSensor.close();
		
	}
	
//	meet de lichtsterkte en print die uit
	public void printLicht() {
		leesLicht();
		System.out.println(intensiteit);
	}
	
	public void setVermogenBocht(int vermogen) {
		this.vermogenBocht = vermogen;
	}

	public void setVermogenRechtdoor(int vermogenRechtdoor) {
		this.vermogenRechtdoor = vermogenRechtdoor;
	}
//	
//	public double getIntensiteit() {
//		return intensiteit;
//	}
	
	
//	berekent de lichtwaarde die beschouwd wordt als "geen pad te zien" als 3 keer zo dicht bij de calibratie wit waarde als de 
//	grenswaarde voor bochtcorrectie
	public double vanPadLicht() {
		return (MIN_LICHT + maxLicht*3)/4;
	}

//	om vanuit Multithread aangeroepen te worden
	@Override
	public void run() {
		voerUit();
		stop();
	}
	
	
	
}
