package tools;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.RangeFinder;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

/**
 * @author Ray Deze klasse zet de methodes voor het gebruik van de
 * kleursensor klaar.
 *
 */

/**
 * @author Cristina
 *
 */

public class InfraroodTools implements RangeFinder {

	private static final int OFFSET = 0;
	private EV3IRSensor sensor;
	private SampleProvider sp;
	public float[] sample;
	private static final int HALF_SECOND = 500;
	private static final int ITERATION_THRESHOLD = 10;

	// Constructor die een nieuw InfraroodSensor object aan maakt en de poort
	// waar deze op aangesloten is als argument vraagt
	public InfraroodTools(Port port) {
		sensor = new EV3IRSensor(port);
	}

	// Set de mode van de infraroodsensor:
	// 1. Afstandsmodus (0), geeft afstand tot object in %
	// 2. Beaconmodus (1), geeft locatie van beacon met afstand in % en heading
	// tussen -25 en 25
	// 3. Remotemodus (2), geeft de ID van de ingedrukte knop

	public void setMode(int mode) {
		int chan = 1;
		sensor.setCurrentMode(mode);
		if (mode == 0) {
			sp = sensor.getDistanceMode();
		} else if (mode == 1) {
			sp = sensor.getSeekMode();
		} else if (mode == 2) { // (nog) niet geimplementeerd
			sensor.getRemoteCommand(chan);
		}
		sample = new float[sp.sampleSize()];
	}

	// Vraag de sensor zelf op voor het gebruik van super methodes
	public EV3IRSensor getSensor() {
		return sensor;
	}

	/**
	 * Get range (distance) tot object gedetecteerd door Infrarood sensor.
	 * 
	 * @return Distance in procent van max proximity (0=dichtbij, 100=ver). Er komt
	 *         slechts 1 waarde terug.
	 */

	@Override
	public float getRange() {
		int distanceValue = 0;
		// Maak nieuwe array aan
		sample = new float[sp.sampleSize()];
		// vraag de waarde van de afstandsmeting op en zet deze in de array
		sp.fetchSample(sample, OFFSET);
		// Stel de int afstand in op de waarde uit de array om de positive infinity te
		// kunnen casten
		distanceValue = (int) sample[OFFSET];
		// Als afstand groter is dan 100 (positive infinity), stel deze in op 100
		if (distanceValue > 100) {
			distanceValue = 100;
		}
		// Geef de juiste waarde voor afstand terug
		return distanceValue;
	}

	/**
	 * Get range (distance) tot object gedetecteerd door Infrarood sensor.
	 * 
	 * @return Distance in procent van max proximity (0=dichtbij, 100=ver). Er komt
	 *         een array van waardes terug.
	 */

	@Override
	public float[] getRanges() {
		int distanceValue = 0;
		// Stel een aantal iteraties in voor meer nauwkeurigheid
		for (int i = 0; i < ITERATION_THRESHOLD; i++) {
			// Maak een nieuwe array aan
			sample = new float[sp.sampleSize()];
			// Vul de array met de gemeten waarde
			sp.fetchSample(sample, OFFSET);
			// Stel de int afstand in op de waarde uit de array om de positive infinity te
			// kunnen casten
			distanceValue = (int) sample[OFFSET];
			// Als afstand groter is dan 100 (positive infinity), stel deze in op 100
			if (distanceValue > 100) {
				distanceValue = 100;
			}
			// Zet een delay tussen de metingen om waardes niet te overschrijven
			Delay.msDelay(HALF_SECOND);
		}
		return sample;
	}

	/**
	 * Zoekt naar de beacon. Gebruik meerdere iteraties om de nauwkeurigheid te
	 * verhogen
	 * 
	 * @return Afstand en richting ten opzichte van de locatie van de beacon
	 *         (waardes tussen -25 en 25 voor richting, tussen 0 en 100 voor
	 *         afstand). Geeft een array van locaties terug(afstand en richting).
	 */

	public float[] getBeacon() {
		int bHeading1 = 0;
		int bDistance1 = 0;
		int bHeading2 = 0;
		int bDistance2 = 0;
		int bHeading3 = 0;
		int bDistance3 = 0;
		int bHeading4 = 0;
		int bDistance4 = 0;
		// For loop om de meetwaardes in een array op te slaan
		// Meerdere iteraties om de nauwkeurigheid te verhogen
		for (int i = 0; i < ITERATION_THRESHOLD; i++) {
			// Maak een array en vul deze met gemeten waardes
			sample = new float[sp.sampleSize()];
			try {
				sp.fetchSample(sample, OFFSET);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Sla de waardes van metingen op kanaal 1 op
			bHeading1 = (int) sample[0];
			bDistance1 = (int) sample[1];
			// Sla de waardes van metingen op kanaal 2 op
			bHeading2 = (int) sample[2];
			bDistance2 = (int) sample[3];
			// Sla de waardes van metingen op kanaal 3 op
			bHeading3 = (int) sample[4];
			bDistance3 = (int) sample[5];
			// Sla de waardes van metingen op kanaal 4 op
			bHeading4 = (int) sample[6];
			bDistance4 = (int) sample[7];
			// Delay om te voorkomen dat meetwaardes elkaar overschrijven
			Delay.msDelay(25);
		}
		return sample;
	}

	// Vraag de ingestelde mode van het sensor object op
	public int getMode() {
		return sensor.getCurrentMode();
	}

	/**
	 * Release resources.
	 */
	public void close() {
		sensor.close();
	}

}
