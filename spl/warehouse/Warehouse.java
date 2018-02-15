package spl.warehouse;

import java.util.HashMap;

import spl.management.RepairToolInformation;

/**
 * The Class Warehouse.
 */
public class Warehouse implements IWarehouse {


	/** The repair materials pool. */
	protected HashMap<String, RepairMaterial> repairMaterials;

	/** The repair tools pool. */
	protected HashMap<String, RepairTool> repairTools;

	/** The tools lock, used for synchronizing the maintenance threads. */
	Object toolsLock;//sync lock for repairTools vector

	/** The materials lock, used for synchronizing the maintenance threads. */
	Object materialsLock;//sync lock for repairMaterials vector

	/**
	 * Instantiates a new warehouse.
	 */
	public Warehouse()
	{
		this.repairMaterials = new HashMap<String, RepairMaterial>();
		this.repairTools = new HashMap<String, RepairTool>();
		this.toolsLock = new Object();
		this.materialsLock = new Object();
	}


	/**
	 * Adds the tool to the tools pool.
	 *
	 * @param rt the repair tool
	 */
	public void addTool(RepairTool rt){
	
		this.repairTools.put(rt.getId(), rt);
	}


	/**
	 * Adds the material the materials pool.
	 *
	 * @param rm the repair material
	 */
	public void addMaterial(RepairMaterial rm){
		this.repairMaterials.put(rm.getId(), rm);
	}


	/** 
	 * @see spl.warehouse.IWarehouse#acquireTool(spl.warehouse.RepairTool)
	 */
	public boolean acquireTool( RepairToolInformation requiredTool){

		RepairTool toolInMap=searchToolInSet(requiredTool.getRepairToolName());//find reference to the required tool in map
		if (toolInMap == null)
		{
			return false;
		}
		try {
			toolInMap.quantity.acquire(requiredTool.getRepairToolQuantity());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}


	/** 
	 * @see spl.warehouse.IWarehouse#releaseTool(spl.warehouse.RepairTool)
	 */
	public boolean releaseTool(RepairToolInformation releasedTool) {
	
		RepairTool toolInMap=searchToolInSet(releasedTool.getRepairToolName());//find reference to the released tool in map
		toolInMap.quantity.release(releasedTool.getRepairToolQuantity());
	
	
		return true;
	
	}


	/** 
	 * @see spl.warehouse.IWarehouse#acquireMaterial(spl.warehouse.RepairMaterial)
	 */
	public boolean acquireMaterial( RepairMaterial requiredMaterial){
		synchronized (materialsLock) {
			RepairMaterial foundedMaterial=searchMaterialInSet(requiredMaterial.getId());
			if ( foundedMaterial != null && foundedMaterial.getQuantity()>=requiredMaterial.getQuantity()  ){

				foundedMaterial.setQuantity( (requiredMaterial.getQuantity())*(-1) );
				return true;

			}
		}
		return false;

	}

	/**
	 * Search tool in the tools pool.
	 *
	 * @param id the id
	 * @return the repair tool
	 */
	private RepairTool searchToolInSet(String id){
		RepairTool result = null;
		synchronized (toolsLock)
		{
			result=this.repairTools.get(id);
		}
		return result;
	
	}


	/**
	 * Search material in the materials pool.
	 *
	 * @param id the id
	 * @return the repair material
	 */
	private RepairMaterial searchMaterialInSet(String id){
		RepairMaterial result = null;
		synchronized (materialsLock)
		{
			result=this.repairMaterials.get(id);
		}
		return result;
	}



}
