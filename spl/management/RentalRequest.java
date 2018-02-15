package spl.management;
import java.util.logging.Logger;

import spl.assets.Asset;

/**
 * The Class RentalRequest.
 * 
 * "This object will hold the information of a rental request."
 */
public class RentalRequest {
	
	/** The rental request's id. */
	private int id;
	
	/** The requested asset type. */
	private String assetType;
	
	/** The required Asset size. */
	private int AssetSize;
	
	/** The requested stay duration. */
	private int stayDuration;
	
	/** The asset the handling clerk booked for this rental request. */
	private Asset rentedAsset;
	
	/** The rental request's processing status. */
	private String requestStatus;
	
	/** The group's manager name. */
	private String groupManagerName;
	
	/** The logger. */
	private Logger LOGGER;
	
	/** a parameter indicating if this rental request is a fake one.
	 * 	one use for fake rental requests is to indicate the clerks that there will be no more rental request for this simulation. */
	private boolean isFake;
	
	
	/** The Constant INCOMPLETE (for requestStatus field). 
	 * 	indicating no clerk started treating it*/
	public static final String INCOMPLETE = "Incomplete";
	
	/** The Constant UNDERTREATMENT (for requestStatus field). 
	 * 	indicating a clerk is dealing with it*/
	public static final String UNDERTREATMENT = "UnderTreatment";
	
	/** The Constant FULFILLED (for requestStatus field).
	 * 	indicating the clerk found an asset which meets the request's parameters */
	public static final String FULFILLED = "Fulfilled";
	
	/** The Constant INPROGRESS (for requestStatus field).
	 * 	indicating the requesting group is currently staying in the supplied asset */
	public static final String INPROGRESS = "InProgress";
	
	/** The Constant COMPLETE (for requestStatus field). 
	 * 	indicating this request has finished it's life cycle*/
	public static final String COMPLETE = "Complete";
	
	/**
	 * Instantiates a new rental request.
	 *
	 * @param id the request's id
	 * @param assetType the requested asset type
	 * @param assetSize the requested asset size
	 * @param stayDuration the stay duration
	 * @param groupManagerName the group's manager name
	 * @param LOGGER the logger
	 */
	public RentalRequest(int id, String assetType, int assetSize, int stayDuration, String groupManagerName, Logger LOGGER)
	{
		this.id = id;
		this.assetType = assetType;
		this.AssetSize = assetSize;
		this.stayDuration = stayDuration;
		this.rentedAsset = null;
		this.requestStatus = INCOMPLETE;
		this.groupManagerName = groupManagerName;
		this.isFake = false;
		this.LOGGER = LOGGER;
		LOGGER.finer("rental request created: " + this.id + " " + this.assetType + " " + this.AssetSize + " " + this.stayDuration);
	}
	
	
	/**
	 * Instantiates a new fake rental request.
	 * one use for fake rental requests is to indicate the clerks that there will be no more rental request for this simulation.
	 *
	 * @param isFakeRentalRequest the is fake rental request
	 * @param LOGGER the logger
	 */
	public RentalRequest(boolean isFakeRentalRequest, Logger LOGGER)
	{
		this.LOGGER = LOGGER;
		if (!isFakeRentalRequest)
			this.LOGGER.warning(Thread.currentThread().getName() + "should have sent true as an argument for fake rental request construction");
		this.isFake = isFakeRentalRequest;
		
			
	}
	
	/**
	 * Gets the rental request's id.
	 *
	 * @return the id
	 */
	public int getId()
	{
		return this.id;
	}


	/**
	 * Gets the stay duration.
	 *
	 * @return the stay duration
	 */
	public int getStayDuration()
	{
		return this.stayDuration;
	}


	/**
	 * Gets the requested size.
	 *
	 * @return the requested size
	 */
	public int getRequestedSize()
	{
		return this.AssetSize;
	}


	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType()
	{
		return this.assetType;
	}


	/**
	 * Gets the group manager name which created the request.
	 *
	 * @return the group manager name
	 */
	public String getGroupManagerName()
	{
		return this.groupManagerName;
	}


	/**
	 * Checks if this rental request is a fake one.
	 *
	 * @return true, if it is fake
	 */
	public boolean checkFake()
	{
		return this.isFake;
	}


	/**
	 * Submit asset for rental request. used by a clerk to submit a fit asset which will be rented for the requesting group.
	 *
	 * @param rentedAsset the rented asset
	 * @return true, if successful
	 */
	public synchronized boolean submitAssetForRentalRequest(Asset rentedAsset)
	{
		if (this.rentedAsset == null)
		{
			this.LOGGER.fine(this.id + " was fulfilled");
			this.requestStatus = FULFILLED;
			this.rentedAsset = rentedAsset;
			notifyAll(); //check if necessary
			return true;
		}
		return false;
	}
	
	/**
	 * Apply customer damage after their stay on the asset.
	 *
	 * @param damageAmount the damage amount
	 * @return the damage report
	 */
	public DamageReport applyCustomerDamageOnAsset(double damageAmount)
	{
		this.rentedAsset.applyDamageOnAssetAfterCustomerStay(damageAmount);
		DamageReport returnedDamageReport = new DamageReport(this.rentedAsset, damageAmount, this.LOGGER);
		return returnedDamageReport;
	}
	
	/**
	 * Checks if the rental request's status is fulfilled.
	 *
	 * @return true, if it's status equals fulfilled
	 */
	public synchronized boolean checkRentalRequestFulfilled()
	{
		if (this.requestStatus == INCOMPLETE)
		{
			try {
				this.LOGGER.fine("waiting for " + this.id + " to be fulfilled");
				wait();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return (this.requestStatus == FULFILLED);
	}

	/**
	 * Check incomplete.
	 *
	 * @return true, if successful
	 */
	public boolean checkIncomplete()
	{
		return (this.requestStatus.equals(INCOMPLETE));
	}
	
	/**
	 * Shift the request's status to under treatment.
	 */
	public void shiftStatusToUnderTreatment()
	{
		this.requestStatus = UNDERTREATMENT;
	}


	/**
	 *  Checks if the rental request's status is complete.
	 *
	 * @return true, if it's status equals to complete
	 */
	public boolean checkCompleated() {
		return (this.requestStatus.equals(COMPLETE));
	}

	/**
	 * Calculate the total cost of the stay
	 *
	 * @param sizeOfGroup the size of group
	 * @return the cost
	 */
	public int calculatePriceOfStay(int sizeOfGroup){
		return sizeOfGroup*this.stayDuration*(rentedAsset.getCostPerNight());
	}
	
	/**
	 * Gets the requested size size.
	 *
	 * @return the size
	 */
	private int getRentedAssetSize() {
		return this.rentedAsset.getSize();
	}


	/**
	 * Gets the rental request status.
	 *
	 * @return the status
	 */
	private String getStatus() {
		return this.requestStatus;
	}


	/**
	 * Gets the rented asset name.
	 *
	 * @return the rented asset name
	 */
	private String getRentedAssetName() {
		return this.rentedAsset.getName();
	}


	/**
	 *  
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		
		
		return "rental request " + this.getId()+" requested asset type "+this.getType()+" with size of "+this.getRentedAssetSize()+
				".  the stay duration is "+this.getStayDuration()+" in asset "+this.getRentedAssetName()+
				".   the status of the request is "+this.getStatus();
		
	}
	
}
