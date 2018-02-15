package spl.main;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import spl.management.Management;


/**
 * The Class Driver which is creating the simulation.
 */
public class Driver {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		final Logger LOGGER = Logger.getLogger(Driver.class.getName());
		try {
			FileHandler myFileHandler = new FileHandler("myLog.log");
			myFileHandler.setFormatter(new LogFromatter());
			LOGGER.addHandler(myFileHandler);
			LOGGER.setLevel(Level.FINEST);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		LOGGER.info("logger created");


		Management management = new Management(LOGGER);
		Init.parseAll(args, management, LOGGER);
		management.simulate();
		management.printStatistics();		

	}

}
