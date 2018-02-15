package spl.warehouse;
import spl.management.RepairToolInformation;

/**
 * The Interface IWarehouse.
 */
public interface IWarehouse {
	
	
/**
	 * Acquire tool from the warehouse for repairing an asset content.
	 *
	 * @param requiredTool the required tool
	 * @return true, if successful
	 */
	boolean acquireTool( RepairToolInformation requiredTool);
	/**
	 * Release tool to the warehouse which was used for repairing an asset content.
	 *
	 * @param releasedTool the released tool
	 * @return true, if successful
	 */
	boolean releaseTool( RepairToolInformation releasedTool);

	/**
	 * Acquire material from the warehouse for repairing an asset content.
	 *
	 * @param requiredMaterial the required material
	 * @return true, if successful
	 */
	boolean acquireMaterial( RepairMaterial requiredMaterial); 
	

}
