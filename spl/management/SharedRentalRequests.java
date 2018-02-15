package spl.management;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


/**
 * The Class SharedRentalRequests.
 */
public class SharedRentalRequests {
	
	/** The rental requests pool. */
	private LinkedBlockingQueue<RentalRequest> rentalRequests;
	
	/** a counter of the total number of requests in the simulation */
	private AtomicInteger totalRequestsInQueue;
	
	/** indicating if the next rental request is fake. */
	private AtomicBoolean nextRentalRequestIsFake;
	
	/** The logger. */
	private Logger LOGGER;
	
	/**
	 * Instantiates a new shared rental requests.
	 *
	 * @param totalRentalRequestsAmount the total rental requests amount
	 * @param LOGGER the logger
	 */
	public SharedRentalRequests(AtomicInteger totalRentalRequestsAmount, Logger LOGGER)
	{
		this.rentalRequests = new LinkedBlockingQueue<RentalRequest>();
		this.totalRequestsInQueue = totalRentalRequestsAmount;
		this.nextRentalRequestIsFake = new AtomicBoolean(false);
		this.LOGGER = LOGGER;
	}
	
	/**
	 * used by the customer groups to push their next rental request
	 *
	 * @param addedRentalRequest the added rental request
	 */
	public void addRentalRequest(RentalRequest addedRentalRequest)
	{
		this.totalRequestsInQueue.decrementAndGet();
		this.rentalRequests.add(addedRentalRequest);//insert the real request from the the static queue to shared rental requests
		if (this.checkReceivedAllSimulationRequests())//once a customer sent the last request of the simulation
		{
		 //an additional fake rental request is created to notify the other clerks that there will be no more
			RentalRequest newFakeRentalRequest = new RentalRequest(true, this.LOGGER);
			this.rentalRequests.add(newFakeRentalRequest);
		}
	}
		
	/**
	 * Gets the next rental request.
	 *
	 * @return the next rental request
	 */
	public RentalRequest getNextRentalRequest()
	{
			RentalRequest result = null;
			try {
				this.LOGGER.finest(Thread.currentThread().getName() + " is waiting for his request");
	
				result = this.rentalRequests.take();
				if (result.checkFake())
				{
					this.rentalRequests.add(result);
					this.nextRentalRequestIsFake.set(true);
					return null;
				}

			} catch (InterruptedException e) {
				this.LOGGER.warning("exception was thrown by take action");
				e.printStackTrace();
			}
			
			return result;
		}
	
	/**
	 * Checks if requests queue is empty.
	 *
	 * @return true, if is empty
	 */
	public synchronized boolean isEmpty()
	{
		return this.rentalRequests.isEmpty();
	}
	
	/**
	 * Check if all of the requests were received.
	 *
	 * @return true, if successful
	 */
	public synchronized boolean checkReceivedAllSimulationRequests()
	{
		return (this.totalRequestsInQueue.get() <= 0);
	}
	
	/**
	 * Check if the next rental request fake which indicating that all of the rental requests were received.
	 *
	 * @return true, if successful
	 */
	public synchronized boolean checkNextRentalRequestFake(){
		return this.nextRentalRequestIsFake.get();
	}
	
	
	

}
