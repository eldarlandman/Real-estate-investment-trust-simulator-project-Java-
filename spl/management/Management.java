package spl.management;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import spl.assets.Asset;
import spl.assets.Assets;
import spl.runnables.RunnableClerk;
import spl.runnables.RunnableCustomerGroupManager;
import spl.runnables.RunnableMaintenanceRequest;
import spl.warehouse.RepairMaterial;
import spl.warehouse.RepairTool;
import spl.warehouse.Warehouse;


/**
 * The Class Management.
 */
public class Management {

	/** The management's active clerks. */
	private Vector<ClerkDetails> activeClerks;

	/** The management's customer groups. */
	private Vector<CustomerGroupDetails> customerGroups;

	/** The rental requests that the customer groups sent and waiting for handling. */
	private SharedRentalRequests rentalRequests;

	/** a list of assets which were damaged after customers stay. */
	private LinkedBlockingQueue<Asset> damagedAssets;

	/** The assets owned by the management. */
	private Assets ownedAssets;

	/** The management's warehouse used for storing repair tools and materials. */
	private Warehouse warehouse;

	/** The repair tools required for repairing a specific asset content type. created as thread-safe map. */
	private HashMap<String,Vector<RepairToolInformation>> repairToolsInformation;

	/** The repair materials required for repairing a specific asset content type. created as thread-safe map. */
	private HashMap<String,Vector<RepairMaterialInformation>> repairMaterialsInformation;

	/** The which will be printed when the simulation ends. */
	private Statistics simulationStatistics;

	/** The total requests amount in the simulation. */
	private int totalRequestsAmount;

	/** The number of maintenance personals. */
	private int numberOfMaintenancePersons; 

	/** The logger. */
	private Logger LOGGER;


	/**
	 * Instantiates a new management.
	 *
	 * @param logger the logger
	 */
	public Management(Logger logger){
		this.LOGGER = logger;
		this.activeClerks=new Vector<ClerkDetails>();
		this.customerGroups=new Vector<CustomerGroupDetails>();
		this.ownedAssets=new Assets(this.LOGGER);
		this.warehouse=new Warehouse();
		this.repairToolsInformation = new HashMap<String,Vector<RepairToolInformation>>();
		this.repairMaterialsInformation = new HashMap<String,Vector<RepairMaterialInformation>>();
		this.damagedAssets=new LinkedBlockingQueue<Asset>();
		this.simulationStatistics=new Statistics(this.LOGGER);
	}

	/**
	 * Gets the assets.
	 *
	 * @return the assets
	 */
	public Assets getAssets(){
		return this.ownedAssets;
	}

	/**
	 * Sets the rental requests amount.
	 *
	 * @param totalNumberOfRentalRequests the new rental requests amount value
	 */
	public void setRentalRequestsAmount(int totalNumberOfRentalRequests) {
		this.totalRequestsAmount = totalNumberOfRentalRequests;
	
	}

	/**
	 * Sets the number of maintenance personals.
	 *
	 * @param NumberOfMaintenancePersons the new number of maintenance persons
	 */
	public void setNumberOfMaintenancePersons(int NumberOfMaintenancePersons) {
		this.numberOfMaintenancePersons=NumberOfMaintenancePersons;	
	}

	/**
	 * Adds a new clerk for the management
	 * Created for populating the management with clerks while reading the input information files.
	 *
	 * @param addedClerk the added clerk
	 */
	public void addClerk(ClerkDetails addedClerk)
	{
		this.activeClerks.addElement(addedClerk);
	}

	/**
	 * Adds a new customer group for the management.
	 * Created for populating the management with customer groups while reading the input information files.
	 *
	 * @param addedCustomerGroup the added customer group
	 */
	public void addCustomerGroup(CustomerGroupDetails addedCustomerGroup)
	{
		this.customerGroups.addElement(addedCustomerGroup);
	}

	/**
	 * Adds an asset for the management.
	 * Created for populating the management with assets while reading the input information files.
	 *
	 * @param newAsset the new asset
	 */
	public void addAsset(Asset newAsset) {
		this.ownedAssets.addAsset(newAsset);
	}

	/**
	 * Adds item repair tools required for repairing a specific asset content type.
	 * Created for populating the management with tools information while reading the input information files.
	 *
	 * @param nameOfContent the name of the asset content
	 * @param v the repair tools required for the fix
	 */
	public void addItemRepairToolInformation(String nameOfContent ,Vector<RepairToolInformation> v)
	{
		repairToolsInformation.put(nameOfContent, v);
	}

	/**
	 * Adds the item repair materials required for repairing a specific asset content type.
	 * Created for populating the management with material information while reading the input information files.
	 *
	 * @param nameOfContent the name of the asset content 
	 * @param v the repair tools required for the fix
	 */
	public void addItemRepairMaterialInformation(String nameOfContent ,Vector<RepairMaterialInformation> v)
	{
		repairMaterialsInformation.put(nameOfContent,v);
	}

