package spl.management;
import java.util.logging.Logger;


/**
 * The Class ClerkDetails.
 * 
 */
public class ClerkDetails {
	
	/** The name. */
	private String name;
	
	/** The location. */
	private Location location;
	
	/** The logger. */
	private Logger LOGGER;
	
	/**
	 * Instantiates a new clerk details.
	 *
	 * @param name the name
	 * @param location the location
	 * @param LOGGER the logger
	 */
	public ClerkDetails(String name, Location location, Logger LOGGER)
	{
		this.name = name;
		this.location = new Location(location);
		this.LOGGER = LOGGER;
		LOGGER.finer("new ClerkDetails: " + this.name);
	}
	
	/**
	 * Move to location.
	 *
	 * @param destinationLocation the destination location
	 * @return the long
	 */
	public long moveToLocation(Location destinationLocation)
	{
		long sleepmultiplier = (this.location.calculatDistance(destinationLocation)) * 2;
		LOGGER.fine(this.name + "moving to location for: " + sleepmultiplier + " seconds");
		try {
			Thread.sleep(sleepmultiplier * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LOGGER.fine(this.name + " finished moving to location");
		return sleepmultiplier;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

}
