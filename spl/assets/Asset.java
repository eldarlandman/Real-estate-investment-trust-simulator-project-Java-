package spl.assets;
import java.util.Vector;
import java.util.logging.Logger;

import spl.management.Location;

/**
 * represents a real estate asset which is rented for customer groups.
 * 
 * "This object will hold information of a single asset."
 *
 * @author Elad & Gal
 */
public class Asset {
	
	/** The name of the asset. */
	private String name;
	
	/** The type of the asset. */
	private String type;
	
	/** The size of the asset. */
	private int size;
	
	/** The location of the asset. */
	private Location location;
	
	/** The asset's cost per night. */
	private int costPerNight;
	
	/** The current status of the asset. */
	private volatile String status;
	
	/** the health of the asset contents */
	private double health;
	
	/** The asset's contents. */
	private Vector<AssetContent> assetContents;
	
	/** The Constant AVAILABLE status. */
	public static final String AVAILABLE = "AVAILABLE";
	
	/** The Constant BOOKED status. */
	public static final String BOOKED = "BOOKED";
	
	/** The Constant OCCUPIED status. */
	public static final String OCCUPIED = "OCCUPIED";
	
	/** The Constant UNAVAILABLE status. */
	public static final String UNAVAILABLE = "UNAVAILABLE";
	
	/**
	 * Instantiates a new asset.
	 *
	 * @param name the name of the asset
	 * @param type the type of the asset
	 * @param size the size of the asset
	 * @param location the location of the asset
	 * @param costPerNight the asset's cost per night
	 * @param LOGGER the logger
	 */
	public Asset(String name, String type, int size, Location location, int costPerNight, Logger LOGGER)
	{
		this.name = name;
		this.type = type;
		this.size = size;
		this.location = new Location(location);
		this.costPerNight = costPerNight;
		this.assetContents = new Vector<AssetContent>();
		this.status = AVAILABLE;
		this.health=100;
		LOGGER.fine("new Asset: " + this.name + " " + this.type + " " + this.size + " " + this.costPerNight);		
	}
	
	/**
	 * Adds a new asset content to the asset
	 * Created for populating the asset with asset contents while reading the input information files
	 *
	 * @param addedContent the added asset content
	 */
	public void addAssetContent(AssetContent addedContent)
	{
		this.assetContents.addElement(addedContent);
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the asset's name
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Gets the asset's location.
	 *
	 * @return the location
	 */
	public Location getLocation()
	{
		return new Location(this.location);
	}
	
	/**
	 * Gets the asset's size.
	 *
	 * @return the size
	 */
	public int getSize()
	{
		return this.size;
	}
	
	/**
	 * Shift asset's status from available to booked.
	 *
	 * @return true, if successful
	 */
	public boolean shiftStatusAvailableToBooked()
	{
		if (this.status == AVAILABLE)
		{
			this.status = BOOKED;
			return true;
		}
		return false;
	}
	
	/**
	 * Shift the asset's status to unavailable.
	 */
	public void shiftStatusToUnavailable()
	{
		this.status = UNAVAILABLE;
	}
	
	/**
	 * Apply damage on asset after customer stay and changes it's status from occupied to available.
	 *
	 * @param appliedDamage the applied damage
	 */
	public synchronized void applyDamageOnAssetAfterCustomerStay(double appliedDamage)
	{
		for (AssetContent assetContent : this.assetContents)
		{
			assetContent.applyDamage(appliedDamage);
		}
		
		this.health-=appliedDamage;
		if (this.health<0){
			this.health=0;
		}
		
		this.status = AVAILABLE;
		notifyAll();
	}

	/**
	 * Check if the asset is damaged.
	 * if all asset contents health is below 65% it is damaged.
	 *
	 * @return true, if damaged
	 */
	public boolean checkIfDamaged(){
		return (this.health<65);
	}
	
	/**
	 * verifies the asset is not booked or occupied by customers
	 * if so, waits for the asset to be available again; 
	 *
	 * @return true, if successful
	 */
	public synchronized boolean checkNotOccupied()
	{
		try {
			while (this.status == OCCUPIED || this.status == BOOKED)
			{
				wait();
			}
		}
		 catch (InterruptedException e) { e.printStackTrace(); return false;}
		//at this point the asset is surely available with no customer group staying in it
		return true;
	}
	
	/**
	 * Gets the names of the asset contents
	 *
	 * @return the asset contents names
	 */
	public Vector<String> getAssetContentsNames()
	{
		Vector<String> result = new Vector<String>();
		for (AssetContent assetContent : this.assetContents)
		{
			String name = assetContent.getName();
			result.add(name);
		}
		
		return result;
	}
	
	
	/**
	 * Gets the asset's type.
	 *
	 * @return the type
	 */
	 public String getType()
		{
			return this.type; 
		}
	 
	 /**
 	 * Calculate repair time for all asset contents.
 	 *
 	 * @return the double
 	 */
 	public double calculateRepairTimeForAllAssetContents()
	 {
		 double result = 0;
		 for (AssetContent assetContent : this.assetContents)
		 {
			 result += assetContent.calculateRepairTime();
		 }
		 return result;
	 }
 	
 	/**
	  * Restore health value of the asset's asset contents after repair.
	  */
	 public void restoreHealthAfterRepair()
 	{
		 for (AssetContent assetContent : this.assetContents)
		 {
			 assetContent.restoreHealthAfterRepair();
		 }
		 this.status = AVAILABLE;
 	}

	/**
	 * Gets the cost per night.
	 *
	 * @return the cost per night
	 */
	public int getCostPerNight() {
		return this.costPerNight;
	}
	
}
