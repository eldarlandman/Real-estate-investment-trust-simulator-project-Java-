package spl.management;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;


/**
 * The Class Statistics.
 */
public class Statistics {

	/** The gained money. */
	private int gainedMoney;

	/** The used repair tools. */
	public HashMap<String, RepairToolInformation> usedRepairTools;

	/** The used repair materials. */
	public HashMap<String, RepairMaterialInformation> usedRepairMaterials;

	/** The used rental requests. */
	public Vector<RentalRequest> usedRentalRequests;

	/** The builder. */
	private StringBuilder builder;

	
	/**
	 * Instantiates a new statistics.
	 *
	 * @param LOGGER the logger
	 */
	public Statistics(Logger LOGGER)
	{
		this.gainedMoney = 0;
		this.usedRepairMaterials = new HashMap<String, RepairMaterialInformation>();
		this.usedRepairTools = new HashMap<String, RepairToolInformation>();
		this.usedRentalRequests = new Vector<RentalRequest>();
		this.builder=new StringBuilder();
		LOGGER.finest("Statistics created");
	}

	/**
	 *  used for outputting the final report.
	 *
	 * @param rentalRequest the rental request
	 * @see java.lang.Object#toString()
	 */
	public synchronized void addToUsedRentalRequests(RentalRequest rentalRequest){
		this.usedRentalRequests.add(rentalRequest);
	}

	/**
	 * Adds the used materials to the accumulating collection.
	 *
	 * @param addedMaterialVector the added material vector
	 */
	public synchronized void addUsedMaterials(Vector<RepairMaterialInformation> addedMaterialVector) {
		for (RepairMaterialInformation addedMaterial: addedMaterialVector){
			RepairMaterialInformation materialInformationInMap = this.usedRepairMaterials.get(addedMaterial.getRepairMaterialName());
			if (materialInformationInMap != null)
			{
				materialInformationInMap.increaseQuantity(addedMaterial.getMaterialQuantity());
			}
			else
			{
				this.usedRepairMaterials.put(addedMaterial.getRepairMaterialName(), addedMaterial);
			}

		}
		
	}

	/**
	 * Adds the used tools to the accumulating collection.
	 *
	 * @param addedToolsVector the added tools vector
	 */
	public void addUsedTools(Vector<RepairToolInformation> addedToolsVector) {
		
		for (RepairToolInformation addedTool : addedToolsVector)
		{
			if (this.usedRepairTools.get(addedTool.getRepairToolName()) == null)
			{
				this.usedRepairTools.put(addedTool.getRepairToolName(), addedTool);
			}
		}
		
		
	}

	/**
	 * Update money gained.
	 *
	 * @param MoneyGainedForSpecificRequest the money gained for specific request
	 */
	public synchronized void updateMoneyGained(int MoneyGainedForSpecificRequest) {
	
		this.gainedMoney=+MoneyGainedForSpecificRequest;
	
	}

	/**
	 * adds the repair tool's information to the string data.
	 */
	private void usedRepairToolToString(){
	
		this.builder.append("list of Used Repair Tool:" + System.lineSeparator());
		Collection<RepairToolInformation> usedToolsCollecion = this.usedRepairTools.values();
		for (RepairToolInformation currTool: usedToolsCollecion){
			this.builder.append(currTool.toString());
			this.builder.append(System.lineSeparator());
		}
	
	}

	/**
	 * adds the repair material's information to the string data.
	 */
	private void usedRepairMaterialToString(){
	
		this.builder.append("list of Used Repair Material:" + System.lineSeparator());
		Collection<RepairMaterialInformation> usedMaterialsCollecion = this.usedRepairMaterials.values();
		for (RepairMaterialInformation currMaterial: usedMaterialsCollecion){
			this.builder.append(currMaterial.toString());
			this.builder.append(System.lineSeparator());
		}
	
	}

	/**
	 * adds the rental request's information to the string data.
	 */
	private void usedRentalRequestsToString(){
	
		this.builder.append("list of Rental Requests:").append(System.lineSeparator());
		for (RentalRequest currRequest: this.usedRentalRequests){
			this.builder.append(currRequest.toString());
			this.builder.append(System.lineSeparator());
		}
	
	}

	/**
	 * adds the gained money to the string data
	 */
	private void MoneyGainedToString(){
		builder.append("the Money was Gained is: "+ this.gainedMoney + System.lineSeparator());
	}

	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
	
		MoneyGainedToString();
		usedRentalRequestsToString();
		usedRepairToolToString();
		usedRepairMaterialToString();
		
		return builder.toString();
	}




}
