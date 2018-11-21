package models;

import java.io.File;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import tools.InfraroodTools;

/**
 * @author Ray Klasse die het mogelijk maakt om een object of een beacon
 * op te pakken en ergens anders neer te zetten.
 *
 */

/**
 * @author Cristina
 */

public class Handler extends TakenModule {

	private InfraroodTools irSensor;
	private Verplaatsen beweeg = new Verplaatsen();
	private GrijperMotor grijper = new GrijperMotor();
	private MusicPlayer beer;
	private final static int DISTANCE_CHANNEL = 3;
	private final static int HEADING_CHANNEL = 2;
	private final static int CORRECTIE_THRESHOLD = 2;
	private final static int GEEN_SNELHEID = 0;
	private final static int LAGE_SNELHEID = 20;
	private final static int GEMIDDELDE_SNELHEID = 30;
	private final static int HOGE_SNELHEID = 50;
	private final static int BEACON_NEAR = 5;
	private float heading = 0;
	private float distance = 0;

	// Constructor van de klasse die tegelijkertijd een sensor object aanmaakt
	public Handler() {
		this.irSensor = new InfraroodTools(SensorPort.S2);
		this.beer = new MusicPlayer();
	}

	// Methode om de robot te laten stoppen met rijden
	public void stopRijden() {
		// Zet de motoren stil dmv snelheid = 0
		beweeg.motorPower(GEEN_SNELHEID, GEEN_SNELHEID);
		beweeg.rijVooruit();
	}

	// Methode om de robot een object te laten pakken en meenemen
	public void grijpen(int delay) {
		// Grijper openen
		grijper.open();
		// Stukje vooruit rijden
		beweeg.motorPower(LAGE_SNELHEID, LAGE_SNELHEID);
		beweeg.rijVooruit();
		Delay.msDelay(delay);
		// Stoppen en armen weer sluiten
		stopRijden();
		grijper.sluit();
		// Draaien naar de originele rijrichting
		beweeg.motorPower(HOGE_SNELHEID, -HOGE_SNELHEID);
		beweeg.rijVooruit();
		Delay.msDelay(2000);
		// Stoppen met draaien
		stopRijden();
		// Achterwielen recht zetten
		beweeg.motorPower(-HOGE_SNELHEID, HOGE_SNELHEID);
		beweeg.rijVooruit();
		Delay.msDelay(500);
		stopRijden();
	}

	// Methode om de robot een object weer neer te zetten
	public void neerzetten() {
		// Stop met rijden
		stopRijden();
		// Open de armen
		grijper.open();
		// Stukje achteruit rijden
		beweeg.motorPower(-GEMIDDELDE_SNELHEID, -GEMIDDELDE_SNELHEID);
		beweeg.rijVooruit();
		Delay.msDelay(4000);
		// Stoppen en armen sluiten
		stopRijden();
		grijper.sluit();
	}

	// Methode om te rijden tussen handelingen
	public void wegRijden(int delay) {
		// Rijden + 'beer beer beer' afspelen
		beweeg.motorPower(HOGE_SNELHEID, HOGE_SNELHEID);// rijdt terug
		beweeg.rijVooruit();
		Delay.msDelay(delay);
	}

	public void readyToGo() {
		// Toon om aan te geven dat het programma start en knipper licht
		Sound.twoBeeps();
		Button.LEDPattern(4);
	}

	// Methode die het afsluiten van de functie aangeeft
	public void finishedTheShow() {
		Button.LEDPattern(4);
		Sound.beepSequence();
		Delay.msDelay(5000);
		Button.LEDPattern(0);
	}

	// Methode om te bepalen waar de beacon is
	public void updateLocation() {
		// Vraag de richting van de beacon op
		heading = irSensor.getBeacon()[HEADING_CHANNEL];
		// Vraag de afstand tot de beacon op
		distance = irSensor.getBeacon()[DISTANCE_CHANNEL];
	}

