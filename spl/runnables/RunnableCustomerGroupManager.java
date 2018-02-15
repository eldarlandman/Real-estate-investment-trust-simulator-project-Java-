package spl.runnables;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import spl.management.CustomerGroupDetails;
import spl.management.DamageReport;
import spl.management.Management;
import spl.management.RentalRequest;
import spl.management.Statistics;


/**
 * The Class RunnableCustomerGroupManager.
 */
public class RunnableCustomerGroupManager implements Runnable {
	
	/** The customer group this thread represents. */
	private CustomerGroupDetails innerCustomerGroup;
	
	/** The management. */
	private Management management;
	
	/** The total pending requests count down latch. */
	private CountDownLatch totalPendingRequestsCountDownLatch;
	
	/** The simulation statistics. */
	private Statistics simulationStatistics;
	
	/** The logger. */
	private Logger LOGGER;
	
	/**
	 * Instantiates a new runnable customer group manager.
	 *
	 * @param innerCustomerGroup the customer group
	 * @param management the management
	 * @param totalPendingRequestsCountDownLatch the total pending requests count down latch
	 * @param LOGGER the logger
	 */
	public RunnableCustomerGroupManager(CustomerGroupDetails innerCustomerGroup, Management management, CountDownLatch totalPendingRequestsCountDownLatch, Statistics simulationStatistics ,Logger LOGGER)
	{
		this.innerCustomerGroup = innerCustomerGroup;
		this.management=management;
		this.totalPendingRequestsCountDownLatch = totalPendingRequestsCountDownLatch;
		this.LOGGER = LOGGER;
		this.simulationStatistics=simulationStatistics;
		LOGGER.finer("new RunnableCustomerGroupManager created");
	}

	/** the thread's run method.
	 * @see java.lang.Runnable#run()
	 * 
	 * simulates a single group's life cycle: submitting requests for the management and simulating it's stay. 
	 */
	@Override
	public void run() {
		RentalRequest currReq=innerCustomerGroup.getNextRentalRequest();
		while (currReq!=null)
		{
			management.submitRentalRequest(currReq);		
			if (currReq.checkRentalRequestFulfilled()){//sleep until request fulfilled!!!
				this.LOGGER.fine("starting simulation stayInAsset in asset for request "+ currReq.getId());
				DamageReport damageReport = innerCustomerGroup.simulateStayInAsset(currReq);
				this.management.submitDamageReport(damageReport);
				
				this.LOGGER.fine("finished stayInAsset simulation, the damage after fulfilling request "+currReq.getId()+" is: " + damageReport.getDamagePercentage());
				
			}
			else{
				LOGGER.warning(Thread.currentThread().getName() + " waited for his request to be fulfilled but when he woke up it's status wasnt = fulfilled");
			}
			
			this.simulationStatistics.updateMoneyGained(currReq.calculatePriceOfStay(innerCustomerGroup.getSizeOfGroup()));
			this.totalPendingRequestsCountDownLatch.countDown();
			currReq=innerCustomerGroup.getNextRentalRequest();	
		}
		
	}

	

}
