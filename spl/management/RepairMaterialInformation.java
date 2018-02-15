package spl.management;
import java.util.logging.Logger;


/**
 * The Class RepairMaterialInformation.
 * 
 * "This object will hold information of a single repair material type."
 */
public class RepairMaterialInformation {

	/** The material name. */
	private String name;
	
	/** The material's quantity. */
	private int quantity;
	
	
	/**
	 * Instantiates a new repair material information.
	 *
	 * @param materialName the material's name
	 * @param quantity the material's quantity
	 * @param LOGGER the logger
	 */
	public RepairMaterialInformation(String materialName, int quantity, Logger LOGGER) {
		this.name=materialName;
		this.quantity=quantity;
	}
	
	/**
	 * Gets the repair material name.
	 *
	 * @return the repair material name
	 */
	public String getRepairMaterialName(){
		return name;
	}
	
	/**
	 * Gets the repair material quantity.
	 *
	 * @return the material tool quantity
	 */
	public int getMaterialQuantity(){
		return quantity;
	}
	
	/**
	 * Increase Material's quantity.
	 *
	 * @param addedQuantity the added quantity
	 */
	public void increaseQuantity(int addedQuantity)
	{
		this.quantity += addedQuantity;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "name: " + this.name + " quantity: " + this.quantity;
	}
}
