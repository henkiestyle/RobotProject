package tools;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.ColorDetector;
import lejos.robotics.ColorIdentifier;

/**
 * 
 * @author Raymon Deze klasse zet de methodes voor het gebruik van de
 *         kleursensor klaar.
 * 
 */

public class ColorTools implements ColorDetector, ColorIdentifier {

	private EV3ColorSensor sensor;
	public float[] sample;
	private static final int OFFSET = 0;

	// Constructor die een nieuw sensor object aan maakt en de poort waarop
	// deze is aangesloten als argument moet hebben
	public ColorTools(Port port) {
		sensor = new EV3ColorSensor(port);
		setFloodLight(false);
	}

	// Vraag de sensor zelf op voor het gebruik van super methodes
	public EV3ColorSensor getSensor() {
		return sensor;
	}

	// Set mode van de kleursensor:
	// 1. Red mode = "Red"
	// 2. Ambient mode = "Ambient"
	// 3. Color ID mode = "ColorID"
	// 4. RGB mode = "RGB"

	public void setMode(String mode) {
		sensor.setCurrentMode(mode);
		sample = new float[sensor.sampleSize()];
	}

	// Vraag de ingestelde mode van het sensor object op
	public int getMode() {
		return sensor.getCurrentMode();
	}

	/**
	 * Returns huidige gedetecteerde kleur. Gebruik met 'Color Id' mode.
	 * 
	 * @return Color id. Color id's zitten in het Color object.
	 */

	@Override
	public int getColorID() {
		sensor.fetchSample(sample, OFFSET);

		return (int) sample[OFFSET];
	}

	/**
	 * Returns Color object huidige gedetecteerde kleur. Gebruik met 'RGB' mode en wit
	 * licht op doel. NB: deze waardes zijn relatieve intensiteit van het
	 * gereflecteerde licht van de primaire kleuren. Dit is niet de RGB waarde die
	 * de kleur van het oppervlak zou reproduceren.
	 * 
	 * @return Color object met RGB intensiteitswaardes van gedetecteerde kleur.
	 */
	@Override
	public Color getColor() {
		sensor.fetchSample(sample, OFFSET);

		return new Color((int) (sample[OFFSET] * 255), (int) (sample[1] * 255), (int) (sample[2] * 255));
	}

	/**
	 * Return omgevingslicht waarde. Gebruik met 'Ambient' mode. Sensor led moet uit
	 * staan.
	 * 
	 * @return Licht niveau as range 0 to 1.
	 */
	public float getAmbient() {
		sensor.fetchSample(sample, OFFSET);
		return sample[OFFSET];
	}

	/**
	 * Return Rood licht niveau. Gebruik met 'Red' mode. Sensor led moet rood zijn.
	 * 
	 * @return Licht niveau as range 0 to 1.
	 */
	public float getRed() {
		sensor.fetchSample(sample, OFFSET);
		return sample[OFFSET];
	}

	/**
	 * Release resources.
	 */
	public void close() {
		sensor.close();
	}

	/**
	 * Return huidige status van floodlight led.
	 * 
	 * @return True als aan, false als uit.
	 */
	public boolean isFloodLightOn() {
		return sensor.isFloodlightOn();
	}

	/**
	 * Set floodlight led aan/uit met default kleur.
	 * 
	 * @param on
	 *            True om floodlight aan te zetten, false voor uit.
	 */
	public void setFloodLight(boolean on) {
		sensor.setFloodlight(on);
	}

	/**
	 * Set floodlight default led kleur.
	 * 
	 * @param color
	 *            Color id waarde van Color object.
	 */
	public void setFloodLight(int color) {
		sensor.setFloodlight(color);
	}

	/**
	 * Map color integer naar naam.
	 * 
	 * @param color
	 *            Color id waarde.
	 * @return String met kleur naam.
	 */
	public static String colorName(int color) {
		switch (color) {
		case Color.NONE:
			return "None";

		case Color.BLACK:
			return "Black";

		case Color.BLUE:
			return "Blue";

		case Color.BROWN:
			return "Brown";

		case Color.CYAN:
			return "Cyan";

		case Color.DARK_GRAY:
			return "Dark Gray";

		case Color.GRAY:
			return "Gray";

		case Color.GREEN:
			return "Green";

		case Color.LIGHT_GRAY:
			return "Light Gray";

		case Color.MAGENTA:
			return "Magenta";

		case Color.ORANGE:
			return "Orange";

		case Color.PINK:
			return "Pink";

		case Color.RED:
			return "Red";

		case Color.WHITE:
			return "White";

		case Color.YELLOW:
			return "Yellow";
		}

		return "";
	}

}
