package spl.assets;
import java.util.Vector;
import java.util.logging.Logger;

import spl.warehouse.RepairMaterial;
import spl.warehouse.RepairTool;


/**
 * The Class AssetContent which represents an asset's asset content(TV, sofa, etc.).
 * 
 * "Asset content is an object which holds the details of one item found in the asset. An asset typically has collection of these items."
 */
public class AssetContent {
	
	/** The asset content's name. */
	private String name;
	
	/** The asset content's health. */
	private double health;
	
	/** The asset content's repair cost multiplier.
	 * 	used for  calculating the required time for repairing this asset content*/
	private double repairCostMultiplier;
	
	/** The tools that are required for repairing this asset content. */
	private Vector<RepairTool> requiredRepairTools;
	
	/** The materials that are required for repairing this asset content. */
	private Vector<RepairMaterial> requiredRepairMaterial;
	
	/** The logger. */
	private Logger LOGGER;
	
	/**
	 * Instantiates a new asset content.
	 *
	 * @param name asset content's name
	 * @param repairCostMultiplier the repair cost multiplier
	 * @param LOGGER the logger
	 */
	public AssetContent(String name, double repairCostMultiplier, Logger LOGGER)
	{
		this.name = name;
		this.health = 100;
		this.repairCostMultiplier = repairCostMultiplier;
		this.requiredRepairTools = new Vector<RepairTool>();
		this.requiredRepairMaterial = new Vector<RepairMaterial>();
		this.LOGGER = LOGGER;
		LOGGER.finest("new AssetContent: " + this.name + " " + this.repairCostMultiplier);
	}
	
	/**
	 * Adds a repair tool.
	 * used for populating the warehouse while reading the input files
	 *
	 * @param addedRepairTool the added repair tool
	 */
	public void addRepairTool(RepairTool addedRepairTool)
	{
		this.requiredRepairTools.addElement(addedRepairTool);
	}
	
	/**
	 * Adds a repair material.
	 * used for populating the warehouse while reading the input files
	 *
	 * @param addedRepairMaterial the added repair material
	 */
	public void addREpairMaterial(RepairMaterial addedRepairMaterial)
	{
		this.requiredRepairMaterial.addElement(addedRepairMaterial);
	}

	/**
	 * applies a damage (due to customer stay) on this asset content's health
	 *
	 * @param appliedDamage the applied damage
	 */
	public void applyDamage(double appliedDamage) {
		this.health = this.health - appliedDamage;
		if (this.health < 0)
			this.health = 0;
		this.LOGGER.finest(this.name + " health was reduced to " + this.health);
		
	}
	
	/**
	 * Gets the asset content's name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Calculates repair time for this asset content based on it's health and repair cost multiplier. formula: <b>(100-Health)*Repair Cost Multiplier.</b>
	 *
	 * @return the double
	 */
	public double calculateRepairTime()
	{
		return ((100 - this.health) * this.repairCostMultiplier);
	}
	
	/**
	 * Restore the asset content's health a maintenance after repair.
	 */
	public void restoreHealthAfterRepair()
	{
		this.health = 100;
	}
	
	
	

}
