package spl.runnables;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import spl.management.Customer;
import spl.management.RentalRequest;


/**
 * The Class CallableSimulateStayInAsset.
 * Used for simulating a single customer stay on a specific asset provided to meet a rental request .
 * 
 * "This callable will simulate the presence of one customer in an asset. After the simulation is complete, a damage percentage to the asset is calculated and returned."
 */
public class CallableSimulateStayInAsset implements Callable<Double> {
	
	/** The inner customer. */
	private Customer innerCustomer;
	
	/** The rental details. */
	private RentalRequest rentalDetails;
	
	/**
	 * Instantiates a new callable stay simulation in asset.
	 *
	 * @param innerCustomer the customer which his stay is simulated 
	 * @param rentalDetails the rental details
	 * @param LOGGER the logger
	 */
	public CallableSimulateStayInAsset(Customer innerCustomer, RentalRequest rentalDetails, Logger LOGGER)
	{
		this.innerCustomer = innerCustomer;
		this.rentalDetails = rentalDetails;
		LOGGER.finest("new CallableSimulateStayInAsset: " + this.innerCustomer.getName() + " " + this.rentalDetails.getId());
	}

	/** 
	 * the stay simulation process(Thread's call method)
	 * @see java.util.concurrent.Callable#call()
	 * 
	 * "Calculating the damage percentage is done once per asset, per customer." ... "Sleeping is done by converting each 24 hours, to 24 seconds."
	 */
	@Override
	public Double call() throws Exception {
		Thread.sleep(1000 * this.rentalDetails.getStayDuration());
		Double damage = new Double(this.innerCustomer.calculateDamage());
		return damage;
	}

}