	/**
	 * Adds the item repair tool to warehouse.
	 *
	 * @param newRepairTool the new repair tool
	 */
	public void addItemRepairToolToWarehouse(RepairTool newRepairTool) {
		this.warehouse.addTool(newRepairTool);
	
	}

	/**
	 * Adds the item repair material to warehouse.
	 *
	 * @param newRepairMaterial the new repair material
	 */
	public void addItemRepairMaterialToWarehouse(RepairMaterial newRepairMaterial) {
		this.warehouse.addMaterial(newRepairMaterial);
	
	}

	/**
	 * Submit rental request.
	 *
	 * @param currReq the current request
	 */
	public void submitRentalRequest(RentalRequest currReq) {//insert into thread safe data base
		rentalRequests.addRentalRequest(currReq);	
	}

	/**
	 * Submit damage report to the management.
	 *
	 * @param report the report
	 */
	public void submitDamageReport(DamageReport report){//groupManager use this method

		if (report.checkAssetContentsHealthBelow65())
		{
			this.damagedAssets.add(report.getReportedAsset());
		}

	}

	/**
	 * Prints the statistics.
	 */
	public void printStatistics() {

		System.out.println(simulationStatistics.toString());
		
	}

	/**
	 * removes duplicate assets from the damaged assets list
	 */
	private void sortDamagedAsset() {
	
		Set<Asset> damagedAssetsNoDuplicates=new LinkedHashSet<>(this.damagedAssets);
		this.damagedAssets.clear();		
		this.damagedAssets.addAll(damagedAssetsNoDuplicates);
	}

	/**
	 * the simulation
	 */
	public void simulate()
	{
		CountDownLatch activeClerksCountDownLatch = new CountDownLatch(this.activeClerks.size());
		CountDownLatch pendingRequestsCountDownLatch = new CountDownLatch(this.totalRequestsAmount);
		this.rentalRequests = new SharedRentalRequests(new AtomicInteger(this.totalRequestsAmount), this.LOGGER);
	
		/****************************************step 1 : Customer Group Manager*************************************/
		
		ExecutorService runnableGroupsExecutor = Executors.newFixedThreadPool(this.customerGroups.size());
		for (int i = 0; i < this.customerGroups.size(); i++)
		{//creates runnable groups
			runnableGroupsExecutor.submit(new RunnableCustomerGroupManager(this.customerGroups.elementAt(i), this, pendingRequestsCountDownLatch,this.simulationStatistics ,this.LOGGER));
		}
	
		runnableGroupsExecutor.shutdown();//no new customer will be added
	
		ExecutorService runnableClerksExecutor  = Executors.newFixedThreadPool(this.activeClerks.size());
	
		/****************************************step 2 : Clerks******************************************************/
		
		while(pendingRequestsCountDownLatch.getCount() > 0 &&
				!this.rentalRequests.checkNextRentalRequestFake()){
	
			this.LOGGER.info("it's a new day people");
	
			activeClerksCountDownLatch = new CountDownLatch(this.activeClerks.size());
			for (ClerkDetails clerk : this.activeClerks)
			{//creates runnable clerks
				runnableClerksExecutor.submit(new RunnableClerk(clerk, this.ownedAssets, this.rentalRequests, activeClerksCountDownLatch, pendingRequestsCountDownLatch,simulationStatistics, LOGGER));
			}
	
			try {
	
				activeClerksCountDownLatch.await();//wait till all clerks will finish their shift
	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
			/****************************************step 3 :  Maintenance**********************************************/
			
			this.ownedAssets.checkAllAssetsAvailable();
			//will hold the thread until all customers finished their stay and therefore are available and ready for repair
			
			LOGGER.info("management starting repairs");	
			sortDamagedAsset();
			int damagedAssetsAmount = this.damagedAssets.size();
			CountDownLatch remainingActiveMaintenanceThreadsCountDownLatch = new CountDownLatch(damagedAssetsAmount);
			ExecutorService runnableMaintenanceExecutor = Executors.newFixedThreadPool(this.numberOfMaintenancePersons);
			for (int i=0; i<damagedAssetsAmount; i++){
				runnableMaintenanceExecutor.submit(new RunnableMaintenanceRequest(this.repairToolsInformation, 
														this.repairMaterialsInformation, this.damagedAssets.poll(),
														this.warehouse,	remainingActiveMaintenanceThreadsCountDownLatch, 
														this.simulationStatistics ,LOGGER));
			}
			try {
				runnableMaintenanceExecutor.shutdown();
				remainingActiveMaintenanceThreadsCountDownLatch.await();
			} catch (InterruptedException e) {	e.printStackTrace(); }
	
		}
	
		try {
			pendingRequestsCountDownLatch.await(); //wait till all runnableStayInAsset will finish their staying
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		runnableClerksExecutor.shutdown();
		this.LOGGER.info("simulation over");
	}


}
