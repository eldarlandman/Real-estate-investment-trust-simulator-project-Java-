package spl.runnables;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import spl.assets.Asset;
import spl.management.RepairMaterialInformation;
import spl.management.RepairToolInformation;
import spl.management.Statistics;
import spl.warehouse.RepairMaterial;
import spl.warehouse.Warehouse;


/**
 * The Class RunnableMaintenanceRequest.
 * simulates a single maintenance worker
 */
public class RunnableMaintenanceRequest implements Runnable {

	/** The repair tools required for repairing a specific asset content type. */
	private HashMap<String,Vector<RepairToolInformation>> repairToolsInformation;

	/** The repair materials required for repairing a specific asset content type. */
	private HashMap<String,Vector<RepairMaterialInformation>> repairMaterialsInformation;

	/** The asset under repair. */
	private Asset assetUnderRepair;

	/** The warehouse. */
	private Warehouse warehouse;
	
	/** The globally alive maintenance. */
	private CountDownLatch globallyAliveMaintenance;
		
	/** The simulation statistics. */
	private Statistics simulationStatistics;
	
	/** The logger. */
	private Logger LOGGER;

	/**
	 * Instantiates a new runnable maintenance request.
	 *
	 * @param assetUnderRepair the asset under repair
	 * @param LOGGER the logger
	 */
	public RunnableMaintenanceRequest(HashMap<String,Vector<RepairToolInformation>> repairToolsInformation, 
									  HashMap<String,Vector<RepairMaterialInformation>> repairMaterialsInformation, 
									  Asset assetUnderRepair, Warehouse warehouse, 
									  CountDownLatch globallyAliveMaintenance, Statistics simulationStatistics ,Logger LOGGER)
	{
		this.repairMaterialsInformation = repairMaterialsInformation;
		this.repairToolsInformation = repairToolsInformation;
		this.assetUnderRepair = assetUnderRepair;
		this.warehouse = warehouse;
		this.globallyAliveMaintenance = globallyAliveMaintenance;
		this.LOGGER = LOGGER;
		this.simulationStatistics=simulationStatistics;
		LOGGER.info("new RunnableMaintenanceRequest thread created for fixing asset: " + assetUnderRepair.getName());
	}

	/**
	 * Combines repair tool information vectors.
	 *
	 * @param originalVector the original vector
	 * @param addedVector the added vector
	 */
	private void combineRepairToolInformationVectors(Vector<RepairToolInformation> originalVector, Vector<RepairToolInformation> addedVector)
	//edits the original vector
	{
		String addedToolName;
		int addedToolQuantity;
		boolean foundTool;
		for (RepairToolInformation tool : addedVector)
		{//adds the quantity of each tool to the original vector
			foundTool = false;
			addedToolName = tool.getRepairToolName();
			addedToolQuantity = tool.getRepairToolQuantity();
			for (int i = 0; i < originalVector.size() && !foundTool; i++)
			{
				if (addedToolName.equals(originalVector.elementAt(i).getRepairToolName()) )
				{//found the same tool type in the original vector and increases it's quantity
					foundTool = true;
					if (originalVector.elementAt(i).getRepairToolQuantity() < addedToolQuantity)
					{	
						tool.setRepairToolInformationQuantity( Math.max(tool.getRepairToolQuantity(), addedToolQuantity));
					}
				}
			}

			if (!foundTool)
			{//the added tool does not exists in the original vector
				originalVector.addElement(new RepairToolInformation(addedToolName, addedToolQuantity, this.LOGGER));
			}
		}
	}

	/**
	 * Gets the required repair tools list for fixing the damaged asset.
	 *
	 * @return the required repair tools list
	 */
	private Vector<RepairToolInformation> getRequiredRepairToolsList(){
		Vector<RepairToolInformation> result = new Vector<RepairToolInformation>();
		Vector<String> assetContentsNames = this.assetUnderRepair.getAssetContentsNames();
		for (String name : assetContentsNames)
		{
			Vector<RepairToolInformation> repairTools = this.repairToolsInformation.get(name);
			combineRepairToolInformationVectors(result, repairTools);
		}
		//at this point all of the required toolsesult

		return result;
	}


