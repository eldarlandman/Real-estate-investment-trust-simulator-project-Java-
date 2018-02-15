package spl.runnables;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import spl.assets.Asset;
import spl.assets.Assets;
import spl.management.ClerkDetails;
import spl.management.RentalRequest;
import spl.management.SharedRentalRequests;
import spl.management.Statistics;


/**
 * The Class RunnableClerk.
 * simulates a clerk's single work day(8 work hours)
 */
public class RunnableClerk implements Runnable {
	
	/** The clerk this thread represents. */
	private ClerkDetails innerClerk;
	
	/** The global shared resource for rental requests. */
	private SharedRentalRequests rentalRequests;
	
	/** The total pending requests count down latch. */
	private CountDownLatch totalPendingRequestsCountDownLatch;
	
	/** The assets owned by the management. */
	private Assets dealtAssets;
	
	/** The work hours accumulated so far for the day. */
	private int workHours;
	
	/** The logger. */
	private Logger LOGGER;
	
	/** The total active clerks count down latch. */
	private CountDownLatch totalActiveClerksCountDownLatch;
	
	/** The simulation statistics. */
	private Statistics simulationStatistics;

	/**
	 * Instantiates a new runnable clerk.
	 *
	 * @param innerClerk the inner clerk
	 * @param dealtAssets the dealt assets
	 * @param rentalRequests the rental requests
	 * @param totalActiveClerksCountDownLatch the total active clerks count down latch
	 * @param totalPendingRequestsCountDownLatch the total pending requests count down latch
	 * @param LOGGER the logger
	 */
	public RunnableClerk(ClerkDetails innerClerk, 
						 Assets dealtAssets, 
						 SharedRentalRequests rentalRequests, 
						 CountDownLatch totalActiveClerksCountDownLatch, 
						 CountDownLatch totalPendingRequestsCountDownLatch,
						 Statistics simulationStatistics,
						 Logger LOGGER)
	{
		this.innerClerk = innerClerk;
		this.rentalRequests = rentalRequests;
		this.dealtAssets = dealtAssets;
		this.workHours = 0;
		this.totalPendingRequestsCountDownLatch = totalPendingRequestsCountDownLatch;
		this.LOGGER = LOGGER;
		this.LOGGER.finer(this.innerClerk.getName() + " new RunnableClerk thread created");
		this.totalActiveClerksCountDownLatch = totalActiveClerksCountDownLatch;
		this.simulationStatistics=simulationStatistics;
	}
	
	/** the clerk's run method
	 * @see java.lang.Runnable#run()
	 * simulates a clerk's single work day (8 work hours)
	 */
	@Override
	public void run() {
		LOGGER.info(this.innerClerk.getName() + " started his shift");
		
		while (workHours<=8 && 
				this.totalPendingRequestsCountDownLatch.getCount() > 0){
			
			
			RentalRequest currentlyTreatedRequest = this.rentalRequests.getNextRentalRequest();
			//will return null if all of the simulation's requests were fulfilled
			if (currentlyTreatedRequest != null)
			{
				LOGGER.fine(this.innerClerk.getName() + " dealing with request " + currentlyTreatedRequest.getId());
				//looking for appropriate asset
				Asset foundAsset = this.dealtAssets.findFitAssets(currentlyTreatedRequest.getType(), currentlyTreatedRequest.getRequestedSize());
				while (foundAsset == null)
				{
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						LOGGER.warning(Thread.currentThread().getName() + " failed to sleep when waiting for appropriate assets to be available");
						e.printStackTrace();
					}
					
					foundAsset = this.dealtAssets.findFitAssets(currentlyTreatedRequest.getType(), currentlyTreatedRequest.getRequestedSize());
				}
				
				this.LOGGER.info(this.innerClerk.getName() + " move to asset " + foundAsset.getName() + " for request: " + currentlyTreatedRequest.getId());
				this.workHours += this.innerClerk.moveToLocation(foundAsset.getLocation());
				this.LOGGER.fine(innerClerk.getName()+" left "+(8-workHours)+" hours for his shift");
				
				if (!currentlyTreatedRequest.submitAssetForRentalRequest(foundAsset))
					LOGGER.warning("failed to submit asset");
				
				simulationStatistics.addToUsedRentalRequests(currentlyTreatedRequest);
				
			}
			else 
			{//this.rentalRequests.getNextRentalRequest() returned null, while loop should not continue 
				break;
			}
			
		}
		LOGGER.info(this.innerClerk.getName() + " finished his shift");
		this.totalActiveClerksCountDownLatch.countDown();//the clerk finished his day
	}

}