	// Methode om een voorwerp op te pakken en te verplaatsen
	public void pakVoorwerp() {
		// Sensor op distance mode zetten
		irSensor.setMode(0);
		// Melding om aan te geven dat de functie start
		readyToGo();
		// Afstand opvragen en rijden
		float distanceValue = 0;
		try {
			distanceValue = irSensor.getRange();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// rijden tot bij de voorwerp en afstand opniew opvragen
		while (distanceValue > 28) {
			beweeg.rijVooruit();
			distanceValue = irSensor.getRange();
		}

		// Stoppen binnen bepaalde afstand/bij voorwerp
		stopRijden();
		Sound.beepSequenceUp();
		Button.LEDPattern(9);
		// Voorwerp oppakken
		grijpen(3400);
		// Verplaatsen
		wegRijden(5000);
		// Voorwerp neerzetten
		neerzetten();
		// Melding om aan te geven dat de functie stopt
		finishedTheShow();
	}

	// Methode om de beacon op te pakken en te verplaatsen (met sturen)
	public void zoekVoorwerp() {
		// Sensor in beacon modus zetten
		irSensor.setMode(1);
		// Afstand en richting van beacon opvragen
		updateLocation();
		Delay.msDelay(1000);
		// Melding om aan te geven dat de functie start
		readyToGo();
		// Begin met rijden
		wegRijden(2000);

		// Rijden richting de beacon
		while (distance > BEACON_NEAR) {
			int power;
			int correctieFactor = 2;
			int distance_threshold = 20;

			// Blijf doorrijden zolang de beacon buiten een bepaald bereik is
			if (distance > distance_threshold) {
				power = HOGE_SNELHEID;
				if (heading < -CORRECTIE_THRESHOLD) {
					// Stuurt bij op hoge snelheid als de beacon links staat en vraag nieuwe locatie
					beweeg.motorPower((int) (power + (correctieFactor * heading)), power);
					beweeg.rijVooruit();
					updateLocation();
					System.out.println("Heading: " + heading + "Distance: " + distance);
				} else if (heading > CORRECTIE_THRESHOLD) {
					// Stuurt bij op hoge snelheid als de beacon rechts staat en vraag nieuwe
					// locatie
					beweeg.motorPower(power, (int) (power - (correctieFactor * heading)));
					beweeg.rijVooruit();
					updateLocation();
					System.out.println("Heading: " + heading + "Distance: " + distance);
				} else {
					// Rij rechtdoor op hoge snelheid en vraag nieuwe locatie
					beweeg.motorPower(power, power);
					beweeg.rijVooruit();
					updateLocation();
					System.out.println("Heading: " + heading + "Distance: " + distance);
				}
			} else if (distance <= distance_threshold) {
				power = LAGE_SNELHEID;
				if (heading < -CORRECTIE_THRESHOLD) {
					// Stuurt bij op lage snelheid als de beacon links staat en vraag nieuwe locatie
					beweeg.motorPower((int) (power + (correctieFactor * heading)), power);
					beweeg.rijVooruit();
					updateLocation();
					System.out.println("Heading: " + heading + "Distance: " + distance);
				} else if (heading > CORRECTIE_THRESHOLD) {
					// Stuurt bij op lage snelheid als de beacon rechts staat en vraag nieuwe
					// locatie
					beweeg.motorPower(power, (int) (power - (correctieFactor * heading)));
					beweeg.rijVooruit();
					updateLocation();
					System.out.println("Heading: " + heading + "Distance: " + distance);
				} else {
					// Rij rechtdoor op hoge snelheid en vraag nieuwe locatie
					beweeg.motorPower(power, power);
					beweeg.rijVooruit();
					updateLocation();
					System.out.println("Heading: " + heading + "Distance: " + distance);
				}
			} else {
				break;
			}
		}

		// Stop met rijden voor de beacon
		stopRijden();
		Sound.beepSequenceUp();
		Button.LEDPattern(9);
		// Pak de beacon op
		grijpen(4000);
		// Rij weg met de beacon
		// beer.playBeerSong();
		wegRijden(5000);
		// Zet de beacon weer neer
		neerzetten();
		// Melding om aan te geven dat de functie stopt
		finishedTheShow();
	}

	// Methode om de beacon op te pakken en te verplaatsen (met sturen) -- LANGZAAM
	public void zoekVoorwerpLangzaam() {
		// Sensor in beacon modus zetten
		irSensor.setMode(1);
		// Afstand en richting van beacon opvragen
		updateLocation();
		Delay.msDelay(1000);
		// Melding om aan te geven dat de functie start
		readyToGo();
		// Begin met rijden
		wegRijden(2000);

		// Rijden richting de beacon
		while (distance > BEACON_NEAR) {
			int power;
			int correctieFactor = 2;
			int distance_threshold = 20;

			// Blijf doorrijden zolang de beacon buiten een bepaald bereik is
			power = LAGE_SNELHEID;
			if (heading < -CORRECTIE_THRESHOLD) {
				// Stuurt bij op hoge snelheid als de beacon links staat en vraag nieuwe locatie
				beweeg.motorPower((int) (power + (correctieFactor * heading)), power);
				beweeg.rijVooruit();
				updateLocation();
				System.out.println("Heading: " + heading + "Distance: " + distance);
			} else if (heading > CORRECTIE_THRESHOLD) {
				// Stuurt bij op hoge snelheid als de beacon rechts staat en vraag nieuwe
				// locatie
				beweeg.motorPower(power, (int) (power - (correctieFactor * heading)));
				beweeg.rijVooruit();
				updateLocation();
				System.out.println("Heading: " + heading + "Distance: " + distance);
			} else {
				// Rij rechtdoor op hoge snelheid en vraag nieuwe locatie
				beweeg.motorPower(power, power);
				beweeg.rijVooruit();
				updateLocation();
				System.out.println("Heading: " + heading + "Distance: " + distance);
			}
		}

		// Stop met rijden voor de beacon
		stopRijden();
		Sound.beepSequenceUp();
		Button.LEDPattern(9);
		// Pak de beacon op
		grijpen(4000);
		// Rij weg met de beacon
		wegRijden(5000);
		// Zet de beacon weer neer
		neerzetten();
		// Melding om aan te geven dat de functie stopt
		finishedTheShow();
	}

	// Methode overgekomen uit de super klasse om een van de functies te
	// activeren

	@Override
	public void voerUit() {
		int knop = 0;
		// Luister naar een knop om een van beide mogelijkheden te selecteren
		while (knop != Button.ID_ESCAPE) {
			// Print de keuzemogelijkheden op het scherm
			System.out.printf("Druk \n-links voor Pak Voorwerp, \n-rechts voor Zoek Object" + "\nbeneden voor Langzaam"
					+ "\n-escape voor stop");
			knop = Button.waitForAnyPress();
			// Als de knop naar links word ingedrukt, doe pakVoorwerp
			if (knop == Button.ID_LEFT) {
				pakVoorwerp();
				stop();
			}
			// Als de knop naar rechts word ingedrukt, doe zoekVoorwerp
			else if (knop == Button.ID_RIGHT) {
				zoekVoorwerp();
				stop();
			}
			// Als de knop naar rechts word ingedrukt, doe zoekVoorwerpLangzaam
			else if (knop == Button.ID_DOWN) {
				zoekVoorwerpLangzaam();
				stop();
			}
		}
		System.out.println("Einde programma, druk op een toets om af te sluiten");
		Button.waitForAnyPress();

	}

	// Methode om alle gebruikte resources weer af te sluiten
	@Override
	public void stop() {
		beweeg.stop();
		grijper.stop();
		irSensor.close();

	}

}