	/**
	 * Combine repair material information vectors.
	 *
	 * @param originalVector the original vector
	 * @param addedVector the added vector
	 */
	private void combineRepairMaterialInformationVectors(Vector<RepairMaterialInformation> originalVector, Vector<RepairMaterialInformation> addedVector)
	//edits the original vector
	{
		String addedMaterialName;
		int addedMaterialQuantity;
		boolean foundTool;
		for (RepairMaterialInformation material : addedVector)
		{//adds the quantity of each material to the original vector
			foundTool = false;
			addedMaterialName = material.getRepairMaterialName();
			addedMaterialQuantity = material.getMaterialQuantity();
			for (int i = 0; i < originalVector.size() && !foundTool; i++)
			{
				if (addedMaterialName == originalVector.elementAt(i).getRepairMaterialName())
				{//found the same material type in the original vector and increases it's quantity
					foundTool = true;
					material.increaseQuantity(addedMaterialQuantity);
				}
			}

			if (!foundTool)
			{//the added material does not exists in the original vector
				originalVector.addElement(new RepairMaterialInformation(addedMaterialName, addedMaterialQuantity, this.LOGGER));
			}
		}
	}

	/**
	 * Gets the required repair material list for fixing the damaged asset.
	 *
	 * @return the required repair material list
	 */
	private Vector<RepairMaterialInformation> getRequiredRepairMaterialList(){
		Vector<RepairMaterialInformation> result = new Vector<RepairMaterialInformation>();
		Vector<String> assetContentsNames = this.assetUnderRepair.getAssetContentsNames();
		for (String name : assetContentsNames)
		{
			Vector<RepairMaterialInformation> repairmaterials = this.repairMaterialsInformation.get(name);
			combineRepairMaterialInformationVectors(result, repairmaterials);
		}
		//at this point all of the required tools were added to result

		return result;
	}


	/**
	 * Sort required repair tools list. this will help avoiding deadlocks when runnable maintenance are requesting their needed repair tools from the warehouse
	 *
	 * @param unsortedList the unsorted list
	 */
	private void sortRequiredRepairToolsList(Vector<RepairToolInformation> unsortedList)
	{
		Collections.sort(unsortedList, new Comparator<RepairToolInformation>(){

			@Override
			public int compare(RepairToolInformation tool1, RepairToolInformation tool2) {
				
				String t1 = ((RepairToolInformation) tool1).getRepairToolName();
				String t2 = ((RepairToolInformation) tool2).getRepairToolName();
				
				return t1.compareToIgnoreCase(t2);
			}
			
		});
	}
	
	/** the thread's run method.
	 * @see java.lang.Runnable#run()
	 *  
	 * simulates a single maintenance personal repairing a single asset
	 */
	@Override
	public void run() {
		
		this.assetUnderRepair.shiftStatusToUnavailable();
		Vector<RepairToolInformation> requiredTools = getRequiredRepairToolsList();
		sortRequiredRepairToolsList(requiredTools);
		Vector<RepairMaterialInformation> requiredMaterials = getRequiredRepairMaterialList();
		
		/****************************************Maintenance taking all stuff*************************************/
		
		for (RepairMaterialInformation repairMaterialInformation : requiredMaterials)
		{
			RepairMaterial requestedMaterial = new RepairMaterial(repairMaterialInformation);
			this.warehouse.acquireMaterial(requestedMaterial);
		}
		
		this.simulationStatistics.addUsedMaterials(requiredMaterials);
		this.LOGGER.finest(Thread.currentThread().getName() + " acquired all of the materials he needs for repairing asset: " + this.assetUnderRepair.getName());

		for (RepairToolInformation repairToolInformation : requiredTools ){
			this.warehouse.acquireTool(repairToolInformation);
		}
		this.simulationStatistics.addUsedTools(requiredTools);
		this.LOGGER.finest(Thread.currentThread().getName() + " acquired all of the tools he needs for repairing asset: " + this.assetUnderRepair.getName());
		
		/****************************************waiting time of repairing and and resets health values to all asset contents*************************************/
		
		Double sleepTimeForRepair = this.assetUnderRepair.calculateRepairTimeForAllAssetContents();
		this.LOGGER.finer(Thread.currentThread().getName() + " will sleep " + sleepTimeForRepair + " while repairing asset: " + this.assetUnderRepair.getName());
		
		try {
			Thread.sleep(sleepTimeForRepair.longValue());
		} catch (InterruptedException e) {e.printStackTrace();	}
		
		this.assetUnderRepair.restoreHealthAfterRepair();
		
		this.LOGGER.info(Thread.currentThread().getName() + " finished repairing asset: " + this.assetUnderRepair.getName());

		for (RepairToolInformation repairToolInformation : requiredTools ){
			this.warehouse.releaseTool(repairToolInformation);
		}
		
		this.LOGGER.finer(Thread.currentThread().getName() + " released tools of asset: " + this.assetUnderRepair.getName());
		
		this.globallyAliveMaintenance.countDown();
		

	}

	

}
