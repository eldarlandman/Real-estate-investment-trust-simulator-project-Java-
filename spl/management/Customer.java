package spl.management;
import java.util.Random;
import java.util.logging.Logger;


/**
 * The Class Customer, represents a single customer entity in the simulation.
 */
public class Customer {
	
	/** The customer's name. */
	private String name;
	
	/** The customer's vandalism type. */
	private String vandalismType;
	
	/** The customer's minimum damage which the customer will cause. */
	private int minimumDamage;
	
	/** The customer's maximum damage which the customer will cause. */
	private int maximumDamage;
	
	/**
	 * Instantiates a new customer.
	 *
	 * @param name the name
	 * @param vandalismType the vandalism type
	 * @param minDamage the minimum damage
	 * @param maxDamage the maximum damage
	 * @param LOGGER the logger
	 */
	public Customer(String name, String vandalismType, int minDamage, int maxDamage, Logger LOGGER)
	{
		this.name = name;
		this.vandalismType = vandalismType;
		this.minimumDamage = minDamage;
		this.maximumDamage = maxDamage;
		LOGGER.finest("new Customer " + this.name + " " + this.vandalismType + " " + this.minimumDamage + " " + this.maximumDamage);
	}
	
	/**
	 * Calculate damage the customer will cause in 24 hours stay.
	 *
	 * @return the double
	 */
	public double calculateDamage()
	{
		double calculatedDamege = 0;
		switch (this.vandalismType)
		{
			case "Arbitrary":	Random r = new Random();
								calculatedDamege = minimumDamage + (r.nextDouble() * (maximumDamage - minimumDamage));
								break;
								
			case "Fixed": 	calculatedDamege = (minimumDamage + maximumDamage) / 2;
							break;
			
			case "None":	calculatedDamege = 0.5;	
							break;
		}
		assert calculatedDamege != 0;
		return calculatedDamege;
	}
	
	/**
	 * Gets the customer's name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

}
