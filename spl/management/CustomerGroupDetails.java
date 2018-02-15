package spl.management;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import spl.runnables.CallableSimulateStayInAsset;

/**
 * The Class CustomerGroupDetails. 
 * this class represents an organized group of customers.
 */
public class CustomerGroupDetails {

	/** The group's rental requests. */
	private LinkedBlockingQueue<RentalRequest> rentalRequests;
	
	/** a collection of the group's customers. */
	private Vector<Customer> customers;
	
	/** The group's manager name. */
	private String managerName;
	
	/** The logger. */
	private Logger LOGGER;

	/**
	 * Instantiates a new customer group details.
	 *
	 * @param managerName the group's manager name
	 * @param LOGGER the logger
	 */
	public CustomerGroupDetails(String managerName, Logger LOGGER)
	{
		this.managerName = managerName;
		this.rentalRequests = new LinkedBlockingQueue<>();
		this.customers = new Vector<Customer>();
		this.LOGGER=LOGGER;
		LOGGER.finer("CustomerGroupDetail created: " + this.managerName);

	}



	/**
	 * Adds the customer.
 	 * Created for populating the group with customers while reading the input information files
	 *
	 * @param addedCustomer the added customer
	 */
	public void addCustomer(Customer addedCustomer)
	{
		this.customers.addElement(addedCustomer);
	}

	/**
	 * Adds the rental request.
	 * Created for populating the group with rental requests while reading the input information files
	 *
	 * @param addedRentalRequest the added rental request
	 */
	public void addRentalRequest(RentalRequest addedRentalRequest)
	{

		this.rentalRequests.add(addedRentalRequest);
	}


	/**
	 * Gets the group's manager name.
	 *
	 * @return the group manager name
	 */
	public String getGroupManagerName()
	{
		return this.managerName;
	}



	/**
	 * Gets the size of the group.
	 *
	 * @return the size of group
	 */
	public int getSizeOfGroup() {
		return this.customers.size();
	}



	/**
	 * Gets the group's next pending rental request.
	 *
	 * @return the next rental request
	 */
	public RentalRequest getNextRentalRequest() {
		RentalRequest nextRentalRequest = this.rentalRequests.peek();
		if (nextRentalRequest.checkIncomplete()){
			return rentalRequests.remove();
		}
		else{
			LOGGER.warning(this.managerName+" was asked to return next rental request but wasnt fullfilled");
			return null;
		}

	}

	/**
	 * Simulate the group's stay in an asset when their next rental request is fulfilled.
	 *
	 * @param rentalRequest the rental request
	 * @return the damage report
	 */
	public DamageReport simulateStayInAsset(RentalRequest rentalRequest)
	{
		
		ExecutorService staySimulationExecutor=Executors.newFixedThreadPool(this.customers.size());
		List<Future<Double>> damage=new ArrayList<Future<Double>>();
		for (Customer customer : this.customers)
		{
			CallableSimulateStayInAsset newStaySimulation = new CallableSimulateStayInAsset(customer, rentalRequest, this.LOGGER);
			damage.add(staySimulationExecutor.submit(newStaySimulation));
		}
		staySimulationExecutor.shutdown();
		this.LOGGER.finest(this.managerName + " finished creating CallableSimulateStayInAsset for each customer for sleeping " + rentalRequest.getStayDuration() + " seconds");
		double sumDamage=new Double(0);
		for (int i=0; i<this.customers.size(); i++)
		{
			Future<Double> future = damage.get(i);
			try {
				this.LOGGER.finest(this.managerName + " waiting for customer to finish stay simulation");
				Double callableResult = future.get();
				sumDamage += callableResult.doubleValue();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}	
		}
		DamageReport returnedDamageReport = rentalRequest.applyCustomerDamageOnAsset(sumDamage);
		return returnedDamageReport;

	}

	/**
	 * Removes the current request if it was completed(after the group's stay was simulated).
	 */
	public void removeCurrentRequest(){

		if (getNextRentalRequest().checkCompleated()){
			rentalRequests.remove();
		}
	}

}
