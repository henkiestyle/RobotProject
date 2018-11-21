package models;

/**
 * @author Bastiën deze class maakt het mogelijk om meerdere taken tegelijk uit
 *         te voeren. In dit geval vooral padVolger + achtergrondmuziek
 */

public class MultiThread extends TakenModule {

	private PadVolger padVolger;
	private MusicPlayer musicPlayer;

	public MultiThread() {// Een MultiThread wordt altijd gemaakt met een MusicPlayer om de muziek af te
							// spelen en een Padvolger om het pad te volgen
		this.musicPlayer = new MusicPlayer();
		this.padVolger = new PadVolger();

	}

	public void voerUit() {
		Thread musicThread = new Thread(musicPlayer);
		Thread padVolgerThread = new Thread(padVolger);
		padVolgerThread.start();// Hiermee start je beide threads (start roept vanuit zichzelf op zijn beurt
								// run() aan in de bijbehorende classes)
		musicThread.start();
		try {
			padVolgerThread.join();// Hiermee wordt er gewacht tot de padvolger thread klaar is om de muziek te
			// stoppen
			musicThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		musicPlayer.stop();
		padVolger.stop();
	}

}
